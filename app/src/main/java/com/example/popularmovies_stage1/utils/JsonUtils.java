package com.example.popularmovies_stage1.utils;

import com.example.popularmovies_stage1.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    private static final String KEY_RESULTS = "results";

    private static final String KEY_ID = "id";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";

    public static Movie[] parseMovieJson(String json) {
        Movie[] movies = null;

        try {
            JSONObject list = new JSONObject(json);

            JSONArray results = list.getJSONArray(KEY_RESULTS);

            movies = new Movie[results.length()];

            for (int i = 0; i < results.length(); i++) {
                movies[i] = new Movie();

                JSONObject result = results.getJSONObject(i);

                movies[i].setId(result.getInt(KEY_ID));
                movies[i].setOriginalTitle(result.getString(KEY_ORIGINAL_TITLE));
                movies[i].setOverview(result.getString(KEY_OVERVIEW));
                movies[i].setVoteAverage(result.getDouble(KEY_VOTE_AVERAGE));
                movies[i].setReleaseDate(result.getString(KEY_RELEASE_DATE));
                movies[i].setPosterPath(result.optString(KEY_POSTER_PATH));
                movies[i].setBackdropPath(result.optString(KEY_BACKDROP_PATH));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }
}
