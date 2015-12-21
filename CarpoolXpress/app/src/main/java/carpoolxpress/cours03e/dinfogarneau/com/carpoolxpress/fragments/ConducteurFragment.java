package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.util.ArrayList;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.activities.DepartsConducteur;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data_sources.OffreDataSource;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.R;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Offre;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json.OffreJsonParser;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.utils.Haversine;

/**
 * Created by Maxime on 2015-09-09.
 */
public class ConducteurFragment extends Fragment implements Button.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapLongClickListener{

    //Attribut du fragment pour la récupération des contrôles
    private LinearLayout dateDialog;
    private LinearLayout timeDialog;
    private LinearLayout conducteurFragment;
    private Button btnChoixDate;
    private Button btnChoixHeure;
    private Button btnAjoutDepart;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private boolean dateChoisie;
    private boolean heureChoisie;
    private EditText txtNbPlaces;
    private EditText txtTitreOffre;
    private TextView txtDistance;
    private OffreDataSource m_OffreDataSource;
    private Offre m_Offre;
    private GoogleMap mMap;
    private SupportMapFragment fragment;
    private GoogleApiClient mGoogleApiClient;
    private boolean choixDepart;
    private LatLng coordDepart = null;
    private LatLng coordArrivee = null;
    private int m_Offre_id_modif;
    private Offre m_Offre_modif;
    private MenuItem optionAjouter;
    private Menu menu;

    //Préférences partagées de l'application
    private SharedPreferences sp;

    //Paramètre pour la connexion Http
    private final String TAG = this.getClass().getSimpleName();
    private final static String WEB_SERVICE_URL = "carpoolxpress-1138.appspot.com";
    private final static String REST_OFFRES = "/offre";

    //Client Http
    private HttpClient m_ClientHttp = new DefaultHttpClient();

