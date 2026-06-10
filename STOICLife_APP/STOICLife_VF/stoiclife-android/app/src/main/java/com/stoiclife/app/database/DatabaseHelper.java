package com.stoiclife.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "stoiclife.db";
    private static final int DB_VERSION = 4;

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

    public static final String TABLE_UTILISATEUR = "utilisateur";
    public static final String TABLE_ACTIVITE = "activite";
    public static final String TABLE_CATALOGUE_ACTIVITE = "catalogue_activite";
    public static final String TABLE_PLANNING = "planning_jour";
    public static final String TABLE_PROGRESSION = "progression";
    public static final String TABLE_SUIVI_EMOTIONNEL = "suivi_emotionnel";
    public static final String TABLE_JOURNAL = "journal_entry";
    public static final String TABLE_CITATION = "citation";
    public static final String TABLE_CITATION_FAVS = "citation_favoris";
    public static final String TABLE_BADGES = "badges";

    private static final String CREATE_UTILISATEUR =
            "CREATE TABLE " + TABLE_UTILISATEUR + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nom TEXT NOT NULL, " +
                    "email TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "date_creation TEXT NOT NULL" +
                    ");";

    private static final String CREATE_CATALOGUE_ACTIVITE =
            "CREATE TABLE " + TABLE_CATALOGUE_ACTIVITE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "titre TEXT NOT NULL, " +
                    "type_activite TEXT NOT NULL, " +
                    "description TEXT, " +
                    "duree_minutes INTEGER DEFAULT 30, " +
                    "plage_horaire TEXT NOT NULL, " +
                    "xp_gagne INTEGER DEFAULT 10" +
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

    private static final String CREATE_CITATION =
            "CREATE TABLE " + TABLE_CITATION + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "auteur TEXT NOT NULL, " +
                    "texte TEXT NOT NULL" +
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
        db.execSQL(CREATE_CATALOGUE_ACTIVITE);
        db.execSQL(CREATE_ACTIVITE);
        db.execSQL(CREATE_PLANNING);
        db.execSQL(CREATE_PROGRESSION);
        db.execSQL(CREATE_SUIVI_EMOTIONNEL);
        db.execSQL(CREATE_JOURNAL);
        db.execSQL(CREATE_CITATION);
        db.execSQL(CREATE_CITATION_FAVS);
        db.execSQL(CREATE_BADGES);

        seedCatalogueActivites(db);
        seedCitations(db);
    }

    private void seedCatalogueActivites(SQLiteDatabase db) {
        insererActiviteCatalogue(db, "Citation du jour", "mentale", "Lire et réfléchir à une citation motivante.", 5, "matin", 10);
        insererActiviteCatalogue(db, "Lecture stoïcienne", "mentale", "Lire un passage inspirant pour développer sa sagesse et sa réflexion.", 15, "matin", 10);
        insererActiviteCatalogue(db, "Planification des objectifs", "mentale", "Définir clairement ses objectifs à court ou long terme.", 10, "matin", 10);
        insererActiviteCatalogue(db, "Visualisation mentale", "mentale", "Imaginer ses objectifs et les actions pour les atteindre.", 10, "matin", 10);

        insererActiviteCatalogue(db, "Respiration profonde", "emotionnelle", "Respirer lentement pour se calmer et réduire le stress.", 5, "matin", 10);
        insererActiviteCatalogue(db, "Gratitude", "emotionnelle", "Noter des choses positives pour améliorer son état d’esprit.", 7, "matin", 10);
        insererActiviteCatalogue(db, "Affirmations positives", "emotionnelle", "Répéter des phrases positives pour se motiver.", 5, "matin", 10);
        insererActiviteCatalogue(db, "Visualisation positive", "emotionnelle", "Imaginer des situations positives pour renforcer la confiance.", 10, "matin", 10);

        insererActiviteCatalogue(db, "Réveil tôt", "discipline", "Se lever tôt pour bien commencer la journée.", 10, "matin", 10);
        insererActiviteCatalogue(db, "Douche froide", "discipline", "Prendre une douche froide pour renforcer la volonté.", 5, "matin", 10);
        insererActiviteCatalogue(db, "Routine matinale", "discipline", "Suivre une routine productive le matin.", 20, "matin", 10);
        insererActiviteCatalogue(db, "Planification du jour", "discipline", "Organiser les tâches de la journée.", 10, "matin", 10);

        insererActiviteCatalogue(db, "Apprendre un nouveau concept", "mentale", "Découvrir une nouvelle notion pour progresser.", 20, "journee", 10);
        insererActiviteCatalogue(db, "Résolution de problème", "mentale", "Analyser un problème et chercher une solution efficace.", 15, "journee", 10);
        insererActiviteCatalogue(db, "Brainstorming", "mentale", "Générer rapidement plusieurs idées sans se limiter.", 10, "journee", 10);
        insererActiviteCatalogue(db, "Apprentissage d’une langue", "mentale", "Pratiquer une langue étrangère pour s’améliorer.", 20, "journee", 10);
        insererActiviteCatalogue(db, "Lecture d’article scientifique", "mentale", "Lire un article pour enrichir ses connaissances.", 15, "journee", 10);
        insererActiviteCatalogue(db, "Focus session", "mentale", "Se concentrer intensément sur une tâche sans distraction.", 25, "journee", 10);

        insererActiviteCatalogue(db, "Marche consciente", "emotionnelle", "Marcher en étant attentif à ses sensations et à l’environnement.", 15, "journee", 10);
        insererActiviteCatalogue(db, "Pause sans écran", "emotionnelle", "Faire une pause loin des écrans pour se reposer.", 10, "journee", 10);
        insererActiviteCatalogue(db, "Observer la nature", "emotionnelle", "Prendre un moment pour observer et se reconnecter à la nature.", 10, "journee", 10);
        insererActiviteCatalogue(db, "Contenu inspirant", "emotionnelle", "Regarder ou écouter un contenu motivant.", 10, "journee", 10);

        insererActiviteCatalogue(db, "Organisation bureau", "discipline", "Mettre de l’ordre dans son espace de travail.", 10, "journee", 10);
        insererActiviteCatalogue(db, "Sport léger", "discipline", "Faire une activité physique douce pour rester actif.", 20, "journee", 10);
        insererActiviteCatalogue(db, "Exercice intense", "discipline", "Faire du sport plus intense pour améliorer sa condition physique.", 30, "journee", 10);
        insererActiviteCatalogue(db, "Deep Work", "discipline", "Travailler profondément sans distraction.", 30, "journee", 10);
        insererActiviteCatalogue(db, "Étude concentrée", "discipline", "Étudier avec concentration pendant un temps défini.", 25, "journee", 10);
        insererActiviteCatalogue(db, "Révision cours", "discipline", "Revoir les leçons pour mieux les mémoriser.", 20, "journee", 10);
        insererActiviteCatalogue(db, "Gestion des tâches", "discipline", "Organiser et prioriser ses tâches.", 10, "journee", 10);
        insererActiviteCatalogue(db, "Limiter réseaux sociaux", "discipline", "Réduire l’utilisation des réseaux sociaux.", 15, "journee", 10);

        insererActiviteCatalogue(db, "Réflexion personnelle", "mentale", "Prendre du recul pour analyser ses pensées et ses actions.", 10, "soir", 10);
        insererActiviteCatalogue(db, "Écriture d’idées", "mentale", "Noter ses idées pour mieux les organiser.", 10, "soir", 10);
        insererActiviteCatalogue(db, "Résumé d’un livre", "mentale", "Synthétiser les idées principales d’un livre.", 15, "soir", 10);
        insererActiviteCatalogue(db, "Analyse d’une journée passée", "mentale", "Faire le bilan de sa journée pour s’améliorer.", 10, "soir", 10);

        insererActiviteCatalogue(db, "Journal émotionnel", "emotionnelle", "Écrire ses émotions pour mieux les comprendre.", 10, "soir", 10);
        insererActiviteCatalogue(db, "Méditation calme", "emotionnelle", "Se poser en silence pour apaiser son esprit.", 15, "soir", 10);
        insererActiviteCatalogue(db, "Écouter musique relaxante", "emotionnelle", "Écouter une musique douce pour se détendre.", 10, "soir", 10);
        insererActiviteCatalogue(db, "Relaxation guidée", "emotionnelle", "Suivre une séance pour relâcher les tensions.", 10, "soir", 10);
        insererActiviteCatalogue(db, "Écriture libre", "emotionnelle", "Écrire librement ce que l’on ressent sans réfléchir.", 10, "soir", 10);
        insererActiviteCatalogue(db, "Déconnexion mentale", "emotionnelle", "Se libérer des pensées stressantes et se détendre.", 15, "soir", 10);

        insererActiviteCatalogue(db, "Nettoyage chambre", "discipline", "Ranger et nettoyer son espace personnel.", 15, "soir", 10);
        insererActiviteCatalogue(db, "Préparation du lendemain", "discipline", "Préparer les affaires et les objectifs du jour suivant.", 10, "soir", 10);
        insererActiviteCatalogue(db, "Routine soir", "discipline", "Mettre en place une routine calme avant de dormir.", 15, "soir", 10);
    }

    private void insererActiviteCatalogue(SQLiteDatabase db, String titre, String type, String description, int duree, String plage, int xp) {
        ContentValues values = new ContentValues();
        values.put("titre", titre);
        values.put("type_activite", type);
        values.put("description", description);
        values.put("duree_minutes", duree);
        values.put("plage_horaire", plage);
        values.put("xp_gagne", xp);
        db.insert(TABLE_CATALOGUE_ACTIVITE, null, values);
    }

    private void seedCitations(SQLiteDatabase db) {
        insererCitation(db, "Marc Aurèle", "Tu as du pouvoir sur ton esprit, pas sur les événements extérieurs.");
        insererCitation(db, "Marc Aurèle", "Les obstacles à l'action font avancer l'action. Ce qui bloque devient le chemin.");
        insererCitation(db, "Marc Aurèle", "L'âme est teinte de la couleur de ses pensées.");
        insererCitation(db, "Marc Aurèle", "Le bonheur de ta vie dépend de la qualité de tes pensées.");
        insererCitation(db, "Marc Aurèle", "Confine-toi au présent.");
        insererCitation(db, "Marc Aurèle", "Agis comme si chaque acte était le dernier de ta vie.");
        insererCitation(db, "Marc Aurèle", "Ne gâche pas le temps qui reste à ruminer sur les autres.");

        insererCitation(db, "Épictète", "Ce n'est pas ce qui arrive qui trouble, mais les opinions qu'on en a.");
        insererCitation(db, "Épictète", "Il y a deux choses : ce qui dépend de nous et ce qui n'en dépend pas.");
        insererCitation(db, "Épictète", "Nous souffrons davantage en imagination que dans la réalité.");
        insererCitation(db, "Épictète", "La liberté naît en éliminant le désir, non en le satisfaisant.");
        insererCitation(db, "Épictète", "Aspire seulement aux choses qui sont en ton pouvoir.");
        insererCitation(db, "Épictète", "La difficulté révèle ce qu'un homme vaut vraiment.");
        insererCitation(db, "Épictète", "Toute grande œuvre a d'abord semblé impossible.");

        insererCitation(db, "Sénèque", "Seul le temps est vraiment à nous.");
        insererCitation(db, "Sénèque", "Pendant que tu remets à plus tard, la vie passe.");
        insererCitation(db, "Sénèque", "Le bonheur n'est pas de posséder beaucoup, mais d'en désirer peu.");
        insererCitation(db, "Sénèque", "Ce n'est pas parce que les choses sont difficiles que nous n'osons pas.");
        insererCitation(db, "Sénèque", "Commence. Qui commence a la moitié de la chose faite.");
        insererCitation(db, "Sénèque", "La chance, c'est quand la préparation rencontre l'opportunité.");
    }

    private void insererCitation(SQLiteDatabase db, String auteur, String texte) {
        ContentValues values = new ContentValues();
        values.put("auteur", auteur);
        values.put("texte", texte);
        db.insert(TABLE_CITATION, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BADGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITATION_FAVS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOURNAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUIVI_EMOTIONNEL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRESSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANNING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATALOGUE_ACTIVITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UTILISATEUR);
        onCreate(db);
    }
}