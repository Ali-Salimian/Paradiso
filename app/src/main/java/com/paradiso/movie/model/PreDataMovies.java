package com.paradiso.movie.model;

public class PreDataMovies {
    String id;
    String poster ;
    String title;
    String director;
    String year;




    public PreDataMovies(String id, String poster, String title, String year) {
        this.id = id;
        this.poster = poster;
        this.title = title;
        this.director = director;
        this.year = year;
    }

    public PreDataMovies(String poster, String title, String year) {
        this.poster = poster;
        this.title = title;
        this.director = director;
        this.year = year;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
