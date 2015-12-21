package carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data_sources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Offre;
import carpoolxpress.cours03e.dinfogarneau.com.carpoolxpress.data.Utilisateur;

/**
 * Created by Steve on 20/10/2015.
 */
public class OffreDataSource {

    private final static int DB_VERSION = 1;
    private final static String TABLE_NAME = "offre";

    private static final String COL_ID = "_id";
    private static final String COL_TITRE = "titre";
    private static final String COL_POINT_DEPART = "pointdepart";
    private static final String COL_POINT_ARRIVEE = "pointarrivee";
    private static final String COL_DATE = "date";
    private static final String COL_HEURE = "heure";
    private static final String COL_NB_PLACE = "nbplace";
    private static final String COL_USERNAME = "username";
    private static final String COL_ID_OFFRE = "idOffre";
    private static final String COL_PASSAGERS = "passagers";

    private static final int IDX_ID = 0;
    private static final int IDX_TITRE = 1;
    private static final int IDX_POINT_DEPART = 2;
    private static final int IDX_POINT_ARRIVEE = 3;
    private static final int IDX_DATE = 4;
    private static final int IDX_HEURE = 5;
    private static final int IDX_NB_PLACE = 6;
    private static final int IDX_USERNAME = 7;
    private static final int IDX_ID_OFFRE = 8;
    private static final int IDX_PASSAGERS = 9;

    private OffreDbHelper m_Helper;
    private SQLiteDatabase m_Db;

    public OffreDataSource(Context context) { m_Helper = new OffreDbHelper(context); }

    public void open() { m_Db = this.m_Helper.getWritableDatabase(); }

    public void close() { m_Db.close(); }

    public int insert(Offre offre) {

        Cursor ti = m_Db.rawQuery("PRAGMA table_info(offre)", null);
        if ( ti.moveToFirst() ) {
            do {
                System.out.println("col: " + ti.getString(1));
            } while (ti.moveToNext());
        }

        ContentValues ligne = faireLigne(offre);
        int newId = (int) m_Db.insertOrThrow(TABLE_NAME, null, ligne);
        offre.setId(newId);
        return newId;
    }

    public static ContentValues faireLigne(Offre offre) {
        ContentValues row = new ContentValues();
        row.put(COL_TITRE, offre.getTitre());
        row.put(COL_POINT_DEPART, offre.getPointDepart());
        row.put(COL_POINT_ARRIVEE, offre.getPointArrivee());
        row.put(COL_DATE, offre.getDate());
        row.put(COL_HEURE, offre.getHeure());
        row.put(COL_NB_PLACE, offre.getNbPlaces());
        row.put(COL_USERNAME, offre.getUsername());
        row.put(COL_ID_OFFRE, offre.getIdOffre());
        row.put(COL_PASSAGERS, offre.getPassagers());
        return row;
    }

    public void update(Offre offre) {
        ContentValues row = faireLigne(offre);
        m_Db.update(TABLE_NAME, row, COL_ID + "=" + offre.getId(), null);
    }

    public void delete(int id) { m_Db.delete(TABLE_NAME, COL_ID + "=" + id, null); }

    public void removeAll() { m_Db.delete(TABLE_NAME, null, null);}

    public Offre getOffre(int id) {
        Offre o = null;
        Cursor c = m_Db.query(
                TABLE_NAME, null, COL_ID + "=" + id, null, null,null,null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
            o = lireLigne(c);
        }
        return o;
    }

    public List<Offre> getAllOffres() {
        List<Offre> list = new ArrayList<Offre>();
        Cursor c = m_Db.query(
                TABLE_NAME,null,null,null,null,null,null,null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            Offre o = lireLigne(c);
            list.add(o);
            c.moveToNext();
        }
        return list;
    }

    private static Offre lireLigne(Cursor cursor) {
        Offre o = new Offre();
        o.setId(cursor.getInt(IDX_ID));
        o.setTitre(cursor.getString(IDX_TITRE));
        o.setPointDepart(cursor.getString(IDX_POINT_DEPART));
        o.setPointArrivee(cursor.getString(IDX_POINT_ARRIVEE));
        o.setDate(cursor.getString(IDX_DATE));
        o.setHeure(cursor.getString(IDX_HEURE));
        o.setNbPlaces((cursor.getInt(IDX_NB_PLACE)));
        o.setUsername(cursor.getString(IDX_USERNAME));
        o.setIdOffre(cursor.getString(IDX_ID_OFFRE));
        o.setPassagers(cursor.getString(IDX_PASSAGERS));
        return o;
    }

    private static class OffreDbHelper extends SQLiteOpenHelper {
        public OffreDbHelper(Context context) {
            super(context, "offre.sqlite", null, DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "create table " + TABLE_NAME
                            + " (_id integer primary key autoincrement, "
                            + "titre text,"
                            + "pointdepart text, pointarrivee text, date text, "
                            + "heure text,"
                            + "nbplace integer,"
                            + "username text, "
                            + "idOffre integer, "
                            + "passagers text)"
                    );
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
            this.onCreate(db);
        }
    }
}

