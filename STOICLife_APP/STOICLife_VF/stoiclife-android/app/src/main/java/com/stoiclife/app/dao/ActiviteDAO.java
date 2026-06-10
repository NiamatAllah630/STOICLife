package com.stoiclife.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Activite;

import java.util.ArrayList;
import java.util.List;


public class ActiviteDAO {

    private final DatabaseHelper dbHelper;

    public ActiviteDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }


    
    public long inserer(Activite a) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("titre", a.getTitre());
        values.put("type_activite", a.getTypeActivite());
        values.put("description", a.getDescription());
        values.put("duree_minutes", a.getDureeMinutes());
        values.put("statut", a.getStatut());
        values.put("date_planifiee", a.getDatePlanifiee());
        values.put("utilisateur_id", a.getUtilisateurId());
        values.put("xp_gagne", a.getXpGagne());
        values.put("plage_horaire", a.getPlageHoraire());

        long id = db.insert(DatabaseHelper.TABLE_ACTIVITE, null, values);
        db.close();
        return id;
    }

    
    public List<Activite> getByDate(int userId, String date) {
        List<Activite> liste = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ACTIVITE,
                null,
                "utilisateur_id = ? AND date_planifiee = ?",
                new String[]{String.valueOf(userId), date},
                null, null, "id DESC"
        );

        while (cursor.moveToNext()) {
            liste.add(cursorToActivite(cursor));
        }

        cursor.close();
        db.close();
        return liste;
    }

    /* Récupérer toutes les activités */
    public List<Activite> getAll(int userId) {
        List<Activite> liste = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ACTIVITE,
                null,
                "utilisateur_id = ?",
                new String[]{String.valueOf(userId)},
                null, null, "date_planifiee DESC"
        );

        while (cursor.moveToNext()) {
            liste.add(cursorToActivite(cursor));
        }

        cursor.close();
        db.close();
        return liste;
    }

    
    public void marquerTerminee(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("statut", "terminee");

        db.update(DatabaseHelper.TABLE_ACTIVITE, values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    
    public void ajouterXpUtilisateur(int activiteId, int xp) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        
        Cursor cursor = db.rawQuery(
                "SELECT utilisateur_id FROM " + DatabaseHelper.TABLE_ACTIVITE + " WHERE id = ?",
                new String[]{String.valueOf(activiteId)}
        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return;
        }

        int userId = cursor.getInt(0);
        cursor.close();

        
        db.execSQL(
                "UPDATE " + DatabaseHelper.TABLE_PROGRESSION +
                        " SET points_xp = points_xp + ?, " +
                        " nombre_activites_completees = nombre_activites_completees + 1 " +
                        " WHERE utilisateur_id = ?",
                new Object[]{xp, userId}
        );

        
        Cursor c2 = db.rawQuery(
                "SELECT points_xp FROM " + DatabaseHelper.TABLE_PROGRESSION +
                        " WHERE utilisateur_id = ?",
                new String[]{String.valueOf(userId)}
        );

        int totalXp = 0;
        if (c2.moveToFirst()) {
            totalXp = c2.getInt(0);
        }
        c2.close();

        
        int level = (totalXp / 100) + 1;

        String niveau;
        if (level < 3) niveau = "Débutant";
        else if (level < 5) niveau = "Motivé";
        else if (level < 10) niveau = "Discipliné";
        else niveau = "Stoïcien";

        
        ContentValues values = new ContentValues();
        values.put("niveau_actuel", niveau);

        db.update(
                DatabaseHelper.TABLE_PROGRESSION,
                values,
                "utilisateur_id = ?",
                new String[]{String.valueOf(userId)}
        );

        
        verifierBadges(db, userId);

        db.close();
    }

    private void verifierBadges(SQLiteDatabase db, int userId) {

        Cursor cursor = db.rawQuery(
                "SELECT points_xp, nombre_activites_completees FROM progression WHERE utilisateur_id = ?",
                new String[]{String.valueOf(userId)}
        );

        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        int xp = cursor.getInt(0);
        int nb = cursor.getInt(1);
        cursor.close();

        if (nb == 1)
            ajouterBadge(db, userId, "Première Flamme", "1ère activité");

        if (nb == 5)
            ajouterBadge(db, userId, "Maître de Soi", "5 activités");

        if (xp >= 20)
            ajouterBadge(db, userId, "Esprit Éveillé", "20 XP gagnés");

        if (xp >= 100)
            ajouterBadge(db, userId, "Cœur Stoïcien", "100 XP gagnés");

        if (nb >= 50)
            ajouterBadge(db, userId, "Philosophe", "50 activités");
    }

  
    public void marquerEnCours(int activiteId) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("statut", "en_cours");

        db.update(
                DatabaseHelper.TABLE_ACTIVITE,
                values,
                "id = ?",
                new String[]{String.valueOf(activiteId)}
        );

        db.close();
    }
   
    public Activite getById(int id) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ACTIVITE,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Activite activite = null;

        if (cursor.moveToFirst()) {
            activite = new Activite();

            activite.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            activite.setTitre(cursor.getString(cursor.getColumnIndexOrThrow("titre")));
            activite.setTypeActivite(cursor.getString(cursor.getColumnIndexOrThrow("type_activite")));
            activite.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
            activite.setDureeMinutes(cursor.getInt(cursor.getColumnIndexOrThrow("duree_minutes")));
            activite.setStatut(cursor.getString(cursor.getColumnIndexOrThrow("statut")));
            activite.setDatePlanifiee(cursor.getString(cursor.getColumnIndexOrThrow("date_planifiee")));
            activite.setUtilisateurId(cursor.getInt(cursor.getColumnIndexOrThrow("utilisateur_id")));
            activite.setXpGagne(cursor.getInt(cursor.getColumnIndexOrThrow("xp_gagne")));
            activite.setPlageHoraire(cursor.getString(cursor.getColumnIndexOrThrow("plage_horaire")));
        }

        cursor.close();
        db.close();

        return activite;
    }
    
    public void supprimer(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(
                DatabaseHelper.TABLE_ACTIVITE,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
    }
    public void marquerPlanifiee(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("statut", "planifiee");

        db.update(
                DatabaseHelper.TABLE_ACTIVITE,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
    }
    
    public int modifier(Activite a) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("titre", a.getTitre());
        values.put("type_activite", a.getTypeActivite());
        values.put("description", a.getDescription());
        values.put("duree_minutes", a.getDureeMinutes());
        values.put("date_planifiee", a.getDatePlanifiee());
        values.put("plage_horaire", a.getPlageHoraire());
        values.put("utilisateur_id", a.getUtilisateurId());

        int result = db.update(
                DatabaseHelper.TABLE_ACTIVITE,
                values,
                "id = ?",
                new String[]{String.valueOf(a.getId())}
        );

        db.close();
        return result;
    }
    
    public int compterTerminees() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int count = 0;

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ACTIVITE + " WHERE statut = ?",
                new String[]{"terminee"}
        );

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }
    
    public int compterTerminees(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int count = 0;

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_ACTIVITE +
                        " WHERE utilisateur_id = ? AND statut = ?",
                new String[]{String.valueOf(userId), "terminee"}
        );

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return count;
    }

    private void ajouterBadge(SQLiteDatabase db, int userId, String nom, String desc) {

        Cursor c = db.rawQuery(
                "SELECT * FROM badges WHERE utilisateur_id = ? AND nom = ?",
                new String[]{String.valueOf(userId), nom}
        );

        if (c.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("utilisateur_id", userId);
            values.put("nom", nom);
            values.put("description", desc);
            values.put("date_obtention", System.currentTimeMillis());

            db.insert("badges", null, values);

            
        }

        c.close();
    }
    
    public List<Activite> getTerminees(int userId) {
        List<Activite> liste = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_ACTIVITE,
                null,
                "utilisateur_id = ? AND statut = ?",
                new String[]{String.valueOf(userId), "terminee"},
                null,
                null,
                "date_planifiee DESC"
        );

        while (cursor.moveToNext()) {
            liste.add(cursorToActivite(cursor));
        }

        cursor.close();
        db.close();

        return liste;
    }
    private int getXpByActivite(String titre) {
        String t = titre.toLowerCase();

        if (t.contains("respiration") || t.contains("relaxation")) return 5;
        if (t.contains("méditation") || t.contains("visualisation")) return 8;
        if (t.contains("focus") || t.contains("deep") || t.contains("étude")) return 15;
        if (t.contains("sport") || t.contains("exercice")) return 20;
        if (t.contains("journal") || t.contains("écriture")) return 10;

        return 10;
    }


    public List<String[]> getCatalogueParPlageEtType(String plage, String type) {

        List<String[]> liste = new ArrayList<String[]>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT titre, description, duree_minutes " +
                        "FROM " + DatabaseHelper.TABLE_CATALOGUE_ACTIVITE +
                        " WHERE plage_horaire = ? AND type_activite = ?",
                new String[]{plage, type}
        );

        while (cursor.moveToNext()) {

            String titre = cursor.getString(0);
            String description = cursor.getString(1);
            String duree = String.valueOf(cursor.getInt(2));

            liste.add(new String[]{
                    titre,
                    description,
                    duree
            });
        }

        cursor.close();
        db.close();

        return liste;
    }
    public List<String[]> getToutesCitations() {

        List<String[]> liste = new ArrayList<String[]>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT auteur, texte FROM " +
                        DatabaseHelper.TABLE_CITATION,
                null
        );

        while (cursor.moveToNext()) {

            String auteur = cursor.getString(0);
            String texte = cursor.getString(1);

            liste.add(new String[]{
                    auteur,
                    texte
            });
        }

        cursor.close();
        db.close();

        return liste;
    }
    public List<String[]> getCatalogueParType(String type) {

        List<String[]> liste = new ArrayList<String[]>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT titre, description, duree_minutes " +
                        "FROM " + DatabaseHelper.TABLE_CATALOGUE_ACTIVITE +
                        " WHERE type_activite = ? " +
                        "GROUP BY titre " +
                        "ORDER BY titre ASC",
                new String[]{type}
        );

        while (cursor.moveToNext()) {

            String titre = cursor.getString(0);
            String description = cursor.getString(1);
            String duree = String.valueOf(cursor.getInt(2));

            liste.add(new String[]{
                    titre,
                    description,
                    duree
            });
        }

        cursor.close();
        db.close();

        return liste;
    }
    private Activite cursorToActivite(Cursor c) {
        Activite a = new Activite();

        a.setId(c.getInt(c.getColumnIndexOrThrow("id")));
        a.setTitre(c.getString(c.getColumnIndexOrThrow("titre")));
        a.setTypeActivite(c.getString(c.getColumnIndexOrThrow("type_activite")));
        a.setDescription(c.getString(c.getColumnIndexOrThrow("description")));
        a.setDureeMinutes(c.getInt(c.getColumnIndexOrThrow("duree_minutes")));
        a.setStatut(c.getString(c.getColumnIndexOrThrow("statut")));
        a.setDatePlanifiee(c.getString(c.getColumnIndexOrThrow("date_planifiee")));
        a.setUtilisateurId(c.getInt(c.getColumnIndexOrThrow("utilisateur_id")));
        a.setXpGagne(c.getInt(c.getColumnIndexOrThrow("xp_gagne")));
        a.setPlageHoraire(c.getString(c.getColumnIndexOrThrow("plage_horaire")));

        return a;
    }
}