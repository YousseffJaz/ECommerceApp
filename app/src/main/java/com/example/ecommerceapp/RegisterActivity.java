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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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
        btnRegister.setOnClickListener(v -> registerUser());

        txtLogin.setOnClickListener(v ->  {
            finish();
        });
    }

    private void registerUser() {
        String nom = editTextNom.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validation des champs
        if (TextUtils.isEmpty(nom)) {
            editTextNom.setError("Nom requires");
            editTextNom.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email requires");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Email invalid");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Mot de passe requires");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Mot de passe  6 carapaces");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        assert mAuth.getCurrentUser() != null;
                        String userId = mAuth.getCurrentUser().getUid();

                        Map<String, Object> user = new HashMap<>();
                        user.put("id", userId);
                        user.put("nom", nom);
                        user.put("email", email);

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

                        String errorMessage = getString(task);

                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @NonNull
    private static String getString(Task<AuthResult> task) {
        String errorMessage = "Erreur d'inscription";
        if (task.getException() != null) {
            errorMessage = task.getException().getMessage();

            // Messages d'erreur personnalisés
            assert errorMessage != null;
            if (errorMessage.contains("email address is already in use")) {
                errorMessage = "Cet email est déjà utilisé";
            } else if (errorMessage.contains("network error")) {
                errorMessage = "Erreur de connexion. Vérifiez votre internet";
            }
        }
        return errorMessage;
    }

}