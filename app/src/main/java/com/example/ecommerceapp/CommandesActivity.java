package com.example.ecommerceapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.adapters.CommandeAdapter;
import com.example.ecommerceapp.models.Commande;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CommandesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCommandes;
    private CommandeAdapter commandeAdapter;
    private List<Commande> commandeList;
    private ProgressBar progressBar;
    private TextView txtNoCommandes;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commandes);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadCommandes();
    }

    private void initViews() {
        recyclerViewCommandes = findViewById(R.id.recyclerViewCommandes);
        progressBar = findViewById(R.id.progressBarMain);
        txtNoCommandes = findViewById(R.id.menu_commandes);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarPanier);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        commandeList = new ArrayList<>();
        commandeAdapter = new CommandeAdapter(this, commandeList);
        recyclerViewCommandes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCommandes.setAdapter(commandeAdapter);
    }

    private void loadCommandes() {
        progressBar.setVisibility(View.VISIBLE);
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("commandes")
                .whereEqualTo("idUtilisateur", userId)
                .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        commandeList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Commande commande = document.toObject(Commande.class);
                            commande.setId(document.getId());
                            commandeList.add(commande);
                        }

                        commandeAdapter.notifyDataSetChanged();

                        if (commandeList.isEmpty()) {
                            txtNoCommandes.setVisibility(View.VISIBLE);
                            recyclerViewCommandes.setVisibility(View.GONE);
                        } else {
                            txtNoCommandes.setVisibility(View.GONE);
                            recyclerViewCommandes.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}