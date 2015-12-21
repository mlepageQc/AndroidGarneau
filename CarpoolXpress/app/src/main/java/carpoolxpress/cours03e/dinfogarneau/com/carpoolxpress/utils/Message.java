package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.utils;

public class Message {

    private String m_Conducteur;
    private String m_Passager;
    private String m_IdOffre;

    public Message(String cond, String pass, String idOffre) {
        this.m_Conducteur = cond;
        this.m_Passager = pass;
        this.m_IdOffre = idOffre;
    }

    public String getConducteur() { return this.m_Conducteur; }
    public void setConducteur(String cond) { this.m_Conducteur = cond; }

    public String getPassager() { return this.m_Passager; }
    public void setPassager(String pass) { this.m_Passager = pass; }

    public String getIdOffre() { return this.m_IdOffre; }
    public void setIdOffre(String id) { this.m_IdOffre = id; }


}
