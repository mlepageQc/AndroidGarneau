package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data;

import java.io.Serializable;

/**
 * Created by Steve on 22/10/2015.
 */
public class Offre implements Serializable {

    private static final long serialVersionUID = -6735796385394522140L;
    public static final int ID_NON_DEFINI = -1;

    private int m_Id;
    private String m_Titre;
    private String m_Point_Depart;
    private String m_Point_Arrivee;
    private String m_Date;
    private String m_Heure;
    private int m_NbPlaces;
    private String username;
    private String m_idOffre;
    private String m_Passagers;

    /**
     Constructeur par défaut (null)
     */
    public Offre() {
        this(ID_NON_DEFINI,"","","","","",ID_NON_DEFINI,"", "", "");
    }

    /**
     * Constructeur Initialisation
     * */
    public Offre(int p_Id, String p_Titre, String p_Point_Depart, String p_Point_Arrivee, String p_Date, String p_Heure, int p_NbPlaces, String p_Username, String p_IdOffre, String p_Passagers) {
        this.setTitre(p_Titre);
        this.setId(p_Id);
        this.setPointDepart(p_Point_Depart);
        this.setPointArrivee(p_Point_Arrivee);
        this.setDate(p_Date);
        this.setHeure(p_Heure);
        this.setNbPlaces(p_NbPlaces);
        this.setUsername(p_Username);
        this.setIdOffre(p_IdOffre);
        this.setPassagers(p_Passagers);
    }

    public Offre(String p_Titre, String p_Point_Depart, String p_Point_Arrivee, String p_Date, String p_Heure, int p_NbPlaces, String p_Username, String p_IdOffre, String p_Passagers) {
        this.setTitre(p_Titre);
        this.setPointDepart(p_Point_Depart);
        this.setPointArrivee(p_Point_Arrivee);
        this.setDate(p_Date);
        this.setHeure(p_Heure);
        this.setNbPlaces(p_NbPlaces);
        this.setUsername(p_Username);
        this.setIdOffre(p_IdOffre);
        this.setPassagers(p_Passagers);
    }

    public int getId() {
        return m_Id;
    }

    public void setId(int m_Id) {
        this.m_Id = m_Id;
    }

    public String getIdOffre(){
        return m_idOffre;
    }

    public String getTitre() {
        return m_Titre;
    }

    public void setTitre(String m_Titre) {
        this.m_Titre = m_Titre;
    }

    public String getPointDepart() {
        return m_Point_Depart;
    }

    public void setPointDepart(String m_Point_Depart) {
        this.m_Point_Depart = m_Point_Depart;
    }

    public String getPointArrivee() {
        return m_Point_Arrivee;
    }

    public void setPointArrivee(String m_Point_Arrivee) {
        this.m_Point_Arrivee = m_Point_Arrivee;
    }

    public String getDate() {
        return m_Date;
    }

    public void setDate(String m_Date) {
        this.m_Date = m_Date;
    }

    public String getHeure() {
        return m_Heure;
    }

    public void setHeure(String m_Heure) {
        this.m_Heure = m_Heure;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNbPlaces() {
        return m_NbPlaces;
    }

    public void setNbPlaces(int m_NbPlaces) {
        this.m_NbPlaces = m_NbPlaces;
    }

    public void setIdOffre(String m_idOffre){
        this.m_idOffre = m_idOffre;
    }

    public String getPassagers() { return m_Passagers; }

    public void setPassagers(String passagers) { this.m_Passagers = passagers; }
}
