package com.stoiclife.app.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stoiclife.app.database.DatabaseHelper;
import com.stoiclife.app.model.Utilisateur;


public class UtilisateurDAO {

    private final DatabaseHelper dbHelper;

    public UtilisateurDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

   
    public long inserer(Utilisateur u) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String email = u.getEmail() != null
                ? u.getEmail().trim().toLowerCase()
                : "";

        String password = u.getPassword() != null
                ? u.getPassword().trim()
                : "";

        ContentValues values = new ContentValues();
        values.put("nom", u.getNom());
        values.put("email", email);
        values.put("password", password);
        values.put("date_creation", u.getDateCreation());

        long id = db.insert(DatabaseHelper.TABLE_UTILISATEUR, null, values);
        db.close();

        return id;
    }

    
    public Utilisateur connecter(String email, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String emailClean = email != null
                ? email.trim().toLowerCase()
                : "";

        String passwordClean = password != null
                ? password.trim()
                : "";

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UTILISATEUR,
                null,
                "LOWER(email) = ? AND password = ?",
                new String[]{emailClean, passwordClean},
                null,
                null,
                null
        );

        Utilisateur u = null;

        if (cursor.moveToFirst()) {
            u = cursorToUtilisateur(cursor);
        }

        cursor.close();
        db.close();

        return u;
    }

    
    public boolean emailExiste(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String emailClean = email != null
                ? email.trim().toLowerCase()
                : "";

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UTILISATEUR,
                new String[]{"id"},
                "LOWER(email) = ?",
                new String[]{emailClean},
                null,
                null,
                null
        );

        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return existe;
    }

   
    public Utilisateur getById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UTILISATEUR,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Utilisateur u = null;

        if (cursor.moveToFirst()) {
            u = cursorToUtilisateur(cursor);
        }

        cursor.close();
        db.close();

        return u;
    }

   
    public void updateNom(int id, String nouveauNom) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nom", nouveauNom);

        db.update(
                DatabaseHelper.TABLE_UTILISATEUR,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();
    }

    private Utilisateur cursorToUtilisateur(Cursor c) {
        Utilisateur u = new Utilisateur();

        u.setId(c.getInt(c.getColumnIndexOrThrow("id")));
        u.setNom(c.getString(c.getColumnIndexOrThrow("nom")));
        u.setEmail(c.getString(c.getColumnIndexOrThrow("email")));
        u.setPassword(c.getString(c.getColumnIndexOrThrow("password")));
        u.setDateCreation(c.getString(c.getColumnIndexOrThrow("date_creation")));

        return u;
    }
}