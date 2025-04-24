package com.example.greenconnect.ui.news;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greenconnect.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NewsAdapter extends FirestoreRecyclerAdapter<NewsItem, NewsAdapter.NewsViewHolder> {

    public NewsAdapter(@NonNull FirestoreRecyclerOptions<NewsItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NewsViewHolder holder, int position, @NonNull NewsItem news) {
        holder.title.setText(news.getTitle());
        holder.content.setText(news.getContent());
        holder.author.setText("By " + news.getAuthor());

        // Load image with Glide
        if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(news.getImageUrl())
                    .into(holder.image);
        }

        holder.itemView.setOnClickListener(v -> {
            if (news.getLink() != null && !news.getLink().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getLink()));
                v.getContext().startActivity(browserIntent);
            }
        });
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, author;
        ImageView image;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_title);
            content = itemView.findViewById(R.id.tv_content);
            author = itemView.findViewById(R.id.tv_author);
            image = itemView.findViewById(R.id.iv_image);
        }
    }
}