package com.example.greenconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenconnect.ui.news.NewsItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Objects;

public class CreateNewsActivity extends AppCompatActivity {
    private EditText titleEditText, contentEditText, imageUrlEditText, linkEditText;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_news);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, login.class));
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        // Initialize views
        titleEditText = findViewById(R.id.et_title);
        contentEditText = findViewById(R.id.et_content);
        imageUrlEditText = findViewById(R.id.et_image_url);
        linkEditText = findViewById(R.id.et_link);

        Button submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(v -> submitNews());
    }

    private void submitNews() {
        // Get current user as author
        String author = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        String authorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        NewsItem news = new NewsItem();
        news.setTitle(titleEditText.getText().toString());
        news.setContent(contentEditText.getText().toString());
        news.setImageUrl(imageUrlEditText.getText().toString());
        news.setLink(linkEditText.getText().toString());
        news.setAuthor(author);
        news.setAuthorId(authorId);
        news.setTimestamp(new Date());

        // Create a new document with auto-generated ID
        DocumentReference newDoc = db.collection("news").document();
        newDoc.set(news)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "News published!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}