package com.stoiclife.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Progression;


public class ProgressionDAO {

    private final DatabaseHelper dbHelper;

    public ProgressionDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    
    public long inserer(int utilisateurId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("utilisateur_id",              utilisateurId);
        values.put("points_xp",                   0);
        values.put("niveau_actuel",               "Débutant");
        values.put("streak_actuel",               0);
        values.put("nombre_activites_completees", 0);
        long id = db.insert(DatabaseHelper.TABLE_PROGRESSION, null, values);
        db.close();
        return id;
    }

   
    public Progression getByUtilisateur(int utilisateurId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_PROGRESSION, null,
            "utilisateur_id = ?",
            new String[]{String.valueOf(utilisateurId)},
            null, null, null
        );
        Progression p = null;
        if (cursor.moveToFirst()) {
            p = cursorToProgression(cursor);
        } else {
            // Creation automatique si manquante
            cursor.close();
            db.close();
            inserer(utilisateurId);
            return getByUtilisateur(utilisateurId);
        }
        cursor.close();
        db.close();
        return p;
    }

    
    public void ajouterXP(int utilisateurId, int xp) {
        Progression p = getByUtilisateur(utilisateurId);
        int newXp = p.getPointsXp() + xp;
        int newActivites = p.getNombreActivitesCompletees() + 1;
        String nouveauNiveau = Progression.calculerNiveau(newXp);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("points_xp",                   newXp);
        values.put("niveau_actuel",               nouveauNiveau);
        values.put("nombre_activites_completees", newActivites);
        db.update(DatabaseHelper.TABLE_PROGRESSION, values,
            "utilisateur_id = ?", new String[]{String.valueOf(utilisateurId)});
        db.close();
    }

    
    public void updateStreak(int utilisateurId, int streak) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("streak_actuel", streak);
        db.update(DatabaseHelper.TABLE_PROGRESSION, values,
            "utilisateur_id = ?", new String[]{String.valueOf(utilisateurId)});
        db.close();
    }

    private Progression cursorToProgression(Cursor c) {
        Progression p = new Progression();
        p.setId(c.getInt(c.getColumnIndexOrThrow("id")));
        p.setUtilisateurId(c.getInt(c.getColumnIndexOrThrow("utilisateur_id")));
        p.setPointsXp(c.getInt(c.getColumnIndexOrThrow("points_xp")));
        p.setNiveauActuel(c.getString(c.getColumnIndexOrThrow("niveau_actuel")));
        p.setStreakActuel(c.getInt(c.getColumnIndexOrThrow("streak_actuel")));
        p.setNombreActivitesCompletees(c.getInt(c.getColumnIndexOrThrow("nombre_activites_completees")));
        return p;
    }
}
