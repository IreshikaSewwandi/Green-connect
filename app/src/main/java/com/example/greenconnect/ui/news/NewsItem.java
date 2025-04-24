package com.example.greenconnect.ui.news;

import java.util.Date;

public class NewsItem {
    private String title;
    private String content;

    public String getAuthor() {
        return author;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    private String imageUrl;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String link;
    private String author;
    private Date timestamp;

    // Empty constructor (required for Firestore)
    public NewsItem() {}

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
// Add getters and setters for all fields
    // (Right-click → Generate → Getters and Setters in Android Studio)
}
