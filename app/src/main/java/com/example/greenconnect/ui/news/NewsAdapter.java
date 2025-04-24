package com.example.greenconnect.ui.news;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greenconnect.R;
import com.example.greenconnect.ui.news.NewsItem;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public abstract class NewsAdapter extends FirestoreRecyclerAdapter<NewsItem, NewsAdapter.NewsViewHolder> {
    private static final String TAG = "NewsAdapter";

    public NewsAdapter(@NonNull FirestoreRecyclerOptions<NewsItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NewsViewHolder holder, int position, @NonNull NewsItem news) {
        try {
            if (news == null) {
                Log.w(TAG, "Null news item at position: " + position);
                return;
            }

            // Set news data with null checks
            holder.title.setText(news.getTitle() != null ? news.getTitle() : "Untitled");
            holder.content.setText(news.getContent() != null ? news.getContent() : "");

            // Handle author display
            if (news.getAuthorId() != null) {
                holder.author.setText(news.getAuthor() != null ? "By " + news.getAuthor() : "By Unknown");
            } else {
                holder.author.setText("By Anonymous");
            }

            // Handle image
            if (news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
                holder.image.setVisibility(View.VISIBLE);
                Glide.with(holder.itemView.getContext())
                        .load(news.getImageUrl())
                        .placeholder(R.drawable.background2)
                        .error(R.drawable.cycle)
                        .into(holder.image);
            } else {
                holder.image.setVisibility(View.GONE);
            }

            // Handle click
            holder.itemView.setOnClickListener(v -> {
                if (news.getLink() != null && !news.getLink().isEmpty()) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getLink()));
                        v.getContext().startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(v.getContext(), "Couldn't open link", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder", e);
        }
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    public abstract void onError(@NonNull Exception e);

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