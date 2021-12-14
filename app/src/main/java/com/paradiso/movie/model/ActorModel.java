package com.paradiso.movie.model;

import java.io.Serializable;

public class ActorModel implements Serializable {
    String name;
    String id;
    String imageUrl;

    public ActorModel(String name, String id, String imageUrl) {
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public ActorModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
