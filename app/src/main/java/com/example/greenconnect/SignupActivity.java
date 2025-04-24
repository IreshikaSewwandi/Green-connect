package com.example.greenconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity { // Changed to proper class naming convention

    private static final String TAG = "SignupActivity"; // For logging

    private EditText firstName, lastName, email, password, confirmPassword;
    private TextView gologin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind UI elements
        initializeViews();

        gologin.setOnClickListener(v -> {
            Log.d(TAG, "Signup button clicked"); // Debug log
            handleSignup();
        });
    }

    private void initializeViews() {
        firstName = findViewById(R.id.editTextText3);
        lastName = findViewById(R.id.editTextText4);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        confirmPassword = findViewById(R.id.editTextTextPassword3);
        gologin = findViewById(R.id.sign_upp);
    }

    private void handleSignup() {
        if (!validateInputs()) {
            return;
        }

        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user);
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignupActivity.this, "Authentication failed: " +
                                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputs() {
        String emailText = email.getText().toString().trim();
        String pw = password.getText().toString();
        String confirmPw = confirmPassword.getText().toString();

        if (emailText.isEmpty()) {
            email.setError("Email is required");
            return false;
        }

        if (pw.isEmpty()) {
            password.setError("Password is required");
            return false;
        }

        if (!pw.equals(confirmPw)) {
            confirmPassword.setError("Passwords don't match!");
            return false;
        }

        if (pw.length() < 6) {
            password.setError("Password needs 6+ characters");
            return false;
        }

        return true;
    }

    private void saveUserToFirestore(FirebaseUser user) {
        Map<String, Object> userData = new HashMap<>();

        // Basic Info
        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("email", user.getEmail());
        basicInfo.put("name", firstName.getText().toString() + " " + lastName.getText().toString());
        userData.put("basicInfo", basicInfo);

        // Empty Farm Info
        userData.put("farmInfo", new HashMap<>());

        // Metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("createdAt", Timestamp.now());
        metadata.put("lastLogin", Timestamp.now());
        userData.put("metadata", metadata);

        db.collection("users").document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User data saved to Firestore");
                    startActivity(new Intent(SignupActivity.this, successful.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error saving user data", e);
                    Toast.makeText(SignupActivity.this,
                            "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}