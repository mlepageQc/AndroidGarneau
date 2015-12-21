package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data;

import java.io.Serializable;
/**
 * Created by Maxime on 2015-10-20.
 */
public class Utilisateur implements Serializable {

    private static final long serialVersionUID = -6735796385394522140L;
    public static final int ID_NON_DEFINI = -1;
    private int m_Id;
    private String m_Username;
    private String m_Password;
    private String m_Adresse;
    private String m_Nom;
    private String m_Prenom;

    public Utilisateur() {
        this(ID_NON_DEFINI, "", "", "", "", "");
    }

    public Utilisateur(String p_Username,String p_Password, String p_Adresse,
                       String p_Nom, String p_Prenom) {
        /**this(ID_NON_DEFINI, p_Username, p_Password, p_Adresse, p_Nom, p_Prenom);*/
        this.setUsername(p_Username);
        this.setPassword(p_Password);
        this.setAdresse(p_Adresse);
        this.setNom(p_Nom);
        this.setPrenom(p_Prenom);
    }

    public Utilisateur(int p_Id,
                       String p_Username,
                       String p_Password,
                       String p_Adresse,
                       String p_Nom,
                       String p_Prenom) {
        this.setId(p_Id);
        this.setUsername(p_Username);
        this.setPassword(p_Password);
        this.setAdresse(p_Adresse);
        this.setNom(p_Nom);
        this.setPrenom(p_Prenom);
    }

    public int getId() { return this.m_Id; };

    public void setId(int p_id) {
        this.m_Id = p_id;
    }

    public String getUsername() { return this.m_Username; };

    public void setUsername(String p_Username) {
        if (p_Username == null) {
            throw new NullPointerException("Login nul !");
        }
        this.m_Username = p_Username;
    }

    public String getAdresse() { return this.m_Adresse; };

    public void setAdresse(String p_Adresse) {
        if (p_Adresse == null) {
            throw new NullPointerException("Adresse nulle !");
        }
        this.m_Adresse = p_Adresse;
    }

    public String getNom() { return this.m_Nom; };

    public void setNom(String p_Nom) {
        if (p_Nom == null) {
            throw new NullPointerException("Nom nul !");
        }
        this.m_Nom = p_Nom;
    }

    public String getPrenom() { return this.m_Prenom; };

    public void setPrenom(String p_Prenom) {
        if (p_Prenom == null) {
            throw new NullPointerException("Prénom nul !");
        }
        this.m_Prenom = p_Prenom;
    }

    public String getPassword() {
        return m_Password;
    }

    public void setPassword(String p_Password) {
        if (p_Password == null) {
            throw new NullPointerException("Mot de passe nul !");
        }
        this.m_Password = p_Password;
    }
}


