package com.example.ecommerceapp.models;

import java.util.Date;
import java.util.List;

public class Commande {
    private String id;
    private String idUtilisateur;
    private List<Produit> produits;
    private double total;
    private Date date;

    // Constructeur vide requis pour Firebase
    public Commande() {
    }

    public Commande(String id, String idUtilisateur, List<Produit> produits, double total) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.produits = produits;
        this.total = total;
        this.date = new Date();
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}