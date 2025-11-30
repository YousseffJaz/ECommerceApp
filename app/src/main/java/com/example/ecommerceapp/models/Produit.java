package com.example.ecommerceapp.models;

public class Produit {
    private String id;
    private String nom;
    private double prix;
    private String description;
    private String imageUrl;

    // Constructeur vide requis pour Firebase
    public Produit() {
    }

    public Produit(String id, String nom, double prix, String description, String imageUrl) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}