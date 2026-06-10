package com.stoiclife.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.PlanningJour;


public class PlanningDAO {

    private final DatabaseHelper dbHelper;

    public PlanningDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

   
    public long upsert(PlanningJour p) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date_jour",       p.getDateJour());
        values.put("resume_matin",    p.getResumeMatin());
        values.put("resume_journee",  p.getResumeJournee());
        values.put("resume_soir",     p.getResumeSoir());
        values.put("utilisateur_id",  p.getUtilisateurId());
        long id = db.insertWithOnConflict(
            DatabaseHelper.TABLE_PLANNING, null, values,
            SQLiteDatabase.CONFLICT_REPLACE
        );
        db.close();
        return id;
    }

  
    public PlanningJour getByDate(int utilisateurId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_PLANNING, null,
            "utilisateur_id = ? AND date_jour = ?",
            new String[]{String.valueOf(utilisateurId), date},
            null, null, null
        );
        PlanningJour p = null;
        if (cursor.moveToFirst()) p = cursorToPlanning(cursor);
        cursor.close();
        db.close();
        return p;
    }

    private PlanningJour cursorToPlanning(Cursor c) {
        PlanningJour p = new PlanningJour();
        p.setId(c.getInt(c.getColumnIndexOrThrow("id")));
        p.setDateJour(c.getString(c.getColumnIndexOrThrow("date_jour")));
        p.setResumeMatin(c.getString(c.getColumnIndexOrThrow("resume_matin")));
        p.setResumeJournee(c.getString(c.getColumnIndexOrThrow("resume_journee")));
        p.setResumeSoir(c.getString(c.getColumnIndexOrThrow("resume_soir")));
        p.setUtilisateurId(c.getInt(c.getColumnIndexOrThrow("utilisateur_id")));
        return p;
    }
}
