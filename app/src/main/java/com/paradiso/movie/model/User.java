package com.paradiso.movie.model;

public class User {

    String name;
    String email;
    String password;
    String id;
    String imageUrl;
    String userName;

    public User(String name, String id, String imageUrl) {
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;


    }


    public User(String name, String id, String imageUrl,String userName) {
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
        this.userName = userName;

    }

    public User(String name, String email, String password, String id,String userName) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.id = id;
        this.userName = userName;
    }

    public User(String name, String email, String password, String id, String imageUrl,String userName) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.id = id;
        this.imageUrl = imageUrl;
        this.userName = userName;
    }

    public User() {
    }

    public String getImageUrl() {
        if(imageUrl ==null){
            return null;
        }
        return imageUrl;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
