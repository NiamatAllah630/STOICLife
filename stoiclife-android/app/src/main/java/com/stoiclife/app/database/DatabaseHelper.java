package com.stoiclife.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "stoiclife.db";
    private static final int    DB_VERSION = 3;

    
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // ── Noms des tables 
    public static final String TABLE_UTILISATEUR      = "utilisateur";
    public static final String TABLE_ACTIVITE         = "activite";
    public static final String TABLE_PLANNING         = "planning_jour";
    public static final String TABLE_PROGRESSION      = "progression";
    public static final String TABLE_SUIVI_EMOTIONNEL = "suivi_emotionnel";
    public static final String TABLE_JOURNAL          = "journal_entry";
    public static final String TABLE_CITATION_FAVS    = "citation_favoris";
    private static final String TABLE_BADGES = "badges";
    // ── Création des tables 
    private static final String CREATE_UTILISATEUR =
        "CREATE TABLE " + TABLE_UTILISATEUR + " (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "nom TEXT NOT NULL, " +
        "email TEXT UNIQUE NOT NULL, " +
        "password TEXT NOT NULL, " +
        "date_creation TEXT NOT NULL" +
        ");";

    private static final String CREATE_ACTIVITE =
            "CREATE TABLE " + TABLE_ACTIVITE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "titre TEXT NOT NULL, " +
                    "type_activite TEXT NOT NULL, " +
                    "description TEXT, " +
                    "duree_minutes INTEGER DEFAULT 30, " +
                    "statut TEXT DEFAULT 'planifiee', " +
                    "date_planifiee TEXT NOT NULL, " +
                    "plage_horaire TEXT, " +   
                    "utilisateur_id INTEGER NOT NULL, " +
                    "xp_gagne INTEGER DEFAULT 10, " +
                    "FOREIGN KEY(utilisateur_id) REFERENCES utilisateur(id)" +
                    ");";

    private static final String CREATE_PLANNING =
        "CREATE TABLE " + TABLE_PLANNING + " (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "date_jour TEXT NOT NULL, " +
        "resume_matin TEXT DEFAULT '', " +
        "resume_journee TEXT DEFAULT '', " +
        "resume_soir TEXT DEFAULT '', " +
        "utilisateur_id INTEGER NOT NULL, " +
        "UNIQUE(date_jour, utilisateur_id), " +
        "FOREIGN KEY(utilisateur_id) REFERENCES utilisateur(id)" +
        ");";

    private static final String CREATE_PROGRESSION =
        "CREATE TABLE " + TABLE_PROGRESSION + " (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "utilisateur_id INTEGER UNIQUE NOT NULL, " +
        "points_xp INTEGER DEFAULT 0, " +
        "niveau_actuel TEXT DEFAULT 'Débutant', " +
        "streak_actuel INTEGER DEFAULT 0, " +
        "nombre_activites_completees INTEGER DEFAULT 0, " +
        "FOREIGN KEY(utilisateur_id) REFERENCES utilisateur(id)" +
        ");";

    private static final String CREATE_SUIVI_EMOTIONNEL =
            "CREATE TABLE " + TABLE_SUIVI_EMOTIONNEL + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "utilisateur_id INTEGER NOT NULL, " +
                    "humeur TEXT NOT NULL, " +
                    "intensite INTEGER DEFAULT 3, " +
                    "declencheur TEXT DEFAULT '', " +
                    "date_jour TEXT NOT NULL, " +
                    "note TEXT DEFAULT '', " +
                    "created_at TEXT DEFAULT '', " +
                    "FOREIGN KEY(utilisateur_id) REFERENCES utilisateur(id)" +
                    ");";

    private static final String CREATE_JOURNAL =
        "CREATE TABLE " + TABLE_JOURNAL + " (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "utilisateur_id INTEGER NOT NULL, " +
        "date_jour TEXT NOT NULL, " +
        "type_prompt TEXT DEFAULT 'libre', " +
        "contenu TEXT NOT NULL, " +
        "created_at TEXT NOT NULL, " +
        "FOREIGN KEY(utilisateur_id) REFERENCES utilisateur(id)" +
        ");";

    private static final String CREATE_CITATION_FAVS =
        "CREATE TABLE " + TABLE_CITATION_FAVS + " (" +
        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "utilisateur_id INTEGER NOT NULL, " +
        "citation_id TEXT NOT NULL, " +
        "UNIQUE(utilisateur_id, citation_id), " +
        "FOREIGN KEY(utilisateur_id) REFERENCES utilisateur(id)" +
        ");";
    private static final String CREATE_BADGES =
            "CREATE TABLE " + TABLE_BADGES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "utilisateur_id INTEGER NOT NULL, " +
                    "nom TEXT NOT NULL, " +
                    "description TEXT, " +
                    "date_obtention TEXT, " +
                    "FOREIGN KEY(utilisateur_id) REFERENCES utilisateur(id)" +
                    ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_UTILISATEUR);
        db.execSQL(CREATE_ACTIVITE);
        db.execSQL(CREATE_PLANNING);
        db.execSQL(CREATE_PROGRESSION);
        db.execSQL(CREATE_SUIVI_EMOTIONNEL);
        db.execSQL(CREATE_JOURNAL);
        db.execSQL(CREATE_CITATION_FAVS);
        db.execSQL(CREATE_BADGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITATION_FAVS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUIVI_EMOTIONNEL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRESSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANNING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UTILISATEUR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BADGES);
        onCreate(db);
    }
}
