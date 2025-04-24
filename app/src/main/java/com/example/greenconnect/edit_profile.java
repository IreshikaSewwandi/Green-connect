package com.example.greenconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class edit_profile extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    TextView saveButton;
    TextView cancelEdit;
    EditText farmName, phoneNumber, email, farmSize, soilType;
    Button setLocationButton;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;

    // Placeholder coordinates - replace with actual values from map selection
    private double latitude = 0.0;
    private double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get current user ID
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // Not logged in, redirect to login
            startActivity(new Intent(edit_profile.this, login.class));
            finish();
            return;
        }
        userId = currentUser.getUid();

        // Initialize UI elements
        saveButton = findViewById(R.id.save); // Rename this to saveButton in layout for clarity
        cancelEdit = findViewById(R.id.cancle);
        farmName = findViewById(R.id.editTextText7);
        phoneNumber = findViewById(R.id.editTextPhone);
        email = findViewById(R.id.editTextTextEmailAddress2);
        farmSize = findViewById(R.id.editTextText5);
        soilType = findViewById(R.id.editTextText6);
        setLocationButton = findViewById(R.id.button);

        // Load existing data
        loadUserProfile();

        // Set up click listeners
        setLocationButton.setOnClickListener(v -> {
            Log.d(TAG, "Open Google Maps");
            // TODO: implement Google Maps integration
            // When implemented, update latitude and longitude variables
        });

        cancelEdit.setOnClickListener(v -> {
            startActivity(new Intent(edit_profile.this, profile.class));
            finish();
        });

        saveButton.setOnClickListener(v -> {
            updateProfile();
        });

        // Inside your onCreate method in edit_profile.java
        setLocationButton.setOnClickListener(v -> {
            Log.d(TAG, "Open Google Maps");
            Intent mapIntent = new Intent(edit_profile.this, MapActivity.class);
            // Pass current coordinates if available
            mapIntent.putExtra("latitude", latitude);
            mapIntent.putExtra("longitude", longitude);
            startActivityForResult(mapIntent, 1); // Use 1 as request code
        });
    }

    private void loadUserProfile() {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the nested maps
                        Map<String, Object> basicInfo = (Map<String, Object>) documentSnapshot.get("basicInfo");
                        Map<String, Object> farmInfo = (Map<String, Object>) documentSnapshot.get("farmInfo");
                        Map<String, Object> contactInfo = (Map<String, Object>) documentSnapshot.get("contactInfo");
                        Map<String, Object> location = (Map<String, Object>) documentSnapshot.get("location");

                        // Set email field
                        if (basicInfo != null && basicInfo.get("email") != null) {
                            email.setText((String) basicInfo.get("email"));
                        } else if (auth.getCurrentUser().getEmail() != null) {
                            // Fallback to auth email
                            email.setText(auth.getCurrentUser().getEmail());
                        }

                        // Set farm info fields
                        if (farmInfo != null) {
                            if (farmInfo.get("farmName") != null)
                                farmName.setText((String) farmInfo.get("farmName"));
                            if (farmInfo.get("farmSize") != null)
                                farmSize.setText((String) farmInfo.get("farmSize"));
                            if (farmInfo.get("soilType") != null)
                                soilType.setText((String) farmInfo.get("soilType"));
                        }

                        // Set contact info
                        if (contactInfo != null && contactInfo.get("phone") != null) {
                            phoneNumber.setText((String) contactInfo.get("phone"));
                        }

                        // Get location data
                        if (location != null) {
                            if (location.get("latitude") != null)
                                latitude = ((Number) location.get("latitude")).doubleValue();
                            if (location.get("longitude") != null)
                                longitude = ((Number) location.get("longitude")).doubleValue();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user profile", e);
                    Toast.makeText(edit_profile.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProfile() {
        // Create nested maps according to your data structure
        Map<String, Object> farmInfoMap = new HashMap<>();
        farmInfoMap.put("farmName", farmName.getText().toString().trim());
        farmInfoMap.put("farmSize", farmSize.getText().toString().trim());
        farmInfoMap.put("soilType", soilType.getText().toString().trim());

        Map<String, Object> contactInfoMap = new HashMap<>();
        contactInfoMap.put("phone", phoneNumber.getText().toString().trim());

        Map<String, Object> locationMap = new HashMap<>();
        locationMap.put("latitude", latitude);
        locationMap.put("longitude", longitude);

        // For updating nested fields like basicInfo.email, we need a special approach
        Map<String, Object> basicInfoMap = new HashMap<>();
        basicInfoMap.put("email", email.getText().toString().trim());

        // Create the main user data map
        Map<String, Object> userData = new HashMap<>();
        userData.put("farmInfo", farmInfoMap);
        userData.put("contactInfo", contactInfoMap);
        userData.put("location", locationMap);
        userData.put("basicInfo", basicInfoMap);

        // Update Firestore (merge to prevent overwriting fields we're not updating)
        db.collection("users").document(userId)
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Profile updated successfully");
                    Toast.makeText(edit_profile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(edit_profile.this, profile.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating profile: " + e.getMessage(), e);
                    Toast.makeText(edit_profile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) { // The same request code used in startActivityForResult
            if (resultCode == RESULT_OK) {
                // Get latitude and longitude from the map activity
                latitude = data.getDoubleExtra("latitude", 0.0);
                longitude = data.getDoubleExtra("longitude", 0.0);

                // Show confirmation to user
                Toast.makeText(this, "Location set successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

}