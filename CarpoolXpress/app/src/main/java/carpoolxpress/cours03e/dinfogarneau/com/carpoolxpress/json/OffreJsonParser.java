package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Offre;

public class OffreJsonParser {

    public static final String UTIL_TITRE = "titre";
    public static final String UTIL_POINT_DEPART = "point_depart";
    public static final String UTIL_POINT_ARRIVEE = "point_arrive";
    public static final String UTIL_DATE = "date";
    public static final String UTIL_HEURE = "heure";
    public static final String UTIL_NB_PLACES = "nb_place";
    public static final String UTIL_USERNAME = "username";
    public static final String UTIL_ID_OFFRE = "_id";
    public static final String UTIL_PASSAGERS = "passagers";

    public static ArrayList<Offre> parseListeOffre(String p_body) throws JSONException {
        ArrayList<Offre> liste= new ArrayList<Offre>();
        JSONArray array = new JSONArray(p_body);

        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonOffres = array.getJSONObject(i);
            Offre o = new Offre(jsonOffres.getString(UTIL_TITRE), jsonOffres.getString(UTIL_POINT_DEPART),
                    jsonOffres.getString(UTIL_POINT_ARRIVEE), jsonOffres.getString(UTIL_DATE), jsonOffres.getString(UTIL_HEURE),
                    jsonOffres.getInt(UTIL_NB_PLACES), jsonOffres.getString(UTIL_USERNAME), jsonOffres.getString(UTIL_ID_OFFRE), "");
            liste.add(o);
        }

        return liste;
    }

    public static Offre parseOffre(String p_body) throws JSONException {
        JSONObject object = new JSONObject(p_body);

        return  new Offre(object.getString(UTIL_TITRE), object.getString(UTIL_POINT_DEPART),
                object.getString(UTIL_POINT_ARRIVEE), object.getString(UTIL_DATE), object.getString(UTIL_HEURE),
                object.getInt(UTIL_NB_PLACES), object.getString(UTIL_USERNAME), object.getString(UTIL_ID_OFFRE), "");
    }

    public static JSONObject ToJSONObject(Offre o) throws JSONException {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put(UTIL_TITRE, o.getTitre());
        jsonObj.put(UTIL_POINT_DEPART, o.getPointDepart());
        jsonObj.put(UTIL_POINT_ARRIVEE, o.getPointArrivee());
        jsonObj.put(UTIL_DATE, o.getDate());
        jsonObj.put(UTIL_HEURE, o.getHeure());
        jsonObj.put(UTIL_NB_PLACES, o.getNbPlaces());
        jsonObj.put(UTIL_USERNAME , o.getUsername());

        return jsonObj;
    }
}
