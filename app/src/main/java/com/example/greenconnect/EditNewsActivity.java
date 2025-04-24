package com.example.greenconnect;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenconnect.ui.news.NewsItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EditNewsActivity extends AppCompatActivity {
    private static final String TAG = "EditNewsActivity";
    private EditText titleEditText, contentEditText, imageUrlEditText, linkEditText;
    private FirebaseFirestore db;
    private String newsId;
    private boolean isEditMode = false;
    private final Map<String, String> userNamesCache = new HashMap<>(); // Cache for user names

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_news);
        Log.d(TAG, "Activity created");

        db = FirebaseFirestore.getInstance();
        initializeViews();
        checkEditMode();
    }

    private void initializeViews() {
        titleEditText = findViewById(R.id.et_title);
        contentEditText = findViewById(R.id.et_content);
        imageUrlEditText = findViewById(R.id.et_image_url);
        linkEditText = findViewById(R.id.et_link);

        Button submitButton = findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(v -> handleNewsAction());
    }

    private void checkEditMode() {
        newsId = getIntent().getStringExtra("newsId");
        isEditMode = newsId != null && !newsId.isEmpty();
        Log.d(TAG, "Edit mode: " + isEditMode + ", News ID: " + newsId);

        Button submitButton = findViewById(R.id.btn_submit);
        submitButton.setText(isEditMode ? "Update" : "Publish");
        setTitle(isEditMode ? "Edit News" : "Create News");

        if (isEditMode) {
            loadNewsData();
        }
    }

    private void loadNewsData() {
        if (newsId == null || newsId.isEmpty()) {
            showErrorAndFinish("Invalid news ID");
            return;
        }

        db.collection("news").document(newsId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        NewsItem newsItem = documentSnapshot.toObject(NewsItem.class);
                        if (newsItem != null) {
                            newsItem.setId(documentSnapshot.getId());
                            populateFields(newsItem);
                            fetchAuthorName(newsItem.getAuthorId()); // Fetch author name if needed
                        } else {
                            showErrorAndFinish("Invalid news data");
                        }
                    } else {
                        showErrorAndFinish("News not found");
                    }
                })
                .addOnFailureListener(e -> showErrorAndFinish("Error loading news: " + e.getMessage()));
    }

    private void populateFields(NewsItem newsItem) {
        try {
            titleEditText.setText(newsItem.getTitle());
            contentEditText.setText(newsItem.getContent());
            imageUrlEditText.setText(newsItem.getImageUrl() != null ? newsItem.getImageUrl() : "");
            linkEditText.setText(newsItem.getLink() != null ? newsItem.getLink() : "");
        } catch (Exception e) {
            Log.e(TAG, "Error populating fields", e);
        }
    }

    private void fetchAuthorName(String authorId) {
        if (userNamesCache.containsKey(authorId)) {
            return; // Already cached
        }

        db.collection("users").document(authorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("basicInfo.name");
                        if (name != null) {
                            userNamesCache.put(authorId, name);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error fetching user name", e));
    }

    private void handleNewsAction() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String imageUrl = imageUrlEditText.getText().toString().trim();
        String link = linkEditText.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Title and content are required", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            showErrorAndFinish("User not authenticated");
            return;
        }

        NewsItem newsItem = new NewsItem();
        newsItem.setTitle(title);
        newsItem.setContent(content);
        newsItem.setImageUrl(imageUrl);
        newsItem.setLink(link);
        newsItem.setAuthor(currentUser.getDisplayName());
        newsItem.setAuthorId(currentUser.getUid());
        newsItem.setTimestamp(new Date());

        if (isEditMode) {
            updateNews(newsItem);
        } else {
            createNews(newsItem);
        }
    }

    private void createNews(NewsItem newsItem) {
        db.collection("news").add(newsItem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "News published successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error publishing news: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Publish error", e);
                });
    }

    private void updateNews(NewsItem newsItem) {
        newsItem.setId(newsId);
        db.collection("news").document(newsId)
                .set(newsItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "News updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Update error", e);
                });
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, message);
        finish();
    }
}