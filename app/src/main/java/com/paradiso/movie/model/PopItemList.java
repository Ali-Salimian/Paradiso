package com.paradiso.movie.model;

public class PopItemList  {
    String title;
    String imageUrl;

    public PopItemList(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public PopItemList() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
