package com.example.greenconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth auth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.editTextText2);
        passwordEditText = findViewById(R.id.editTextTextPassword2);
        loginButton = findViewById(R.id.button2);

        setupProgressDialog();
        setupLoginButton();
    }



    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Authenticating");
        progressDialog.setMessage("Checking credentials...");
        progressDialog.setCancelable(false);
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (validateInputs(email, password)) {
                attemptLogin(email, password);
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            return false;
        }
        return true;
    }

    private void attemptLogin(String email, String password) {
        progressDialog.show();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        // Sign in success
                        Log.d(TAG, "signInWithEmail:success");
                        navigateToMainActivity();
                    } else {
                        // If sign in fails, display a message to the user
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        showError("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}