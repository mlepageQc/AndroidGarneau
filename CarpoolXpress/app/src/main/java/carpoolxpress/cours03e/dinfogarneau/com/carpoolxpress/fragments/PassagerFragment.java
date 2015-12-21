package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities.RecherchePassagers;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Offre;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json.OffreJsonParser;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.utils.Haversine;

import java.util.ArrayList;

/**
 * Created by Maxime on 2015-09-09.
 */
public class PassagerFragment extends Fragment implements Button.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapLongClickListener{

    //Attributs du fragment
    private LinearLayout dateDialog;
    private LinearLayout timeDialog;
    private Button btnChoixDate;
    private Button btnRecherche;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private SupportMapFragment fragment;
    private TextView txtDistance;

    private boolean choixDepart;
    private LatLng coordDepart = null;
    private LatLng coordArrivee = null;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    //Paramètre pour la connexion Http
    private final String TAG = this.getClass().getSimpleName();
    private final static String WEB_SERVICE_URL = "carpoolxpress-1138.appspot.com";
    private final static String REST_OFFRES = "/offre";

    //Client Http pour la recherche
    private HttpClient m_ClientHttp = new DefaultHttpClient();

    //Préférences partagées
    private SharedPreferences sp;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle){

        LinearLayout passagerFragment = (LinearLayout)inflater.inflate(R.layout.passager_fragment, container, false);

        //Récupération des contrôles de la vue
        dateDialog = (LinearLayout)inflater.inflate(R.layout.dialog_date, null);
        timeDialog = (LinearLayout)inflater.inflate(R.layout.dialog_heure, null);

        btnChoixDate = (Button)passagerFragment.findViewById(R.id.btn_date_passager);
        btnRecherche = (Button)passagerFragment.findViewById(R.id.btn_recherche_passager);
        datePicker = (DatePicker)dateDialog.findViewById(R.id.date_picker);
        timePicker = (TimePicker)timeDialog.findViewById(R.id.time_picker);
        txtDistance = (TextView) passagerFragment.findViewById(R.id.txt_distance);

        btnChoixDate.setOnClickListener(this);
        btnRecherche.setOnClickListener(this);
        timePicker.setIs24HourView(true);

        sp = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        this.choixDepart = true;
        setMap();
        return passagerFragment;
    }

    public void setMap() {
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }

        buildGoogleApiClient();
        setUpMapIfNeeded();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setUpMapIfNeeded() {
        //Première instance de la carte
        if (mMap == null) {
            //Récupération de la carte
            mMap = fragment.getMap();
            mMap.setOnMapLongClickListener(this);
            //Configuration de la carte
            setUpMap();
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);

        CameraUpdate center;

        //Zoom sur la ville de Québec
        center = CameraUpdateFactory.newLatLng(new LatLng(46.7755482,
                -71.2954967));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.7755482,
                -71.2954967), 10));

    }

    @Override
    public void onClick(View v) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        switch(v.getId()) {

            //Choix de la date
            case R.id.btn_date_passager:
                AlertDialog.Builder dateBuilder = new AlertDialog.Builder(getActivity());

                dateBuilder.setTitle(R.string.titreDate)
                    .setView(dateDialog)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            btnChoixDate.setText(datePicker.getDayOfMonth() + " / " + datePicker.getMonth() + " / " + datePicker.getYear());
                        }
                     })
                     .setNegativeButton(android.R.string.cancel, null)
                    .show();
                break;

            //Recherche des offres selon les critères entrés
            case R.id.btn_recherche_passager:
                new DownloadOffreListTask().execute((Void)null);
                break;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if(this.choixDepart == true) {
            this.coordDepart = latLng;
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setTitle("Ajouter ce point de départ?")
                .setMessage(this.coordDepart.toString())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        choixDepart = false;
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(coordDepart).title("Départ"));
                        Toast.makeText(getActivity(), "Point de départ ajouté",
                                Toast.LENGTH_LONG).show();
                    }
                })
                .show();
        } else {
            this.coordArrivee = latLng;
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setTitle("Ajouter ce point d'arrivée?")
                .setMessage(this.coordDepart.toString())
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mMap.addMarker(new MarkerOptions().position(coordArrivee).title("Arrivée")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        Toast.makeText(getActivity(), "Point d'arrivée ajouté",
                                Toast.LENGTH_LONG).show();

                        double distance = Haversine.distance(coordDepart.latitude, coordDepart.longitude, coordArrivee.latitude, coordArrivee.longitude);

                        txtDistance.setText(String.valueOf(distance) + " km");

                        choixDepart = true;
                    }
                })
                .show();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //Requête pour la recherche d'offres
    private class DownloadOffreListTask extends AsyncTask<Void, Void, ArrayList<Offre>> {
        Exception m_Exp;

        @Override
        protected void onPreExecute() {
            getActivity().setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected ArrayList<Offre> doInBackground(Void ... unused) {
            ArrayList<Offre> liste = null;

            try {

                String dateOffre = btnChoixDate.getText().toString();
                dateOffre = dateOffre.replaceAll(" ", "");

                String coordDepartRecherche = String.valueOf(coordDepart.latitude) + ',' + String.valueOf(coordDepart.longitude);
                String coordArriveeRecherche = String.valueOf(coordArrivee.latitude) + ',' + String.valueOf(coordArrivee.longitude);

                URI uri = new URI("http", WEB_SERVICE_URL, REST_OFFRES + "/" + sp.getString("Utilisateur", ""), "coordDepart=" + coordDepartRecherche + "&coordArrivee=" + coordArriveeRecherche + "&date=" + dateOffre + "&heure=" + "", null);

                //Exécution de la requête Http
                HttpGet getMethod = new HttpGet(uri);
                String body = m_ClientHttp.execute(getMethod, new BasicResponseHandler());
                Log.i(TAG, "Reçu : " + body);

                liste = OffreJsonParser.parseListeOffre(body);
            } catch (Exception e) {
                m_Exp = e;
            }
            return liste;
        }

        @Override
        protected void onPostExecute(ArrayList<Offre> p_Offres) {
            getActivity().setProgressBarIndeterminateVisibility(false);

            if (m_Exp == null && p_Offres != null) {

                Intent i = new Intent(getActivity(), RecherchePassagers.class);
                i.putExtra("lstResultatsOffres", p_Offres);
                startActivity(i);

            } else {
                Log.e(TAG, "Error while fetching", m_Exp);
                Toast.makeText(getActivity(), getString(R.string.erreur_recherche), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
