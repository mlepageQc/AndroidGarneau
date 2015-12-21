package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Demande;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Offre;

//Classe permettant de faire des opérations de conversions entre des objets JSON et Demande
public class DemandeJsonParser {

    //Attributs de la classe
    public static final String UTIL_ID_DEMANDE = "_id";
    public static final String UTIL_USERNAME = "username";
    public static final String UTIL_ID_OFFRE = "idOffre";
    public static final String UTIL_TITRE = "titre";
    public static final String UTIL_NOTIFIE = "notifie";

    public static ArrayList<Demande> parseListeDemandes(String p_body) throws JSONException {
        ArrayList<Demande> liste= new ArrayList<Demande>();
        JSONArray array = new JSONArray(p_body);

        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonDemandes = array.getJSONObject(i);

            Demande d = new Demande(jsonDemandes.getString(UTIL_ID_DEMANDE), jsonDemandes.getString(UTIL_USERNAME),
                    jsonDemandes.getString(UTIL_ID_OFFRE), jsonDemandes.getString(UTIL_TITRE), jsonDemandes.getBoolean(UTIL_NOTIFIE));

            liste.add(d);
        }

        return liste;
    }

    //Parse un objet JSON en objet Demande
    public static Demande parseDemande(String p_body) throws JSONException {
        JSONObject object = new JSONObject(p_body);

        return  new Demande(object.getString(UTIL_ID_DEMANDE), object.getString(UTIL_USERNAME),
                object.getString(UTIL_ID_OFFRE), object.getString(UTIL_TITRE), object.getBoolean(UTIL_NOTIFIE));
    }

    //Parse en objet JSON les informations d'une demande
    public static JSONObject demandeToJson(String idDemande, String username, String idOffre, String titre, boolean notifie) throws JSONException
    {
        JSONObject demandeJson = new JSONObject();

        demandeJson.put(UTIL_USERNAME, username);
        demandeJson.put(UTIL_ID_OFFRE, idOffre);
        demandeJson.put(UTIL_ID_DEMANDE, idDemande);
        demandeJson.put(UTIL_TITRE, titre);
        demandeJson.put(UTIL_NOTIFIE, notifie);

        return demandeJson;
    }

    //Parse un objet Demande en objet JSON
    public static JSONObject ToJSONObject(Demande d) throws JSONException {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put(UTIL_USERNAME, d.getUsername());
        jsonObj.put(UTIL_ID_DEMANDE, d.getIdDemande());
        jsonObj.put(UTIL_ID_OFFRE, d.getIdOffre());
        jsonObj.put(UTIL_TITRE, d.getTitre());
        jsonObj.put(UTIL_NOTIFIE, d.getNotifie());

        return jsonObj;
    }
}


