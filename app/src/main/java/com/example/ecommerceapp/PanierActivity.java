package com.example.ecommerceapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.adapters.PanierAdapter;
import com.example.ecommerceapp.models.Produit;
import com.example.ecommerceapp.utils.PanierManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanierActivity extends AppCompatActivity implements PanierAdapter.OnProduitRemoveListener {

    private RecyclerView recyclerViewPanier;
    private PanierAdapter panierAdapter;
    private TextView txtTotalPanier, txtPanierVide;
    private Button btnValiderCommande;
    private LinearLayout layoutTotal;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        // Initialiser Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialiser les vues
        initViews();

        // Configurer la toolbar
        setupToolbar();

        // Configurer le RecyclerView
        setupRecyclerView();

        // Mettre à jour l'affichage du panier
        updatePanier();

        // Configurer les listeners
        setupListeners();
    }

    private void initViews() {
        recyclerViewPanier = findViewById(R.id.recyclerViewPanier);
        txtTotalPanier = findViewById(R.id.txtTotalPanier);
        txtPanierVide = findViewById(R.id.txtPanierVide);
        btnValiderCommande = findViewById(R.id.btnValiderCommande);
        layoutTotal = findViewById(R.id.layoutTotal);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarPanier);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mon Panier");
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        // Récupérer les produits du panier
        List<Produit> produits = PanierManager.getInstance().getPanier().getProduits();

        // Créer l'adapter
        panierAdapter = new PanierAdapter((Context) this, produits, (PanierAdapter.OnPanierItemListener) this);

        // Configurer le RecyclerView
        recyclerViewPanier.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPanier.setAdapter(panierAdapter);
    }

    private void updatePanier() {
        List<Produit> produits = PanierManager.getInstance().getPanier().getProduits();
        double total = PanierManager.getInstance().getTotal();

        // Mettre à jour le total
        txtTotalPanier.setText(String.format("%.2f DT", total));

        // Afficher/Masquer les éléments selon l'état du panier
        if (produits.isEmpty()) {
            // Panier vide
            txtPanierVide.setVisibility(View.VISIBLE);
            recyclerViewPanier.setVisibility(View.GONE);
            layoutTotal.setVisibility(View.GONE);
            btnValiderCommande.setEnabled(false);
        } else {
            // Panier contient des produits
            txtPanierVide.setVisibility(View.GONE);
            recyclerViewPanier.setVisibility(View.VISIBLE);
            layoutTotal.setVisibility(View.VISIBLE);
            btnValiderCommande.setEnabled(true);
        }

        // Mettre à jour le titre avec le nombre de produits
        if (getSupportActionBar() != null) {
            String titre = produits.isEmpty() ? "Mon Panier" : "Mon Panier (" + produits.size() + ")";
            getSupportActionBar().setTitle(titre);
        }
    }

    private void setupListeners() {
        btnValiderCommande.setOnClickListener(v -> {
            // Afficher une boîte de dialogue de confirmation
            showConfirmationDialog();
        });
    }

    private void showConfirmationDialog() {
        double total = PanierManager.getInstance().getTotal();
        int nombreProduits = PanierManager.getInstance().getNombreProduits();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmer la commande");
        builder.setMessage("Vous êtes sur le point de valider une commande de " +
                nombreProduits + " produit(s) pour un total de " +
                String.format("%.2f DT", total) + "\n\nConfirmer ?");

        builder.setPositiveButton("Confirmer", (dialog, which) -> {
            validerCommande();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.create().show();
    }

    private void validerCommande() {
        // Vérifier que l'utilisateur est connecté
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Erreur: Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        // Désactiver le bouton pendant le traitement
        btnValiderCommande.setEnabled(false);
        btnValiderCommande.setText("Validation en cours...");

        String userId = mAuth.getCurrentUser().getUid();
        List<Produit> produits = new ArrayList<>(PanierManager.getInstance().getPanier().getProduits());
        double total = PanierManager.getInstance().getTotal();

        // Créer la commande
        Map<String, Object> commande = new HashMap<>();
        commande.put("idUtilisateur", userId);
        commande.put("total", total);
        commande.put("date", new Date());
        commande.put("nombreProduits", produits.size());

        // Convertir les produits en Map pour Firestore
        List<Map<String, Object>> produitsMap = new ArrayList<>();
        for (Produit produit : produits) {
            Map<String, Object> produitMap = new HashMap<>();
            produitMap.put("id", produit.getId());
            produitMap.put("nom", produit.getNom());
            produitMap.put("prix", produit.getPrix());
            produitMap.put("description", produit.getDescription());
            produitMap.put("imageUrl", produit.getImageUrl());
            produitsMap.add(produitMap);
        }
        commande.put("produits", produitsMap);

        // Enregistrer la commande dans Firestore
        db.collection("commandes")
                .add(commande)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this,
                            "Commande validée avec succès !",
                            Toast.LENGTH_LONG).show();

                    // Vider le panier
                    PanierManager.getInstance().viderPanier();

                    // Fermer l'activité
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnValiderCommande.setEnabled(true);
                    btnValiderCommande.setText(R.string.valider_commande);

                    Toast.makeText(this,
                            "Erreur lors de la validation: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onProduitRemove(Produit produit, int position) {
        // Retirer le produit du panier
        PanierManager.getInstance().retirerProduit(produit);

        // Notifier l'adapter
        panierAdapter.notifyItemRemoved(position);

        // Mettre à jour l'affichage
        updatePanier();

        // Afficher un message
        Toast.makeText(this,
                produit.getNom() + " retiré du panier",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mettre à jour le panier quand on revient à cette activité
        updatePanier();
        panierAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}