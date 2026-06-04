package com.stoiclife.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.SuiviEmotionnel;

import java.util.ArrayList;
import java.util.List;


public class SuiviEmotionnelDAO {

    private final DatabaseHelper dbHelper;

    public SuiviEmotionnelDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

   
    public long inserer(SuiviEmotionnel s) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("utilisateur_id", s.getUtilisateurId());
        values.put("humeur",         s.getHumeur());
        values.put("intensite",      s.getIntensite());
        values.put("date_jour",      s.getDateJour());
        values.put("note",           s.getNote() != null ? s.getNote() : "");
        long id = db.insert(DatabaseHelper.TABLE_SUIVI_EMOTIONNEL, null, values);
        db.close();
        return id;
    }

    
    public List<SuiviEmotionnel> getDerniers(int utilisateurId, int limite) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<SuiviEmotionnel> liste = new ArrayList<>();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_SUIVI_EMOTIONNEL, null,
            "utilisateur_id = ?",
            new String[]{String.valueOf(utilisateurId)},
            null, null, "id DESC",
            String.valueOf(limite)
        );
        while (cursor.moveToNext()) liste.add(cursorToSuivi(cursor));
        cursor.close();
        db.close();
        return liste;
    }

    
    public SuiviEmotionnel getByDate(int utilisateurId, String date) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
            DatabaseHelper.TABLE_SUIVI_EMOTIONNEL, null,
            "utilisateur_id = ? AND date_jour = ?",
            new String[]{String.valueOf(utilisateurId), date},
            null, null, "id DESC", "1"
        );
        SuiviEmotionnel s = null;
        if (cursor.moveToFirst()) s = cursorToSuivi(cursor);
        cursor.close();
        db.close();
        return s;
    }

    private SuiviEmotionnel cursorToSuivi(Cursor c) {
        SuiviEmotionnel s = new SuiviEmotionnel();
        s.setId(c.getInt(c.getColumnIndexOrThrow("id")));
        s.setUtilisateurId(c.getInt(c.getColumnIndexOrThrow("utilisateur_id")));
        s.setHumeur(c.getString(c.getColumnIndexOrThrow("humeur")));
        s.setIntensite(c.getInt(c.getColumnIndexOrThrow("intensite")));
        s.setDateJour(c.getString(c.getColumnIndexOrThrow("date_jour")));
        s.setNote(c.getString(c.getColumnIndexOrThrow("note")));
        return s;
    }
}
