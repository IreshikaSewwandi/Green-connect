package com.example.greenconnect;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.greenconnect.ui.news.NewsItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class profile extends AppCompatActivity {

    ImageView logout;
    TextView editpro;
    LinearLayout newsContainer;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        logout = findViewById(R.id.imageView17);
        editpro = findViewById(R.id.edit_profil);
        newsContainer = findViewById(R.id.newsContainer);

        // Set click listeners
        editpro.setOnClickListener(v -> {
            startActivity(new Intent(profile.this, edit_profile.class));
        });

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(profile.this, first.class));
            finish();
        });

        // Load user's news articles
        loadUserNews();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadUserNews() {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        db.collection("news")
                .whereEqualTo("authorId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        TextView emptyView = new TextView(this);
                        emptyView.setText("You haven't created any news articles yet");
                        emptyView.setTextSize(16);
                        emptyView.setPadding(16, 16, 16, 16);
                        newsContainer.addView(emptyView);
                    } else {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            NewsItem news = document.toObject(NewsItem.class);
                            addNewsItemToView(news, document.getId());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load news: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addNewsItemToView(NewsItem news, String documentId) {
        View newsItemView = LayoutInflater.from(this).inflate(R.layout.item_user_news, null);

        TextView title = newsItemView.findViewById(R.id.newsTitle);
        TextView date = newsItemView.findViewById(R.id.newsDate);
        View editBtn = newsItemView.findViewById(R.id.editButton);
        View deleteBtn = newsItemView.findViewById(R.id.deleteButton);

        // Set news data
        title.setText(news.getTitle());
        date.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                .format(news.getTimestamp()));

        // Edit button click
        editBtn.setOnClickListener(v -> {
            Intent intent; // Or create EditNewsActivity
            intent = new Intent(this, EditNewsActivity.class);
            intent.putExtra("newsId", documentId);
            startActivity(intent);
        });

        // Delete button click
        deleteBtn.setOnClickListener(v -> showDeleteConfirmation(documentId));

        newsContainer.addView(newsItemView);
    }

    private void showDeleteConfirmation(String newsId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete News")
                .setMessage("Are you sure you want to delete this news article?")
                .setPositiveButton("Delete", (dialog, which) -> deleteNews(newsId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteNews(String newsId) {
        db.collection("news").document(newsId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "News deleted", Toast.LENGTH_SHORT).show();
                    refreshNewsList();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error deleting news: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void refreshNewsList() {
        newsContainer.removeAllViews();
        loadUserNews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNewsList(); // Refresh when returning from edit
    }
}