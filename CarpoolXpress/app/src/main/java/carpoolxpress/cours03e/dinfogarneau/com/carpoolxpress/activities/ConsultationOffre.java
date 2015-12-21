package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.util.ArrayList;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.alarm_manager.AlarmReceiver;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.alarm_manager.BootReceiver;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Demande;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Offre;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json.DemandeJsonParser;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json.OffreJsonParser;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.utils.Haversine;


public class ConsultationOffre extends ActionBarActivity implements Button.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapLongClickListener {

    //Attributs de la classe
    private Offre offreCourante;
    private Demande nouvelleDemande;
    private TextView txtDate;
    private TextView txtHeure;
    private TextView txtNbPlaces;
    private TextView txtDistance;
    private TextView lblPassagers;
    private TextView lblNomsPassagers;
    private Button btnEnvoiDemande;
    private android.support.v7.app.ActionBar actionBar;
    private ArrayList<Uri> listUri = new ArrayList<Uri>();

    private LatLng coordDepart = null;
    private LatLng coordArrivee = null;

    private GoogleMap mMap;
    private SupportMapFragment fragment;
    private GoogleApiClient mGoogleApiClient;

    //Attributs pour le service web (récupération de l'utilisateur)
    private final String TAG = this.getClass().getSimpleName();
    private final static String WEB_SERVICE_URL = "carpoolxpress-1138.appspot.com";
    private final static String REST_DEMANDES = "/demande";
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    private Uri uri;

    //Attributs pour la connexion internet
    ConnectivityManager connManager;
    NetworkInfo mWifi;
    NetworkInfo m3g;

    IntentFilter intentFilter;

    //Attributs pour le broadcast
    private AlarmManager alarmMgr;
    private Intent alarmIntent;
    public final static int ID_ALARM = 12345;
    public static int INTERVAL_ALARM = 7000;

    //Préférences partagées
    private SharedPreferences sp;

    // Identifiant unique pour la notification.
    private static final int ID_NOTIF = 12345;

    // Clé pour l'information attachée à l'intention.
    public static final String EXTRA_INFO = "demande";

    // Gestionnaire de notifications.
    private NotificationManager notifMgr;

