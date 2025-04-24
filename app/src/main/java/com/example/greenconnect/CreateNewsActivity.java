package com.example.greenconnect;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenconnect.ui.news.NewsItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class CreateNewsActivity extends AppCompatActivity {

    private EditText titleEditText, contentEditText, imageUrlEditText, linkEditText;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_news);

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
        String author = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        NewsItem news = new NewsItem();
        news.setTitle(titleEditText.getText().toString());
        news.setContent(contentEditText.getText().toString());
        news.setImageUrl(imageUrlEditText.getText().toString());
        news.setLink(linkEditText.getText().toString());
        news.setAuthor(author);
        news.setTimestamp(new Date());

        db.collection("news")
                .add(news)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "News published!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

