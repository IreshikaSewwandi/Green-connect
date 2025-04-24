package com.example.greenconnect.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenconnect.CreateNewsActivity;
import com.example.greenconnect.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NewsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.rv_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setupRecyclerView();
        setupAddNewsButton(view);

        return view;
    }

    private void setupRecyclerView() {
        Query query = db.collection("news")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<NewsItem> options = new FirestoreRecyclerOptions.Builder<NewsItem>()
                .setQuery(query, NewsItem.class)
                .build();

        adapter = new NewsAdapter(options);
        recyclerView.setAdapter(adapter);
    }

    private void setupAddNewsButton(View view) {
        FloatingActionButton fab = view.findViewById(R.id.fab_add_news);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CreateNewsActivity.class));
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}