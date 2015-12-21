package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.alarm_manager;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities.DepartsConducteur;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities.ListeDemande;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Demande;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json.DemandeJsonParser;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json.OffreJsonParser;


public class AlarmService extends IntentService {

    //Attributs de la classe
    private Demande demandeCourante;

    //Attributs pour le service web
    private final String TAG = this.getClass().getSimpleName();
    private final static String WEB_SERVICE_URL = "carpoolxpress-1138.appspot.com";
    private final static String REST_DEMANDE = "/demande";
    private HttpClient m_ClientHttp = new DefaultHttpClient();

    private String username;

    private ArrayList<Demande> lesDemandes;
    private static final int ID_NOTIF = 12345;

    //Préférences partagées
    private SharedPreferences sp;

    // Gestionnaire de notifications.
    private NotificationManager notifMgr;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //On envoie le user Id
        sp = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        username = sp.getString("Utilisateur", "");
        new DownloadListeDemandeTask().execute((Void)null);
    }

    private void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        // méthode anonyme du timer passée au handler
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            DownloadListeDemandeTask performBackgroundTask = new DownloadListeDemandeTask();
                            // PerformBackgroundTask doit être sous-classe d'AsynchTask
                            performBackgroundTask.execute((Void) null);
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 50000); //toutes les 50000 ms
    }



    private class DownloadListeDemandeTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;
        String id;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void ... unused) {
            try {

                URI uri = new URI("http", WEB_SERVICE_URL, REST_DEMANDE + "/" + username, null, null);
                HttpGet getMethod = new HttpGet(uri);

                String body = m_ClientHttp.execute(getMethod, new BasicResponseHandler());

                lesDemandes = DemandeJsonParser.parseListeDemandes(body);

            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {

            if (m_Exp == null) {
                for (int i=0; i < lesDemandes.size(); i ++) {
                    if(!lesDemandes.get(i).getNotifie()) {
                        envoyerNotifs(lesDemandes.get(i));
                        demandeCourante = lesDemandes.get(i);
                        lesDemandes.get(i).setNotifie(true);
                        new PutDemandeTask(lesDemandes.get(i).getIdDemande()).execute((Void) null);
                    }
                }
            }
        }
    }

    public void envoyerNotifs(Demande demande) {
        // Récupération du "NotificationManager".
        this.notifMgr = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        String statusBarNotif = this.getString(R.string.statusBarNotif);
        String titreNotif = demande.getTitre();
        String texteNotif = demande.getUsername() + " a fait une demande pour l'offre " + demande.getTitre() + "!";

        // Création d'un nouvelle notification.
        Notification notif = new Notification(R.drawable.ic_launcher, statusBarNotif, System.currentTimeMillis());

        // Pour faire disparaître la notification lorsque l'utilisateur la clique.
        notif.flags |= Notification.FLAG_AUTO_CANCEL;

        // Création d'une intention de retour lorsqu'on clique sur la notification.
        Intent i = new Intent(this, ListeDemande.class);
        // Création d'une nouvelle intention en suspens.
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // Configuration de la notification.
        notif.setLatestEventInfo(this, titreNotif, texteNotif, pi);
        // Envoie de la notification.
        this.notifMgr.notify(ID_NOTIF, notif);
    }

    private class PutDemandeTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;
        String idDemande;

        private PutDemandeTask(String p_idDemande) {
            this.idDemande = p_idDemande;
        }

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
                        .path(REST_DEMANDE + "/" + idDemande)
                        .build();

                //Exécution de la requête
                HttpPut putMethod = new HttpPut(uri.toString());
                JSONObject obj = DemandeJsonParser.ToJSONObject(demandeCourante);
                putMethod.setEntity(new StringEntity(obj.toString()));
                putMethod.addHeader("Content-Type", "application/json");

                m_ClientHttp.execute(putMethod, new BasicResponseHandler());
                Log.i(TAG, "Post terminé");

            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {

        }
    }

}
