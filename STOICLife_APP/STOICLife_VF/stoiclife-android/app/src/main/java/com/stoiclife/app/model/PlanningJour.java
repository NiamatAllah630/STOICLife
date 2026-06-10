package com.stoiclife.app.model;

public class PlanningJour {

    private int id;
    private String dateJour;       // YYYY-MM-DD
    private String resumeMatin;
    private String resumeJournee;
    private String resumeSoir;
    private int utilisateurId;

    public PlanningJour() {}

    public PlanningJour(String dateJour, String resumeMatin, String resumeJournee,
                        String resumeSoir, int utilisateurId) {
        this.dateJour = dateJour;
        this.resumeMatin = resumeMatin;
        this.resumeJournee = resumeJournee;
        this.resumeSoir = resumeSoir;
        this.utilisateurId = utilisateurId;
    }

    
    public int getId() { return id; }
    public String getDateJour() { return dateJour; }
    public String getResumeMatin() { return resumeMatin; }
    public String getResumeJournee() { return resumeJournee; }
    public String getResumeSoir() { return resumeSoir; }
    public int getUtilisateurId() { return utilisateurId; }

    
    public void setId(int id) { this.id = id; }
    public void setDateJour(String dateJour) { this.dateJour = dateJour; }
    public void setResumeMatin(String resumeMatin) { this.resumeMatin = resumeMatin; }
    public void setResumeJournee(String resumeJournee) { this.resumeJournee = resumeJournee; }
    public void setResumeSoir(String resumeSoir) { this.resumeSoir = resumeSoir; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
}