    // Pour récupérer le texte de la notification.
    private EditText txtNotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_offre);

        //Récupération des contrôles
        txtDate = (TextView) this.findViewById(R.id.lbl_date_offre);
        txtHeure = (TextView) this.findViewById(R.id.lbl_heure_offre);
        txtNbPlaces = (TextView) this.findViewById(R.id.lbl_nb_places);
        btnEnvoiDemande = (Button) this.findViewById(R.id.btn_envoi_demande);
        txtDistance = (TextView) this.findViewById(R.id.txt_distance);
        lblPassagers = (TextView) this.findViewById(R.id.lbl_passagers);
        lblNomsPassagers = (TextView) this.findViewById(R.id.lbl_noms_passagers);

        actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.departs_titre);

        actionBar.setDisplayHomeAsUpEnabled(true);

        //Offre récupérée
        offreCourante = (Offre) getIntent().getSerializableExtra("offreCourante");

        //Récupération des coordonnées de l'offre
        String[] coordOffreDepart = offreCourante.getPointDepart().split(",");
        String[] coordOffreArrivee = offreCourante.getPointArrivee().split(",");

        coordDepart = new LatLng(Double.parseDouble(coordOffreDepart[0]), Double.parseDouble(coordOffreDepart[1]));
        coordArrivee = new LatLng(Double.parseDouble(coordOffreArrivee[0]), Double.parseDouble(coordOffreArrivee[1]));

        double distance = Haversine.distance(Double.parseDouble(coordOffreDepart[0]), Double.parseDouble(coordOffreDepart[1]), Double.parseDouble(coordOffreArrivee[0]), Double.parseDouble(coordOffreArrivee[1]));

        txtDistance.setText(String.valueOf(distance) + " km");

        //Remplissage et configuration des contrôles
        configurerControles();

        //Récupération du "NotificationManager".
        this.notifMgr = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        //Récupération des préférences partagées
        sp = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        //On cache le bouton de demande et les labels des passagers si c'est l'offre de l'utilisateur courant
        if (offreCourante.getUsername().equals(sp.getString("Utilisateur", ""))) {
            btnEnvoiDemande.setVisibility(View.GONE);

            if (offreCourante.getPassagers().equals("")) {
                lblNomsPassagers.setText(R.string.no_passagers);
            } else {
                lblNomsPassagers.setText(offreCourante.getPassagers());
            }
        } else {
            lblPassagers.setVisibility(View.GONE);
            lblNomsPassagers.setVisibility(View.GONE);
        }

        //Gestion du fragmentManager pour la carte
        FragmentManager fm = getSupportFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }

        // Création de l'intention pour le déclenchement de l'alarme.
        this.alarmIntent = new Intent(this, AlarmReceiver.class);

        // Récupération du gestionnaire d'alarme.
        this.alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);


        intentFilter = new IntentFilter("carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.USER_ACTION");
        buildGoogleApiClient();
        setUpMapIfNeeded();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.registerReceiver(this.br, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterReceiver(this.br);
    }

    private void configurerControles() {
        txtDate.setText(offreCourante.getDate());
        txtHeure.setText(offreCourante.getHeure());
        txtNbPlaces.setText(String.valueOf(offreCourante.getNbPlaces()));
        btnEnvoiDemande.setOnClickListener(this);
    }

    private void setUpMapIfNeeded() {
        //Carte google map
        if (mMap == null) {
            //Récupération de la carte
            mMap = fragment.getMap();
            //Configuration de la carte
            setUpMap();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMyLocationEnabled(true);

        CameraUpdate center;

        mMap.addMarker(new MarkerOptions().position(this.coordDepart).title(("Départ")));
        mMap.addMarker(new MarkerOptions().position(this.coordArrivee).title(("Arrivée"))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        center = CameraUpdateFactory.newLatLng(new LatLng(46.7755482,
                -71.2954967));

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_consultation_offre, menu);
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
        if(id == R.id.aide_interactive_consultation_offre) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aide");
            builder.setMessage("Fenêtre de consultation d'un offre : " +
                    "Sur cette fenêtre, vous pouvez consulter un offre que vous avez choisit.");
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
        switch (v.getId()) {
            case R.id.btn_envoi_demande:
                //Création de la nouvelle demande
                String test = sp.getString("Utilisateur", "");
                nouvelleDemande = new Demande(sp.getString("Utilisateur", ""), offreCourante.getIdOffre(), offreCourante.getTitre(), false);

                //Construction de l'Uri
                uri = new Uri.Builder()
                        .scheme("http")
                        .authority(WEB_SERVICE_URL)
                        .path(REST_DEMANDES)
                        .build();

                //Connecté à internet?
                //if (mWifi.isConnected() || m3g.isConnected()) {
                    //Requête serveur pour l'ajout de l'offre
                new PostNewDemandeTask().execute((Void)null);
                //}
                //else {
                //    listUri.add(uri);
                //}

                //gererEnvoyerNotif(v);
                break;
        }
    }

    private class PostNewDemandeTask extends AsyncTask<Void, Void, Demande> {
        Exception m_Exp;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Demande doInBackground(Void... unused) {
            Demande uneDemande = null;

            try {
                HttpPost postMethod = new HttpPost(uri.toString());
                JSONObject obj = DemandeJsonParser.ToJSONObject(nouvelleDemande);
                postMethod.setEntity(new StringEntity(obj.toString()));
                postMethod.addHeader("Content-Type", "application/json");

                String body = m_ClientHttp.execute(postMethod, new BasicResponseHandler());
                uneDemande = DemandeJsonParser.parseDemande(body);
                Log.i(TAG, "Post terminé");

            } catch (Exception e) {
                m_Exp = e;
            }
            return uneDemande;
        }

        @Override
        protected void onPostExecute(Demande p_Demande) {

            if (m_Exp == null) {
                Toast.makeText(ConsultationOffre.this, getString(R.string.succes_envoi_demande), Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Error while posting", m_Exp);
                Toast.makeText(ConsultationOffre.this, getString(R.string.erreur_envoi_demande), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void activerAlarme(View source) {

        //Intention en suspens
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, ID_ALARM, this.alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        //Activation de l'alarme
        this.alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 0, INTERVAL_ALARM, alarmPendingIntent);

        // Activation du BroadcastReceiver
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mWifi.isConnected() || m3g.isConnected()) {
                for (int i=0; i < listUri.size(); i++) {
                    uri = listUri.get(i);
                    new PostNewDemandeTask().execute((Void)null);
                }
            }
        }
    };

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }


}
