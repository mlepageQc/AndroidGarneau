package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URLDecoder;
import java.util.List;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Demande;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Offre;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data_sources.OffreDataSource;

public class ConsultationDemande extends ActionBarActivity implements Button.OnClickListener {

    //Attributs de la classe
    private Demande demandeCourante;
    private Button btnAccepter;
    private Button btnRefuser;
    private TextView lblUsername;
    private TextView lblTitre;
    private String toastMessage = "";
    private OffreDataSource m_OffreDataSource;
    private boolean accepter = false;

    //Attributs pour le service web (récupération de l'utilisateur)
    private final String TAG = this.getClass().getSimpleName();
    private final static String WEB_SERVICE_URL = "carpoolxpress-1138.appspot.com";
    private final static String REST_DEMANDES = "/demande";
    private HttpClient m_ClientHttp = new DefaultHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_demande);

        //Récupération des contrôles
        btnAccepter = (Button) findViewById(R.id.btn_accepter_demande);
        btnRefuser = (Button) findViewById(R.id.btn_refuser_demande);
        lblUsername = (TextView) findViewById(R.id.lbl_username);

        //Base de données locale
        m_OffreDataSource = new OffreDataSource(this);

        //Clique events sur les boutons
        btnAccepter.setOnClickListener(this);
        btnRefuser.setOnClickListener(this);

        demandeCourante = (Demande)getIntent().getSerializableExtra("demandeCourante");

        lblUsername.setText(demandeCourante.getUsername());

        setTitle(demandeCourante.getTitre());
    }

    @Override
    public void onStart() {
        super.onStart();
        m_OffreDataSource.open();
    }

    @Override
    public void onResume() {
        super.onStart();
        m_OffreDataSource.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        m_OffreDataSource.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        m_OffreDataSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_consultation_demande, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_deconnexion) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            Toast.makeText(getApplicationContext(), "Déconnexion réussie.", Toast.LENGTH_SHORT).show();
        }
        if(id == R.id.home) {
            this.finish();
        }
        if(id == R.id.mes_offres) {
            Intent i = new Intent(this, DepartsConducteur.class);
            startActivity(i);
        }
        if(id == R.id.mes_demandes) {
            Intent i = new Intent(this, ListeDemande.class);
            startActivity(i);
        }
        if(id == R.id.aide_interactive_consultation_demande) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aide");
            builder.setMessage("Fenêtre de consultation d'une demande : " +
                    "Sur cette fenêtre, vous pouvez consulter une demande que vous avez reçue.");
            builder.setCancelable(true);
            builder.setNeutralButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        //Acceptation ou refus de la demande
        switch (v.getId()) {
            case R.id.btn_accepter_demande:
                toastMessage = getString(R.string.confirmation_demande);
                accepter = true;
                new DeleteDemandeTask().execute((Void)null);
                break;

            case R.id.btn_refuser_demande:
                toastMessage = getString(R.string.refus_demande);
                accepter = false;
                new DeleteDemandeTask().execute((Void)null);
                break;
        }
    }

    //Suppression d'une demande
    private class DeleteDemandeTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void ... unused) {
            try {
                //Construction de l'uri en fonction de si on accepte on refuse l'offre
                Uri uri = null;

                if (accepter) {
                    uri = new Uri.Builder()
                            .scheme("http")
                            .authority(Uri.decode(WEB_SERVICE_URL))
                            .path(REST_DEMANDES + "/" + String.valueOf(demandeCourante.getIdDemande()) + "/accepter")
                            .build();
                } else {
                    uri = new Uri.Builder()
                            .scheme("http")
                            .authority(Uri.decode(WEB_SERVICE_URL))
                            .path(REST_DEMANDES + "/" + String.valueOf(demandeCourante.getIdDemande()))
                            .build();
                }

                String uriString = URLDecoder.decode(uri.toString(), "UTF-8");
                HttpDelete deleteMethod = new HttpDelete(uriString);

                deleteMethod.addHeader("Content-Type", "application/json");

                m_ClientHttp.execute(deleteMethod, new BasicResponseHandler());
                Log.i(TAG, "Delete terminé");

            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (m_Exp == null) {
                List<Offre> lesOffres = m_OffreDataSource.getAllOffres();

                Offre offreCourante = null;
                int i = 0;
                Boolean trouve = false;

                while (!trouve && i < lesOffres.size()){
                    if (lesOffres.get(i).getIdOffre().equals(demandeCourante.getIdOffre())){
                        trouve = true;
                        offreCourante = lesOffres.get(i);
                    }

                    i++;
                }

                //Construction de la string de passagers stockée en local
                if (offreCourante.getPassagers().equals("")) {
                    offreCourante.setPassagers(demandeCourante.getUsername());
                } else {
                    offreCourante.setPassagers(offreCourante.getPassagers() + ", " + demandeCourante.getUsername());
                }

                m_OffreDataSource.update(offreCourante);

                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
                Intent intentDemande = new Intent(getApplicationContext(), ListeDemande.class);
                startActivity(intentDemande);

            } else {
                Log.e(TAG, "Error while posting", m_Exp);
                Toast.makeText(getApplicationContext(), getString(R.string.erreur_suppression_demande), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), ListeDemande.class);
                startActivity(i);
            }
        }
    }

}
