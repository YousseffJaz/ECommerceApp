package com.example.ecommerceapp.utils;

import com.example.ecommerceapp.models.Panier;
import com.example.ecommerceapp.models.Produit;

public class PanierManager {
    private static PanierManager instance;
    private Panier panier;

    private PanierManager() {
        panier = new Panier();
    }

    public static synchronized PanierManager getInstance() {
        if (instance == null) {
            instance = new PanierManager();
        }
        return instance;
    }

    public Panier getPanier() {
        return panier;
    }

    public void ajouterProduit(Produit produit) {
        panier.ajouterProduit(produit);
    }

    public void retirerProduit(Produit produit) {
        panier.retirerProduit(produit);
    }

    public void viderPanier() {
        panier.vider();
    }

    public double getTotal() {
        return panier.getTotal();
    }

    public int getNombreProduits() {
        return panier.getProduits().size();
    }
}