package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.net.URLDecoder;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Utilisateur;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data_sources.OffreDataSource;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data_sources.UtilisateurDataSource;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json.UtilisateurJsonParser;


public class InscriptionActivity extends ActionBarActivity {

    private UtilisateurDataSource m_UtilisateurDataSource;
    /**
     * Déclaration des EDIT TEXT
     * */

    private EditText txtUtilisateur;
    private EditText txtPassword;
    private EditText txtAdresse;
    private EditText txtNom;
    private EditText txtPrenom;

    Utilisateur m_utilisateur;

    private final String TAG = this.getClass().getSimpleName();
    private final static String WEB_SERVICE_URL = "carpoolxpress-1138.appspot.com";
    private final static String REST_OFFRES = "/utilisateur";

    private HttpClient m_ClientHttp = new DefaultHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        txtUtilisateur = (EditText)this.findViewById(R.id.txt_InscriptionUtilisateur);
        txtPassword = (EditText)this.findViewById(R.id.txt_InscriptionMotdePasse);
        txtAdresse = (EditText)this.findViewById(R.id.txt_InscriptionAdresse);
        txtNom = (EditText)this.findViewById(R.id.txt_InscriptionNom);
        txtPrenom = (EditText)this.findViewById(R.id.txt_InscriptionPrenom);

        this.m_UtilisateurDataSource = new UtilisateurDataSource(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inscription, menu);
        return true;
    }

    @Override
    public void onStart() {
       super.onStart();
       m_UtilisateurDataSource.open();
    }

    @Override
    public void onResume() {
        super.onResume();
        m_UtilisateurDataSource.open();
    }

    @Override
    public void onStop(){
        super.onStop();
        m_UtilisateurDataSource.close();
    }

    @Override
    public void onPause(){
        super.onPause();
        m_UtilisateurDataSource.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickSignUp(View v) {
        m_utilisateur = new Utilisateur(txtUtilisateur.getText().toString(), txtPassword.getText().toString(), txtAdresse.getText().toString(), txtNom.getText().toString(), txtPrenom.getText().toString());
        new PostNewUtilisateurTask().execute((Void) null);
    }

    private class PostNewUtilisateurTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void ... unused) {
            try {
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority(Uri.decode(WEB_SERVICE_URL))
                        .path(REST_OFFRES)
                        .build();

                String uriString = URLDecoder.decode(uri.toString(), "UTF-8");

                HttpPost postMethod = new HttpPost(uriString);
                JSONObject obj = UtilisateurJsonParser.ToJSONObject(m_utilisateur);
                postMethod.setEntity(new StringEntity(obj.toString()));
                postMethod.addHeader("Content-Type", "application/json");

                m_ClientHttp.execute(postMethod, new BasicResponseHandler());
                Log.i(TAG, "Post terminé");

            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (m_Exp == null) {
                m_UtilisateurDataSource.insert(m_utilisateur);
                Utilisateur utilisateurCreated = m_UtilisateurDataSource.getUtilisateur(m_utilisateur.getUsername(), m_utilisateur.getPassword());
                m_UtilisateurDataSource.close();
                Toast.makeText(getApplicationContext(), "Compte créé avec succès", Toast.LENGTH_LONG).show();

                Intent i = new Intent(InscriptionActivity.this, LoginActivity.class);
                startActivity(i);
            } else {
                Log.e(TAG, "Error while posting", m_Exp);
                Toast.makeText(getApplicationContext(), getString(R.string.erreur_ajout), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
