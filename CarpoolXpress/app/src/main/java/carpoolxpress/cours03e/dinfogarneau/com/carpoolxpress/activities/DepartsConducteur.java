package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URLDecoder;
import java.util.List;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Offre;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data_sources.OffreDataSource;

public class DepartsConducteur extends ActionBarActivity implements ListView.OnItemClickListener {

    //Attributs de la classe
    private ListView departs;
    private android.support.v7.app.ActionBar actionBar;
    private OffreDataSource m_OffreDataSource;
    private List<Offre> lstOffres;
    private LigneAdapter m_Adapter;
    private Offre offreASupprimer;

    //Attributs pour le service web
    private final String TAG = this.getClass().getSimpleName();
    private final static String WEB_SERVICE_URL = "carpoolxpress-1138.appspot.com";
    private final static String REST_OFFRES = "/offre";
    private HttpClient m_ClientHttp = new DefaultHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_departs_conducteur);

        this.m_OffreDataSource = new OffreDataSource(this);
        this.m_OffreDataSource.open();
        this.departs = (ListView)this.findViewById(R.id.list_view_departs);

        this.resetUI();

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.departs_titre);

        //Actions sur les cliques de la liste
        registerForContextMenu(departs);
        departs.setOnItemClickListener(this);

        actionBar.setDisplayHomeAsUpEnabled(true);

        String toastMessage = getIntent().getStringExtra("toast_message");

        if (toastMessage != "" && toastMessage != null) {
            Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
            toastMessage = "";
        }

        this.m_OffreDataSource.close();
    }

    private void resetUI() {
        // Récupération de toutes les personnes dans la BD.
        this.lstOffres = m_OffreDataSource.getAllOffres();
        this.m_Adapter = new LigneAdapter();
        departs.setAdapter(m_Adapter);
        this.m_Adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if(id == R.id.aide_interactive_depart_conducteur) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aide");
            builder.setMessage("Fenêtre Liste de Départ(s) : " +
                    "Sur cette fenêtre, vous pouvez consulter les départs que vous avez mis en ligne.  Les départs sont disponible aux utilisateurs désirant utiliser le service de covoiturage.  Pour Supprimer ou Modifier le départ, veuillez cliquer deux secondes sur un des départs.");
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

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Menu");
        menu.add(0, v.getId(), 0, "Supprimer");
        menu.add(0, v.getId(), 0, "Modifier");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        offreASupprimer = lstOffres.get(info.position);

        //Suppression de l'offre
        if(item.getTitle()=="Supprimer"){
            new DeleteOffreTask().execute((Void)null);
        }
        //Modification de l'offre
        else if(item.getTitle()=="Modifier"){
            redirectionModification(offreASupprimer.getId());
        }
        return true;
    }

    public void supprimerOffreLocal() {
        m_OffreDataSource.open();
        m_OffreDataSource.delete(offreASupprimer.getId());
        resetUI();
        Toast.makeText(getApplicationContext(), "Départ supprimé.", Toast.LENGTH_SHORT).show();
    }

    public void redirectionModification(int offre_id) {
        Intent i = new Intent(this, MainActivity.class);
        int fragmentPosition = 1;
        i.putExtra("fragmentPosition", fragmentPosition);
        i.putExtra("mode", R.string.modifier);
        i.putExtra("offreId", offre_id);
        startActivity(i);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(this, ConsultationOffre.class);
        i.putExtra("offreCourante", lstOffres.get(position));
        startActivity(i);
    }

    private class LigneAdapter extends ArrayAdapter<Offre> {
        public LigneAdapter() {
            super(DepartsConducteur.this, android.R.layout.simple_list_item_1, lstOffres);
        }

        @Override
        public View getView(int position, View vrow, ViewGroup list) {
            View row = super.getView(position, vrow, list);

            Offre o = lstOffres.get(position);
            TextView label = (TextView) row.findViewById(android.R.id.text1);
            label.setText(String.valueOf(position + 1) + " - " + o.getTitre());
            return row;
        }
    }

    //Suppression d'une offre
    private class DeleteOffreTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void ... unused) {
            try {
                //Construction de l'uri
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority(Uri.decode(WEB_SERVICE_URL))
                        .path(REST_OFFRES + "/" + String.valueOf(offreASupprimer.getIdOffre()))
                        .build();

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
                //Suppression locale si la suppression sur le service web a été concluante
                supprimerOffreLocal();
            } else {
                Log.e(TAG, "Error while posting", m_Exp);
                Toast.makeText(getApplicationContext(), getString(R.string.erreur_suppression), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
