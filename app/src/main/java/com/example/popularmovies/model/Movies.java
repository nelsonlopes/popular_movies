package com.example.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Movies {
    @SerializedName("results")
    private ArrayList<Movie> movies = new ArrayList<>();

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public void setMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }
}