    //Liste des offres
    private ArrayList<Offre> m_Offres = new ArrayList<Offre>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle){
        conducteurFragment = (LinearLayout)inflater.inflate(R.layout.conducteur_fragment, container, false);

        //Récupération des contrôles de la vue
        dateDialog = (LinearLayout)inflater.inflate(R.layout.dialog_date, null);
        timeDialog = (LinearLayout)inflater.inflate(R.layout.dialog_heure, null);

        btnChoixDate = (Button)conducteurFragment.findViewById(R.id.btn_date_conducteur);
        btnChoixHeure = (Button)conducteurFragment.findViewById(R.id.btn_heure_conducteur);
        btnAjoutDepart = (Button)conducteurFragment.findViewById(R.id.btn_ajouter_depart);
        datePicker = (DatePicker)dateDialog.findViewById(R.id.date_picker);
        timePicker = (TimePicker)timeDialog.findViewById(R.id.time_picker);
        txtNbPlaces = (EditText) conducteurFragment.findViewById(R.id.txt_nb_place);
        txtTitreOffre = (EditText) conducteurFragment.findViewById(R.id.txt_titre_offre);
        txtDistance = (TextView) conducteurFragment.findViewById(R.id.txt_distance);

        //Clique event sur les boutons
        btnChoixDate.setOnClickListener(this);
        btnChoixHeure.setOnClickListener(this);
        btnAjoutDepart.setOnClickListener(this);

        //Sélection des critères
        dateChoisie = false;
        heureChoisie = false;
        this.choixDepart = true;

        this.m_OffreDataSource = new OffreDataSource(this.getActivity());

        //Récupération des préférences partagées
        sp = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        //Récupération de l'offre (mode modif) et gestion du fragmentManager pour la carte
        setControlesAndMap();
        return conducteurFragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
    }

    public void setControlesAndMap() {
        this.m_Offre_id_modif = getActivity().getIntent().getIntExtra("offreId", 0);

        if(this.m_Offre_id_modif != 0) {
            //On pré-remplit les champs avec les données de l'offre courante
            this.m_OffreDataSource.open();
            this.m_Offre_modif = this.m_OffreDataSource.getOffre(m_Offre_id_modif);
            btnChoixDate.setText(m_Offre_modif.getDate());
            btnChoixHeure.setText(m_Offre_modif.getHeure());
            txtTitreOffre.setText(m_Offre_modif.getTitre());
            txtNbPlaces.setText(String.valueOf(m_Offre_modif.getNbPlaces()));
            this.dateChoisie = true;
            this.heureChoisie = true;

            ((Button) conducteurFragment.findViewById(R.id.btn_ajouter_depart)).setText(R.string.modifier_depart);
            m_Offre = m_OffreDataSource.getOffre(m_Offre_id_modif);
        }

        if (mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map, fragment).commit();
            }

            buildGoogleApiClient();
            setUpMapIfNeeded();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.m_OffreDataSource.open();
    }

    @Override
    public void onResume() {
        super.onStart();
        this.m_OffreDataSource.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.m_OffreDataSource.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.m_OffreDataSource.close();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
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

        if(this.m_Offre_id_modif != 0) {
            //focus sur les coordonnées de départ du trajet
            Double coordXDepart = Double.parseDouble(this.m_Offre_modif.getPointDepart().split(",")[0]);
            Double coordYDepart = Double.parseDouble(this.m_Offre_modif.getPointDepart().split(",")[1]);

            Double coordXArrivee = Double.parseDouble(this.m_Offre_modif.getPointArrivee().split(",")[0]);
            Double coordYArrivee = Double.parseDouble(this.m_Offre_modif.getPointArrivee().split(",")[1]);

            this.coordDepart = new LatLng(coordXDepart, coordYDepart);
            this.coordArrivee = new LatLng(coordXArrivee, coordYArrivee);

            double distance = Haversine.distance(coordXDepart, coordYDepart, coordXArrivee, coordYArrivee);

            txtDistance.setText(String.valueOf(distance) + " km");

            //Ajout des marqueurs pour les coordonnées de l'offre sur la carte
            mMap.addMarker(new MarkerOptions().position(this.coordDepart).title(("Départ")));
            mMap.addMarker(new MarkerOptions().position(this.coordArrivee).title(("Arrivée"))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            //Centre de la carte sur le point de départ de l'ofre
            center = CameraUpdateFactory.newLatLngZoom(new LatLng(coordXDepart, coordYDepart), 10);

        } else {
            center = CameraUpdateFactory.newLatLngZoom(new LatLng(46.7755482,
                    -71.2954967), 10);
        }

        mMap.moveCamera(center);
    }

    @Override
    public void onClick(View v) {

        LayoutInflater inflater = getActivity().getLayoutInflater();

        switch(v.getId()) {
            //Choix de la date
            case R.id.btn_date_conducteur:
                //choix de la date
                AlertDialog.Builder dateBuilder = new AlertDialog.Builder(getActivity());

                dateBuilder.setTitle(R.string.titreDate)
                        .setView(dateDialog)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                btnChoixDate.setText(datePicker.getDayOfMonth() + " / " + datePicker.getMonth() + " / " + datePicker.getYear());
                                dateChoisie = true;
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                break;
            //choix de l'heure
            case R.id.btn_heure_conducteur:

                AlertDialog.Builder heureBuilder = new AlertDialog.Builder(getActivity());

                heureBuilder.setTitle(R.string.titreHeure)
                        .setView(timeDialog)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                //Récupération de l'heure
                                String heure = String.valueOf(timePicker.getCurrentHour());
                                int minutes = timePicker.getCurrentMinute();
                                String minutesString = "";

                                //On s'assure de garder le 0 en avant des minutes si elles sont inférieures à 10
                                if (minutes < 10) {
                                    minutesString = "0" + String.valueOf(minutes);
                                } else {
                                    minutesString = String.valueOf(minutes);
                                }

                                btnChoixHeure.setText(heure + " : " + minutesString);
                                heureChoisie = true;
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                break;
            //Ajout ou modification de l'offre
            case R.id.btn_ajouter_depart:

                Intent i = new Intent(getActivity(), DepartsConducteur.class);
                String toastMessage = "";

                //Validation sur les champs obligatoires quand on veut ajouter une offre
                if(dateChoisie && heureChoisie) {
                    try {
                        int nbPlaces = Integer.parseInt(String.valueOf(txtNbPlaces.getText()));

                        String coordX = String.valueOf(this.coordDepart.latitude) + ',' + String.valueOf(this.coordDepart.longitude);
                        String coordY = String.valueOf(this.coordArrivee.latitude) + ',' + String.valueOf(this.coordArrivee.longitude);

                        //Mode ajout d'une offre
                        if(this.m_Offre_id_modif == 0) {
                            if(this.coordDepart != null && this.coordArrivee != null) {

                                //Création de l'offre
                                this.m_Offre = new Offre(txtTitreOffre.getText().toString(), coordX,
                                            coordY,
                                            btnChoixDate.getText().toString().replaceAll(" ", ""),
                                            btnChoixHeure.getText().toString().replaceAll(" ", ""),
                                            nbPlaces,
                                            sp.getString("Utilisateur", ""), "", "");

                                //Requête serveur pour l'ajout de l'offre
                                new PostNewOffreTask().execute((Void)null);
                            } else {
                                Toast.makeText(this.getActivity(), "Veuillez choisir un point de départ et un point d'arrivée.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            //Mode modification de trajet
                            this.m_Offre_modif.setTitre(this.txtTitreOffre.getText().toString());
                            this.m_Offre_modif.setDate(this.btnChoixDate.getText().toString().replaceAll(" ", ""));
                            this.m_Offre_modif.setHeure(this.btnChoixHeure.getText().toString().replaceAll(" ", ""));
                            this.m_Offre_modif.setPointDepart(coordX);
                            this.m_Offre_modif.setPointArrivee(coordY);
                            this.m_Offre_modif.setNbPlaces(nbPlaces);
                            this.m_Offre_modif.setPassagers(this.m_Offre_modif.getPassagers());

                            new PutOffreTask().execute((Void)null);

                        }
                    } catch (Exception e) {
                        Toast.makeText(this.getActivity(), "Un ou plusieurs champs sont invalides",
                                Toast.LENGTH_LONG).show();
                    }
                }
                 else {
                     Toast.makeText(this.getActivity(), "Veuillez remplir tous les champs et saisir une date et une heure pour l'offre.",
                             Toast.LENGTH_LONG).show();
                 }
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if(this.choixDepart == true) {
            this.coordDepart = latLng;
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
            builder.setTitle("Ajouter ce point de départ?")
                    .setMessage(this.coordDepart.toString())
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id){
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
                        public void onClick(DialogInterface dialog, int id){
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
    private class PostNewOffreTask extends AsyncTask<Void, Void, Offre> {
        Exception m_Exp;

        @Override
        protected void onPreExecute() {
            getActivity().setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Offre doInBackground(Void ... unused) {
            Offre uneOffre = null;

            try {
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority(WEB_SERVICE_URL)
                        .path(REST_OFFRES)
                        .build();

                HttpPost postMethod = new HttpPost(uri.toString());
                JSONObject obj = OffreJsonParser.ToJSONObject(m_Offre);
                postMethod.setEntity(new StringEntity(obj.toString()));
                postMethod.addHeader("Content-Type", "application/json");

                String body = m_ClientHttp.execute(postMethod, new BasicResponseHandler());
                uneOffre = OffreJsonParser.parseOffre(body);
                Log.i(TAG, "Post terminé");

            } catch (Exception e) {
                m_Exp = e;
            }
            return uneOffre;
        }

        @Override
        protected void onPostExecute(Offre p_Offre) {

            if (m_Exp == null) {
                //Ajout l'offre en local
                m_Offre.setIdOffre(p_Offre.getIdOffre());
                m_OffreDataSource.insert(m_Offre);

                //Redirection vers la liste des offres
                Intent i = new Intent(getActivity(), DepartsConducteur.class);
                String toastMessage = "";
                toastMessage = "Départ ajouté avec succès";
                i.putExtra("toast_message", toastMessage);
                startActivity(i);

            } else {
                Log.e(TAG, "Error while posting", m_Exp);
                Toast.makeText(getActivity(), getString(R.string.erreur_ajout), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class PutOffreTask extends AsyncTask<Void, Void, Void> {
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
                        .path(REST_OFFRES + "/" + String.valueOf(m_Offre_modif.getIdOffre()))
                        .build();

                //Exécution de la requête
                HttpPut putMethod = new HttpPut(uri.toString());
                JSONObject obj = OffreJsonParser.ToJSONObject(m_Offre_modif);
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

            if (m_Exp == null) {
                //Modification de l'offre en local
                m_OffreDataSource.update(m_Offre_modif);
                Intent i = new Intent(getActivity(), DepartsConducteur.class);

                //Redirection vers la liste des départ
                String toastMessage = "";
                toastMessage = "Départ modifié avec succès";
                i.putExtra("toast_message", toastMessage);
                startActivity(i);
            } else {
                Log.e(TAG, "Error while posting", m_Exp);
                Toast.makeText(getActivity(), getString(R.string.erreur_modif), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
