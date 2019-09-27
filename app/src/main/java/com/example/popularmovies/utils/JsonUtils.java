package com.example.popularmovies.utils;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.Review;
import com.example.popularmovies.model.Trailer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonUtils {

    // Commom
    private static final String KEY_RESULTS = "results";

    private static Gson gsonMovies;
    private static Gson gsonTrailers;
    private static Gson gsonReviews;

    // Parse Movies
    public static List<Movie> parseMovieJson(String json) {
        List<Movie> movies = null;

        GsonBuilder gsonBuilderMovies = new GsonBuilder();
        gsonMovies = gsonBuilderMovies.create();

        try {
            JSONObject list = new JSONObject(json);
            JSONArray results = list.getJSONArray(KEY_RESULTS);
            movies = new ArrayList<>();
            movies = Arrays.asList(gsonMovies.fromJson(results.toString(), Movie[].class));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

    // Parse Trailers
    public static List<Trailer> parseTrailerJson(String json) {
        List<Trailer> trailers = null;

        GsonBuilder gsonBuilderTrailers = new GsonBuilder();
        gsonTrailers = gsonBuilderTrailers.create();

        try {
            JSONObject list = new JSONObject(json);
            JSONArray results = list.getJSONArray(KEY_RESULTS);
            trailers = new ArrayList<>();
            trailers = Arrays.asList(gsonTrailers.fromJson(results.toString(), Trailer[].class));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trailers;
    }

    // Parse Reviews
    public static List<Review> parseReviewJson(String json) {
        List<Review> reviews = null;

        GsonBuilder gsonBuilderReviews = new GsonBuilder();
        gsonReviews = gsonBuilderReviews.create();

        try {
            JSONObject list = new JSONObject(json);
            JSONArray results = list.getJSONArray(KEY_RESULTS);
            reviews = new ArrayList<>();
            reviews = Arrays.asList(gsonReviews.fromJson(results.toString(), Review[].class));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviews;
    }
}
