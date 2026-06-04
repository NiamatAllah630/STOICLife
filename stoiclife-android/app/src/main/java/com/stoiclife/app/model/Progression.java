package com.stoiclife.app.model;


public class Progression {

    private int id;
    private int utilisateurId;
    private int pointsXp;
    private String niveauActuel;
    private int streakActuel;           // jours consécutifs
    private int nombreActivitesCompletees;

    public Progression() {}

    public Progression(int utilisateurId) {
        this.utilisateurId = utilisateurId;
        this.pointsXp = 0;
        this.niveauActuel = "Débutant";
        this.streakActuel = 0;
        this.nombreActivitesCompletees = 0;
    }

    
    public int getId() { return id; }
    public int getUtilisateurId() { return utilisateurId; }
    public int getPointsXp() { return pointsXp; }
    public String getNiveauActuel() { return niveauActuel; }
    public int getStreakActuel() { return streakActuel; }
    public int getNombreActivitesCompletees() { return nombreActivitesCompletees; }

    
    public void setId(int id) { this.id = id; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    public void setPointsXp(int pointsXp) {
        this.pointsXp = pointsXp;
        this.niveauActuel = calculerNiveau(pointsXp);
    }
    public void setNiveauActuel(String niveauActuel) { this.niveauActuel = niveauActuel; }
    public void setStreakActuel(int streakActuel) { this.streakActuel = streakActuel; }
    public void setNombreActivitesCompletees(int n) { this.nombreActivitesCompletees = n; }

    
    public static String calculerNiveau(int xp) {
        if (xp >= 600) return "Maître Stoïque";
        if (xp >= 300) return "Discipliné";
        if (xp >= 100) return "Régulier";
        return "Débutant";
    }

    
    public int xpPourProchainNiveau() {
        if (pointsXp < 100) return 100 - pointsXp;
        if (pointsXp < 300) return 300 - pointsXp;
        if (pointsXp < 600) return 600 - pointsXp;
        return 0;
    }

    
    public int pourcentageProgression() {
        if (pointsXp < 100) return (pointsXp * 100) / 100;
        if (pointsXp < 300) return ((pointsXp - 100) * 100) / 200;
        if (pointsXp < 600) return ((pointsXp - 300) * 100) / 300;
        return 100;
    }
}
