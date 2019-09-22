package com.example.popularmovies.utils;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    // Commom
    private static final String KEY_RESULTS = "results";
    private static final String KEY_ID = "id";

    // Related to Movies
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";

    // Related to Trailers
    private static final String KEY_KEY = "key";
    private static final String KEY_NAME = "name";
    private static final String KEY_SITE = "site";
    private static final String KEY_TYPE = "type";

    // Parse Movies
    public static List<Movie> parseMovieJson(String json) {
        List<Movie> movies = null;

        try {
            JSONObject list = new JSONObject(json);

            JSONArray results = list.getJSONArray(KEY_RESULTS);

            movies = new ArrayList<>();

            for (int i = 0; i < results.length(); i++) {
                Movie movie = new Movie();

                JSONObject result = results.getJSONObject(i);

                movie.setId(result.getInt(KEY_ID));
                movie.setOriginalTitle(result.getString(KEY_ORIGINAL_TITLE));
                movie.setOverview(result.getString(KEY_OVERVIEW));
                movie.setVoteAverage(result.getDouble(KEY_VOTE_AVERAGE));
                movie.setReleaseDate(result.getString(KEY_RELEASE_DATE));
                movie.setPosterPath(result.optString(KEY_POSTER_PATH));
                movie.setBackdropPath(result.optString(KEY_BACKDROP_PATH));

                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

    // Parse Trailers
    public static List<Trailer> parseTrailerJson(String json) {
        List<Trailer> trailers = null;

        try {
            JSONObject list = new JSONObject(json);

            JSONArray results = list.getJSONArray(KEY_RESULTS);

            trailers = new ArrayList<>();

            for (int i = 0; i < results.length(); i++) {
                Trailer trailer = new Trailer();

                JSONObject result = results.getJSONObject(i);

                trailer.setId(result.getString(KEY_ID));
                trailer.setKey(result.getString(KEY_KEY));
                trailer.setName(result.getString(KEY_NAME));
                trailer.setSite(result.getString(KEY_SITE));
                trailer.setType(result.getString(KEY_TYPE));

                trailers.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trailers;
    }
}
