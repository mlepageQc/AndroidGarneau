package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data;

import java.io.Serializable;

/**
 * Created by Steve on 22/10/2015.
 */
public class Demande implements Serializable {

    private static final long serialVersionUID = -6735796385394522140L;
    public static final int ID_NON_DEFINI = -1;

    private String m_idDemande;
    private String username;
    private String m_idOffre;
    private String m_titre;
    private boolean m_notifie;

    public Demande() {
        this("", "", "", "", false);
    }

    public Demande(String p_Username, String p_IdOffre, String p_Titre, boolean p_Notifie) {
        this.setIdOffre(p_IdOffre);
        this.setUsername(p_Username);
        this.setTitre(p_Titre);
        this.setNotifie(p_Notifie);
    }

    public Demande(String p_IdDemande, String p_Username, String p_IdOffre, String p_Titre, boolean p_Notifie) {
        this.setIdOffre(p_IdOffre);
        this.setUsername(p_Username);
        this.setIdDemande(p_IdDemande);
        this.setTitre(p_Titre);
        this.setNotifie(p_Notifie);
    }

    public String getIdOffre() {
        return m_idOffre;
    }

    public void setIdOffre(String idOffre) {
        this.m_idOffre = idOffre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIdDemande() {
        return m_idDemande;
    }

    public void setIdDemande(String idDemande) {
        this.m_idDemande = idDemande;
    }

    public String getTitre() { return m_titre; }

    public void setTitre(String titre) {
        this.m_titre = titre;
    }

    public boolean getNotifie() { return this.m_notifie; }

    public void setNotifie(boolean notifie) { this.m_notifie = notifie; }

}
