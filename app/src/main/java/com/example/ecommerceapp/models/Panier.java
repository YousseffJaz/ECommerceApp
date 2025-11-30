package com.example.ecommerceapp.models;

import java.util.ArrayList;
import java.util.List;

public class Panier {
    private String idUtilisateur;
    private List<Produit> produits;
    private double total;

    // Constructeur vide requis pour Firebase
    public Panier() {
        this.produits = new ArrayList<>();
        this.total = 0.0;
    }

    public Panier(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
        this.produits = new ArrayList<>();
        this.total = 0.0;
    }

    // Méthodes utilitaires
    public void ajouterProduit(Produit produit) {
        this.produits.add(produit);
        calculerTotal();
    }

    public void retirerProduit(Produit produit) {
        this.produits.remove(produit);
        calculerTotal();
    }

    public void vider() {
        this.produits.clear();
        this.total = 0.0;
    }

    private void calculerTotal() {
        this.total = 0.0;
        for (Produit p : produits) {
            this.total += p.getPrix();
        }
    }

    // Getters et Setters
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
        calculerTotal();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}