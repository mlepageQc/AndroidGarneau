package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data_sources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Utilisateur;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Maxime on 2015-10-20.
 */
public class UtilisateurDataSource {

    private final static int DB_VERSION = 1;
    private final static String TABLE_NAME = "utilisateur";

    private static final String COL_ID = "_id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_ADRESSE = "adresse";
    private static final String COL_NOM = "nom";
    private static final String COL_PRENOM = "prenom";

    // Constantes pour les indices des champs de la BD
    private static final int IDX_ID = 0;
    private static final int IDX_USERNAME = 1;
    private static final int IDX_PASSWORD = 2;
    private static final int IDX_ADRESSE = 3;
    private static final int IDX_NOM = 4;
    private static final int IDX_PRENOM = 5;

    private UtilisateurDbHelper m_Helper;
    private SQLiteDatabase m_Db;

    public UtilisateurDataSource(Context context) { m_Helper = new UtilisateurDbHelper(context);}

    public void open() { m_Db = this.m_Helper.getWritableDatabase();}

    public void close() { m_Db.close(); }

    public int insert(Utilisateur utilisateur) {
        ContentValues ligne = faireLigne(utilisateur);
        int newId = (int) m_Db.insert(TABLE_NAME, null, ligne);
        utilisateur.setId(newId);
        return newId;
    }

    public static ContentValues faireLigne(Utilisateur utilisateur) {
        ContentValues row = new ContentValues();
        row.put(COL_USERNAME, utilisateur.getUsername());
        row.put(COL_PASSWORD, utilisateur.getPassword());
        row.put(COL_ADRESSE, utilisateur.getAdresse());
        row.put(COL_NOM, utilisateur.getNom());
        row.put(COL_PRENOM, utilisateur.getPrenom());
        return row;
    }

    public void update(Utilisateur utilisateur) {
        ContentValues row = faireLigne(utilisateur);
        m_Db.update(TABLE_NAME,row, COL_ID + "=" + utilisateur.getId(), null);
    }

    public void delete(int id) { m_Db.delete(TABLE_NAME, COL_ID + "=" + id, null); }

    public void removeAll() { m_Db.delete(TABLE_NAME,null,null); }

    public Utilisateur getUtilisateur(String username, String motpasse) {
        Utilisateur u = null;
        Cursor c = m_Db.rawQuery("SELECT _id, username, password, adresse, nom, prenom FROM " + TABLE_NAME + " WHERE username = ? AND password = ?", new String[] {username, motpasse});
        c.moveToFirst();
        if (!c.isAfterLast()) {
            u = lireLigne(c);
        }
        return u;
    }

    public List<Utilisateur> getAllUtilisateurs() {
        List<Utilisateur> list = new ArrayList<Utilisateur>();
        Cursor c = m_Db.query(TABLE_NAME,null,null,null,null,null,null);
        c.moveToFirst();
        while(!c.isAfterLast()) {
            Utilisateur u = lireLigne(c);
            list.add(u);
            c.moveToNext();
        }
        return list;
    }

    private static Utilisateur lireLigne(Cursor cursor) {
        Utilisateur u = new Utilisateur();
        u.setId(cursor.getInt(IDX_ID));
        u.setUsername(cursor.getString(IDX_USERNAME));
        u.setPassword(cursor.getString(IDX_PASSWORD));
        u.setAdresse(cursor.getString(IDX_ADRESSE));
        u.setNom(cursor.getString(IDX_NOM));
        u.setPrenom(cursor.getString(IDX_PRENOM));

        return u;
    }

    private static class UtilisateurDbHelper extends SQLiteOpenHelper {
        public UtilisateurDbHelper(Context context) {
            super(context, "person.sqlite", null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "create table " + TABLE_NAME
                            + " (_id integer primary key autoincrement, "
                            + "username text, password text, adresse text, "
                            + "nom text, "
                            + "prenom text)");
        }

        /*
            Lors de la mise à jour de la BD; installation d'une nouvelle version
            de l'application et de la BD.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            this.onCreate(db);
        }
    }

}
