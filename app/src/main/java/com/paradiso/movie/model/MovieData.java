package com.paradiso.movie.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MovieData implements Serializable {
    String id;

    String name;
    String director;
    String genre;
    String year;
    String posterUrl;
    String teaserUrl;
    String imdbRate;
    String metaRate;
    String description;
    String myRate;
    String savedGroup;
    String savedTitleGroup;
    long date;




    String PublisherId;

    ArrayList<ActorModel> actors;

    String postId;
    ArrayList<String> imageList ;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getSavedTitleGroup() {
        return savedTitleGroup;
    }

    public void setSavedTitleGroup(String savedTitleGroup) {
        this.savedTitleGroup = savedTitleGroup;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public String getSavedGroup() {
        return savedGroup;
    }

    public void setSavedGroup(String savedGroup) {
        this.savedGroup = savedGroup;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public ArrayList<ActorModel> getActors() {
        return actors;
    }

    public void setActors(ArrayList<ActorModel> actors) {
        this.actors = actors;
    }


    public String getMyRate() {
        return myRate;
    }

    public void setMyRate(String myRate) {
        this.myRate = myRate;
    }

    public String getPublisherId() {
        return PublisherId;
    }

    public void setPublisherId(String PublisherId) {
        this.PublisherId = PublisherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getTeaserUrl() {
        return teaserUrl;
    }

    public void setTeaserUrl(String teaserUrl) {
        this.teaserUrl = teaserUrl;
    }

    public String getImdbRate() {
        return imdbRate;
    }

    public void setImdbRate(String imdbRate) {
        this.imdbRate = imdbRate;
    }

    public String getMetaRate() {
        return metaRate;
    }

    public void setMetaRate(String metaRate) {
        this.metaRate = metaRate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public MovieData(String name,String id, String director, String genre, String year, String posterUrl, String teaserUrl, String imdbRate, String metaRate, String description) {
//        this.name = name;
//        this.id = id;
//        this.director = director;
//        this.genre = genre;
//        this.year = year;
//        this.posterUrl = posterUrl;
//        this.teaserUrl = teaserUrl;
//        this.imdbRate = imdbRate;
//        this.metaRate = metaRate;
//        this.description = description;
//    }

    public MovieData() {
    }

    public MovieData(String id, String name, String director, String genre, String year, String posterUrl, String teaserUrl, String imdbRate, String metaRate, String description, String PublisherId,ArrayList<ActorModel> actors) {
        this.id = id;
        this.name = name;
        this.director = director;
        this.genre = genre;
        this.year = year;
        this.posterUrl = posterUrl;
        this.teaserUrl = teaserUrl;
        this.imdbRate = imdbRate;
        this.metaRate = metaRate;
        this.description = description;


        this.PublisherId = PublisherId;

        this.actors = actors;
    }

    public MovieData(String id, String name, String director, String genre, String year, String posterUrl, String teaserUrl, String imdbRate, String metaRate, String description) {
        this.id = id;
        this.name = name;
        this.director = director;
        this.genre = genre;
        this.year = year;
        this.posterUrl = posterUrl;
        this.teaserUrl = teaserUrl;
        this.imdbRate = imdbRate;
        this.metaRate = metaRate;
        this.description = description;

    }

    public MovieData(String id, String name,  String year, String posterUrl, String imdbRate) {
        this.id = id;
        this.name = name;


        this.year = year;
        this.posterUrl = posterUrl;

        this.imdbRate = imdbRate;


    }
}
