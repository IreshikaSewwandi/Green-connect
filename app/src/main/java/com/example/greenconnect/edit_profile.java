package com.example.greenconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class edit_profile extends AppCompatActivity {

    ImageView logout1;
    TextView cancel_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        logout1 = findViewById(R.id.imageView36);
        cancel_edit = findViewById(R.id.cancle);
        cancel_edit.setOnClickListener(v -> {
            startActivity(new Intent(edit_profile.this, profile.class));
        });

        logout1.setOnClickListener(v -> {
            startActivity(new Intent(edit_profile.this, profile.class));
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}