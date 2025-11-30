package com.example.ecommerceapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.adapters.ProduitAdapter;
import com.example.ecommerceapp.models.Produit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public abstract class MainActivity extends AppCompatActivity implements ProduitAdapter.OnProduitClickListener {

    private RecyclerView recyclerViewProduits;
    private ProduitAdapter produitAdapter;
    private List<Produit> produitList;
    private List<Produit> filteredList;
    private ProgressBar progressBar;
    private TextView txtNoProduits;
    private EditText editTextSearch;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialiser Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Vérifier si l'utilisateur est connecté
        if (mAuth.getCurrentUser() == null) {
            // Rediriger vers LoginActivity si non connecté
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialiser les vues
        initViews();

        // Configurer la toolbar
        setupToolbar();

        // Configurer le RecyclerView
        setupRecyclerView();

        // Configurer la recherche
        setupSearch();

        // Charger les produits depuis Firestore
        loadProduits();
    }

    private void initViews() {
        recyclerViewProduits = findViewById(R.id.recyclerViewProduits);
        progressBar = findViewById(R.id.progressBarMain);
        txtNoProduits = findViewById(R.id.txtNoProduits);
        editTextSearch = findViewById(R.id.editTextSearch);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Liste des Produits");
        }
    }

    private void setupRecyclerView() {
        produitList = new ArrayList<>();
        filteredList = new ArrayList<>();
        produitAdapter = new ProduitAdapter(this, filteredList, this);

        // Vous pouvez choisir entre LinearLayoutManager ou GridLayoutManager
        // LinearLayoutManager pour une liste verticale
        recyclerViewProduits.setLayoutManager(new LinearLayoutManager(this));

        // Ou GridLayoutManager pour une grille (décommentez la ligne ci-dessous)
        // recyclerViewProduits.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerViewProduits.setAdapter(produitAdapter);
    }

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Non utilisé
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filtrer les produits à chaque changement de texte
                filterProduits(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Non utilisé
            }
        });
    }

    private void filterProduits(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            // Si la recherche est vide, afficher tous les produits
            filteredList.addAll(produitList);
        } else {
            // Filtrer par nom (insensible à la casse)
            String queryLower = query.toLowerCase();
            for (Produit produit : produitList) {
                if (produit.getNom().toLowerCase().contains(queryLower)) {
                    filteredList.add(produit);
                }
            }
        }

        // Mettre à jour l'adapter
        produitAdapter.notifyDataSetChanged();

        // Afficher un message si aucun résultat
        if (filteredList.isEmpty()) {
            txtNoProduits.setVisibility(View.VISIBLE);
            txtNoProduits.setText("Aucun produit trouvé");
            recyclerViewProduits.setVisibility(View.GONE);
        } else {
            txtNoProduits.setVisibility(View.GONE);
            recyclerViewProduits.setVisibility(View.VISIBLE);
        }
    }

    private void loadProduits() {
        // Afficher le ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        txtNoProduits.setVisibility(View.GONE);
        recyclerViewProduits.setVisibility(View.GONE);

        // Récupérer les produits depuis Firestore
        db.collection("produits")
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        produitList.clear();

                        // Parcourir tous les documents
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Produit produit = document.toObject(Produit.class);
                            produit.setId(document.getId());
                            produitList.add(produit);
                        }

                        // Afficher tous les produits
                        filteredList.clear();
                        filteredList.addAll(produitList);
                        produitAdapter.notifyDataSetChanged();

                        // Vérifier si la liste est vide
                        if (produitList.isEmpty()) {
                            txtNoProduits.setVisibility(View.VISIBLE);
                            txtNoProduits.setText("Aucun produit disponible");
                            recyclerViewProduits.setVisibility(View.GONE);
                        } else {
                            txtNoProduits.setVisibility(View.GONE);
                            recyclerViewProduits.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Erreur lors du chargement
                        txtNoProduits.setVisibility(View.VISIBLE);
                        txtNoProduits.setText("Erreur de chargement");
                        Toast.makeText(MainActivity.this,
                                "Erreur: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    txtNoProduits.setVisibility(View.VISIBLE);
                    txtNoProduits.setText("Erreur de connexion");
                    Toast.makeText(MainActivity.this,
                            "Erreur: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onProduitClick(Produit produit) {
        // Lancer ProductDetailsActivity avec les données du produit
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("produit_id", produit.getId());
        intent.putExtra("produit_nom", produit.getNom());
        intent.putExtra("produit_prix", produit.getPrix());
        intent.putExtra("produit_description", produit.getDescription());
        intent.putExtra("produit_image", produit.getImageUrl());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Créer le menu dans la toolbar
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_panier) {
            // Ouvrir le panier
            startActivity(new Intent(this, PanierActivity.class));
            return true;
        } else if (id == R.id.menu_commandes) {
            // Ouvrir l'historique des commandes
            startActivity(new Intent(this, CommandesActivity.class));
            return true;
        } else if (id == R.id.menu_deconnexion) {
            // Déconnecter l'utilisateur
            deconnecterUtilisateur();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deconnecterUtilisateur() {
        // Déconnecter Firebase
        mAuth.signOut();

        // Message de confirmation
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show();

        // Rediriger vers LoginActivity
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recharger les produits quand on revient à cette activité
        // (optionnel, peut être commenté pour éviter les rechargements inutiles)
        // loadProduits();
    }
}