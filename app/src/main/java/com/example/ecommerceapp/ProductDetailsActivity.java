package com.example.ecommerceapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.models.Produit;
import com.example.ecommerceapp.utils.PanierManager;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView imageViewProduit;
    private TextView txtNomProduit, txtPrixProduit, txtDescription;
    private Button btnAjouterPanier;
    private Produit produit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        // Initialiser les vues
        initViews();

        // Charger les données du produit
        loadProduitData();

        // Configurer les listeners
        setupListeners();
    }

    private void initViews() {
        imageViewProduit = findViewById(R.id.imageViewProduitDetail);
        txtNomProduit = findViewById(R.id.txtNomProduitDetail);
        txtPrixProduit = findViewById(R.id.txtPrixProduitDetail);
        txtDescription = findViewById(R.id.txtDescriptionProduit);
        btnAjouterPanier = findViewById(R.id.btnAjouterAuPanier);
    }

    private void loadProduitData() {
        // Récupérer les données passées depuis MainActivity via Intent
        String id = getIntent().getStringExtra("produit_id");
        String nom = getIntent().getStringExtra("produit_nom");
        double prix = getIntent().getDoubleExtra("produit_prix", 0.0);
        String description = getIntent().getStringExtra("produit_description");
        String imageUrl = getIntent().getStringExtra("produit_image");

        // Créer l'objet Produit avec les données reçues
        produit = new Produit(id, nom, prix, description, imageUrl);

        // Afficher les informations du produit
        txtNomProduit.setText(nom);
        txtPrixProduit.setText(String.format("Prix : %.2f DT", prix));

        // Afficher la description (ou un message par défaut si vide)
        if (description != null && !description.isEmpty()) {
            txtDescription.setText(description);
        } else {
            txtDescription.setText("Aucune description disponible pour ce produit.");
        }

        // Charger l'image avec Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .centerCrop()
                .into(imageViewProduit);
    }

    private void setupListeners() {
        // Bouton Ajouter au Panier
        btnAjouterPanier.setOnClickListener(v -> ajouterAuPanier());
    }

    private void ajouterAuPanier() {
        // Ajouter le produit au panier via le PanierManager (Singleton)
        PanierManager.getInstance().ajouterProduit(produit);

        // Afficher un message de confirmation
        Toast.makeText(this,
                produit.getNom() + " ajouté au panier ✓",
                Toast.LENGTH_SHORT).show();

        // Animation visuelle du bouton
        btnAjouterPanier.setEnabled(false);
        btnAjouterPanier.setText("✓ Ajouté au panier");
        btnAjouterPanier.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

        // Réactiver le bouton après 1 seconde
        btnAjouterPanier.postDelayed(() -> {
            btnAjouterPanier.setEnabled(true);
            btnAjouterPanier.setText(R.string.ajouter_au_panier);
            btnAjouterPanier.setBackgroundColor(getResources().getColor(R.color.primary));
        }, 1000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Gérer le bouton retour de l'ActionBar
        onBackPressed();
        return true;
    }
}