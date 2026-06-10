package com.stoiclife.app.model;

public class SuiviEmotionnel {

    private int id;
    private int utilisateurId;
    private String humeur;     // Calme | Motivé | Joyeux | Stressé | Triste | Irrité
    private int intensite;     // 1-10
    private String dateJour;   // YYYY-MM-DD
    private String note;

    public SuiviEmotionnel() {}

    public SuiviEmotionnel(int utilisateurId, String humeur, int intensite, String dateJour) {
        this.utilisateurId = utilisateurId;
        this.humeur = humeur;
        this.intensite = intensite;
        this.dateJour = dateJour;
    }

    
    public int getId() { return id; }
    public int getUtilisateurId() { return utilisateurId; }
    public String getHumeur() { return humeur; }
    public int getIntensite() { return intensite; }
    public String getDateJour() { return dateJour; }
    public String getNote() { return note; }

    
    public void setId(int id) { this.id = id; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    public void setHumeur(String humeur) { this.humeur = humeur; }
    public void setIntensite(int intensite) { this.intensite = intensite; }
    public void setDateJour(String dateJour) { this.dateJour = dateJour; }
    public void setNote(String note) { this.note = note; }
}
