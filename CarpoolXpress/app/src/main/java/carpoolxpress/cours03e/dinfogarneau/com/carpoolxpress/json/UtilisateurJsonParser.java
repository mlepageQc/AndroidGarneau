package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Utilisateur;
//Classe permettant de faire des opérations de conversions entre des objets JSON et Utilisateur
public class UtilisateurJsonParser {

    //Attributs de la classe
    public static final String UTIL_USERNAME = "username";
    public static final String UTIL_PASSWORD = "password";
    public static final String UTIL_ADRESSE = "adresse";
    public static final String UTIL_NOM = "nom";
    public static final String UTIL_PRENOM = "prenom";

    //Parse un objet JSON en objet Utilisateur
    public static Utilisateur parseUtilisateur(String p_body) throws JSONException {
        JSONObject object = new JSONObject(p_body);

        return  new Utilisateur(object.getString(UTIL_USERNAME), object.getString(UTIL_PASSWORD),
                object.getString(UTIL_ADRESSE), object.getString(UTIL_NOM), object.getString(UTIL_PRENOM));
    }

    //Parse en objet JSON les informations de connexion entrées par l'utilisateur
    public static JSONObject loginInfosToJson(String username, String password) throws JSONException
    {
        JSONObject loginInfos = new JSONObject();

        loginInfos.put(UTIL_USERNAME, username);
        loginInfos.put(UTIL_PASSWORD, password);

        return loginInfos;
    }

    //Parse un objet Utilisateur en objet JSON
    public static JSONObject ToJSONObject(Utilisateur u) throws JSONException {
        JSONObject jsonObj = new JSONObject();

        jsonObj.put(UTIL_USERNAME, u.getUsername());
        jsonObj.put(UTIL_PASSWORD, u.getPassword());
        jsonObj.put(UTIL_ADRESSE, u.getAdresse());
        jsonObj.put(UTIL_NOM, u.getNom());
        jsonObj.put(UTIL_PRENOM , u.getPrenom());

        return jsonObj;
    }
}


