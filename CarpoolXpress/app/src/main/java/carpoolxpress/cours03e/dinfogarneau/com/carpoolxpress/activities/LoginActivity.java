package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Utilisateur;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data_sources.UtilisateurDataSource;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json.UtilisateurJsonParser;


public class LoginActivity extends ActionBarActivity{

    private EditText txtUtilisateur;
    private EditText txtPassword;
    private TextView txtValidation;
    private UtilisateurDataSource m_UtilisateurDataSource;
    private static final String MyPREFERENCES = "MyPrefs";

    //Attributs pour le service web (récupération de l'utilisateur)
    private final String TAG = this.getClass().getSimpleName();
    private final static String WEB_SERVICE_URL = "carpoolxpress-1138.appspot.com";
    private final static String REST_UTILISATEUR = "/connexion";
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    private Utilisateur m_Utilisateur = new Utilisateur();

    private SharedPreferences sp;

    Utilisateur unUserConnecte = new Utilisateur();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btnConnexion = (Button) this.findViewById(R.id.btnConnexion);
        Button btnInscription = (Button) this.findViewById(R.id.btnInscription);
        txtUtilisateur = (EditText)this.findViewById(R.id.txt_Utilisateur);
        txtPassword = (EditText)this.findViewById(R.id.txt_Motdepasse);
        txtValidation = (TextView)this.findViewById(R.id.txt_Validation);

        /**SESSION*/
        sp = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    //À la création de l'activité...
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.aide_interactive_login){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aide");
            builder.setMessage("Fenêtre Login : " +
                    "Sur cette fenêtre, vous pouvez vous connecter à un compte déjà existant, en inscrivant vos informations et en cliquant sur Connexion, si vous n'avez pas de compte, veuillez cliquer sur le bouton Inscription.");
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

    public void onClickConnexion(View v) {
        if (txtUtilisateur.getText().equals("") || txtPassword.getText().equals(""))
        {
            Toast.makeText(getApplicationContext(), "Entrez vos informations.", Toast.LENGTH_LONG).show();
        }
        else
        {
            new DownloadUtilisateurInfos().execute((Void)null);
        }
    }
    public void onClickInscription(View v) {
        Intent intent = new Intent(this, InscriptionActivity.class);
        this.startActivity(intent);
    }
    //Vérifie les informations de connexion et confirme la connexion si elles sont valides
    private class DownloadUtilisateurInfos extends AsyncTask<Void, Void, Utilisateur> {
        Exception m_Exp;

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Utilisateur doInBackground(Void... unused) {
            Utilisateur utilisateur = null;

            try {
                //Construction de l'URI pour vérifier les informations de connexion entrées
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority(Uri.decode(WEB_SERVICE_URL))
                        .path(REST_UTILISATEUR)
                        .build();

                String username = txtUtilisateur.getText().toString();
                String password = txtPassword.getText().toString();

                //Création de la requête Http avec l'URI construit
                HttpPost postMethod = new HttpPost(uri.toString());
                JSONObject obj = UtilisateurJsonParser.loginInfosToJson(username, password);
                postMethod.setEntity(new StringEntity(obj.toString()));

                //Exécution de la requête Http
                String body = m_ClientHttp.execute(postMethod, new BasicResponseHandler());
                utilisateur = UtilisateurJsonParser.parseUtilisateur(body);

            } catch (Exception e) {
                m_Exp = e;
            }
            return utilisateur;
        }

        @Override
        protected void onPostExecute(Utilisateur p_Util) {
            setProgressBarIndeterminateVisibility(false);

            //Ajout des informations de l'utilisateur connecté aux préférences partagées de l'application
            if (m_Exp == null && p_Util != null) {
                m_Utilisateur = p_Util;

                SharedPreferences.Editor editor = sp.edit();

                editor.putInt("idUtilisateur", m_Utilisateur.getId());
                editor.putString("Utilisateur", m_Utilisateur.getUsername().toString());
                editor.putString("Nom", m_Utilisateur.getNom().toString());
                editor.putString("Prenom", m_Utilisateur.getPrenom().toString());
                editor.commit();

                Toast.makeText(getApplicationContext(), "Bienvenue " + unUserConnecte.getNom(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                LoginActivity.this.startActivity(intent);

            } else {
                Log.e(TAG, "Erreur lors de la connexion", m_Exp);
                Toast.makeText(LoginActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
