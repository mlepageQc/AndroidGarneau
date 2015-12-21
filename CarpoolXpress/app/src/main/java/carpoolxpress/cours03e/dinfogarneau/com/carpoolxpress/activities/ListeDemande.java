package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.util.ArrayList;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.alarm_manager.BootReceiver;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Demande;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json.DemandeJsonParser;

public class ListeDemande extends ActionBarActivity implements ListView.OnItemClickListener {

    //Attributs de la classe
    private ListView demandes;
    private ArrayList<Demande> lstDemande;
    private LigneAdapter m_Adapter;
    private Demande demandeCourante;
    private String toastMessage;

    //Attributs pour le service web (récupération de l'utilisateur)
    private final String TAG = this.getClass().getSimpleName();
    private final static String WEB_SERVICE_URL = "carpoolxpress-1138.appspot.com";
    private final static String REST_DEMANDES = "/demande";
    private HttpClient m_ClientHttp = new DefaultHttpClient();

    //Préférences partagées
    private SharedPreferences sp;

    private IntentFilter intentFilter;
    private BootReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_demande);

        lstDemande = new ArrayList<Demande>();
        this.demandes = (ListView)this.findViewById(R.id.list_view_demandes);

        demandes.setOnItemClickListener(this);

        //Récupération des préférences partagées
        sp = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

    }

    //Menu contextuel
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Menu");
        menu.add(0, v.getId(), 0, "Accepter");
        menu.add(0, v.getId(), 0, "Refuser");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Chargement asynchrone de la liste des personnes.
        new DownloadPersonListTask().execute((Void)null);
    }

    //Peut-etre la méthode asynchrone ici, pas sur

    protected void onStop() {
        super.onStop();

        // Désactivation DYNAMIQUE du récepteur
        //unregisterReceiver(receiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_liste_demande, menu);
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
        if(id == R.id.aide_interactive_liste_demande) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aide");
            builder.setMessage("Fenêtre de la liste des demandes : " +
                    "Sur cette fenêtre, vous pouvez consulter toutes les demandes que vous avez reçues.");
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        demandeCourante = lstDemande.get(position);

        Intent i = new Intent(this, ConsultationDemande.class);
        i.putExtra("demandeCourante", demandeCourante);
        startActivity(i);
    }

    private class DownloadPersonListTask extends AsyncTask<Void, Void, ArrayList<Demande>> {
        Exception m_Exp;

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected ArrayList<Demande> doInBackground(Void ... unused) {
            ArrayList<Demande> liste = null;
            String username = sp.getString("Utilisateur", "");

            try {
                URI uri = new URI("http", WEB_SERVICE_URL, REST_DEMANDES + "/" + username,  null);
                HttpGet getMethod = new HttpGet(uri);

                String body = m_ClientHttp.execute(getMethod, new BasicResponseHandler());
                Log.i(TAG, "Reçu : " + body);

                liste = DemandeJsonParser.parseListeDemandes(body);
            } catch (Exception e) {
                m_Exp = e;
            }
            return liste;
        }

        @Override
        protected void onPostExecute(ArrayList<Demande> p_Demandes) {
            setProgressBarIndeterminateVisibility(false);

            if (m_Exp == null && p_Demandes != null) {
                lstDemande.clear();
                lstDemande.addAll(p_Demandes);

                m_Adapter = new LigneAdapter();
                demandes.setAdapter(m_Adapter);
                m_Adapter.notifyDataSetChanged();

            } else {
                Log.e(TAG, "Error while fetching", m_Exp);
                Toast.makeText(ListeDemande.this, getString(R.string.erreur_demandes), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Adapteur de la liste de demandes
    private class LigneAdapter extends ArrayAdapter<Demande> {
        public LigneAdapter() {
            super(ListeDemande.this, android.R.layout.simple_list_item_1, lstDemande);
        }

        @Override
        public View getView(int position, View vrow, ViewGroup list) {
            View row = super.getView(position, vrow, list);

            Demande d = lstDemande.get(position);
            TextView label = (TextView) row.findViewById(android.R.id.text1);
            label.setText(String.valueOf(position + 1) + " - " + d.getTitre());
            return row;
        }
    }
}
