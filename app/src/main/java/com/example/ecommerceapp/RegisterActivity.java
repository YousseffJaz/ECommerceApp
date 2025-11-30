package com.example.ecommerceapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextNom, editTextEmail, editTextPassword;
    private Button btnRegister;
    private TextView txtLogin;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialiser Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialiser les vues
        initViews();

        // Configurer les listeners
        setupListeners();
    }

    private void initViews() {
        editTextNom = findViewById(R.id.editTextNom);
        editTextEmail = findViewById(R.id.editTextEmailRegister);
        editTextPassword = findViewById(R.id.editTextPasswordRegister);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);
        progressBar = findViewById(R.id.progressBarRegister);
    }

    private void setupListeners() {
        // Bouton d'inscription
        btnRegister.setOnClickListener(v -> registerUser());

        // Lien vers la page de connexion
        txtLogin.setOnClickListener(v -> {
            finish(); // Retour à LoginActivity
        });
    }

    private void registerUser() {
        String nom = editTextNom.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validation des champs
        if (TextUtils.isEmpty(nom)) {
            editTextNom.setError("Nom requis");
            editTextNom.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email requis");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Email invalide");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Mot de passe requis");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Mot de passe doit contenir au moins 6 caractères");
            editTextPassword.requestFocus();
            return;
        }

        // Afficher le ProgressBar
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Créer le compte avec Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Récupérer l'ID de l'utilisateur
                        String userId = mAuth.getCurrentUser().getUid();

                        // Créer un document utilisateur dans Firestore
                        Map<String, Object> user = new HashMap<>();
                        user.put("id", userId);
                        user.put("nom", nom);
                        user.put("email", email);

                        // Ajouter l'utilisateur à Firestore
                        db.collection("utilisateurs").document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    progressBar.setVisibility(View.GONE);
                                    btnRegister.setEnabled(true);

                                    Toast.makeText(RegisterActivity.this,
                                            "Inscription réussie ! Bienvenue " + nom,
                                            Toast.LENGTH_SHORT).show();

                                    // Rediriger vers MainActivity
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    btnRegister.setEnabled(true);
                                    Toast.makeText(RegisterActivity.this,
                                            "Erreur lors de la création du profil: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);

                        String errorMessage = "Erreur d'inscription";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();

                            // Messages d'erreur personnalisés
                            if (errorMessage.contains("email address is already in use")) {
                                errorMessage = "Cet email est déjà utilisé";
                            } else if (errorMessage.contains("network error")) {
                                errorMessage = "Erreur de connexion. Vérifiez votre internet";
                            }
                        }

                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}