package com.stoiclife.app.model;

public class Activite {

    private int id;
    private String titre;
    private String typeActivite;   // mentale | emotionnelle | discipline
    private String description;
    private int dureeMinutes;
    private String statut;         // planifiee | en_cours | terminee
    private String datePlanifiee;  // YYYY-MM-DD
    private int utilisateurId;
    private int xpGagne;
    private String plageHoraire; // matin | journee | soir

    public Activite() {}

    public Activite(String titre, String typeActivite, String description,
                    int dureeMinutes, String datePlanifiee, int utilisateurId) {
        this.titre = titre;
        this.typeActivite = typeActivite;
        this.description = description;
        this.dureeMinutes = dureeMinutes;
        this.statut = "planifiee";
        this.datePlanifiee = datePlanifiee;
        this.utilisateurId = utilisateurId;
        this.xpGagne = 10;
    }

   
    public int getId() { return id; }
    public String getPlageHoraire() { return plageHoraire; }
    public String getTitre() { return titre; }
    public String getTypeActivite() { return typeActivite; }
    public String getDescription() { return description; }
    public int getDureeMinutes() { return dureeMinutes; }
    public String getStatut() { return statut; }
    public String getDatePlanifiee() { return datePlanifiee; }
    public int getUtilisateurId() { return utilisateurId; }
    public int getXpGagne() { return xpGagne; }

   
    public void setPlageHoraire(String plageHoraire) {
        this.plageHoraire = plageHoraire;
    }
    public void setId(int id) { this.id = id; }
    public void setTitre(String titre) { this.titre = titre; }
    public void setTypeActivite(String typeActivite) { this.typeActivite = typeActivite; }
    public void setDescription(String description) { this.description = description; }
    public void setDureeMinutes(int dureeMinutes) { this.dureeMinutes = dureeMinutes; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setDatePlanifiee(String datePlanifiee) { this.datePlanifiee = datePlanifiee; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    public void setXpGagne(int xpGagne) { this.xpGagne = xpGagne; }

    public boolean isTerminee() { return "terminee".equals(statut); }
    public boolean isEnCours() { return "en_cours".equals(statut); }
}
