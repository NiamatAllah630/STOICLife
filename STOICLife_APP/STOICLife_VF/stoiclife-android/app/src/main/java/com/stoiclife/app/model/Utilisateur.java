package com.stoiclife.app.model;

public class Utilisateur {

    private int id;
    private String nom;
    private String email;
    private String password;
    private String dateCreation;

    private int xp;
    private int niveau;

    public Utilisateur() {}

    public Utilisateur(String nom, String email, String password, String dateCreation) {
        this.nom = nom;
        this.email = email;
        this.password = password;
        this.dateCreation = dateCreation;
        
        this.xp = 0;
        this.niveau = 1;
    }

    
    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getDateCreation() { return dateCreation; }

    public int getXp() { return xp; }
    public int getNiveau() { return niveau; }

    
    public void setId(int id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setDateCreation(String dateCreation) { this.dateCreation = dateCreation; }

    public void setXp(int xp) { this.xp = xp; }
    public void setNiveau(int niveau) { this.niveau = niveau; }

    
    public void ajouterXp(int valeur) {
        this.xp += valeur;
        this.niveau = (this.xp / 100) + 1;
    }
}