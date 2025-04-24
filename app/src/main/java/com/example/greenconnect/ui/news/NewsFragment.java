package com.example.greenconnect.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenconnect.CreateNewsActivity;
import com.example.greenconnect.R;
import com.example.greenconnect.login;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NewsFragment extends Fragment {

    private static final String TAG = "NewsFragment";
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private View emptyView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.rv_news);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.empty_view);

        // Initialize with empty state
        showLoading(true);
        showEmptyView(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupRecyclerView();
        setupAddNewsButton(view);

        return view;
    }

    private void setupRecyclerView() {
        Query query = db.collection("news")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<NewsItem> options = new FirestoreRecyclerOptions.Builder<NewsItem>()
                .setQuery(query, snapshot -> {
                    try {
                        NewsItem news = snapshot.toObject(NewsItem.class);
                        if (news != null) {
                            news.setId(snapshot.getId());
                        }
                        return news;
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing document", e);
                        return null;
                    }
                })
                .build();

        adapter = new NewsAdapter(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();
                showLoading(false);
                boolean isEmpty = getItemCount() == 0;
                showEmptyView(isEmpty);
            }

            @Override
            public void onError(@NonNull Exception e) {
                showLoading(false);
                Toast.makeText(getContext(), "Error loading news", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Adapter error", e);
            }
        };

        recyclerView.setAdapter(adapter);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyView(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void setupAddNewsButton(View view) {
        FloatingActionButton fab = view.findViewById(R.id.fab_add_news);
        fab.setOnClickListener(v -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                startActivity(new Intent(getActivity(), CreateNewsActivity.class));
            } else {
                Toast.makeText(getContext(), "Please login to create news", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), login.class));
                requireActivity().finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}