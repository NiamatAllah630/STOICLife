package com.stoiclife.app.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SessionManager {

    private static final String PREFS_NAME    = "stoiclife_session";
    private static final String KEY_USER_ID   = "user_id";
    private static final String KEY_USER_NOM  = "user_nom";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_ONBOARDED = "onboarded";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    
    public void connecter(int userId, String nom) {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putInt(KEY_USER_ID, userId)
            .putString(KEY_USER_NOM, nom)
            .apply();
    }

    
    public void deconnecter() {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, false)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_NOM)
            .apply();
    }

    public boolean estConnecte() {
        return prefs.getBoolean(KEY_LOGGED_IN, false);
    }

    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getUserNom() {
        return prefs.getString(KEY_USER_NOM, "Stoïcien");
    }

    public boolean estOnboarde() {
        return prefs.getBoolean(KEY_ONBOARDED, false);
    }

    public void setOnboarde() {
        prefs.edit().putBoolean(KEY_ONBOARDED, true).apply();
    }
}
