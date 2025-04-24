package com.example.greenconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLocation;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get the confirm button
        confirmButton = findViewById(R.id.confirm_location);
        confirmButton.setOnClickListener(v -> {
            if (selectedLocation != null) {
                // Return the selected location back to the edit_profile activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitude", selectedLocation.latitude);
                resultIntent.putExtra("longitude", selectedLocation.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please select a location first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Set default location (you can use a default location or get the current location)
        LatLng defaultLocation = new LatLng(7.8731, 80.7718); // Center of Sri Lanka
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 7));

        // Get initial location from intent if available
        double initialLat = getIntent().getDoubleExtra("latitude", 0.0);
        double initialLng = getIntent().getDoubleExtra("longitude", 0.0);

        if (initialLat != 0.0 || initialLng != 0.0) {
            LatLng initialLocation = new LatLng(initialLat, initialLng);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(initialLocation).title("Farm Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15));
            selectedLocation = initialLocation;
        }

        // Set click listener on map
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            selectedLocation = latLng;
        });
    }
}
