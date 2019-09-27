package com.example.popularmovies.data.network;

import com.example.popularmovies.model.Movies;
import com.example.popularmovies.model.Reviews;
import com.example.popularmovies.model.Trailers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MoviesApi {
    interface MoviesInterface {
        @GET("{sortMethod}")
        Call<Movies> getMovies(@Path("sortMethod") String sortMethod);
    }

    interface MovieTrailers {
        @GET("{id}/videos")
        Call<Trailers> getTrailers(@Path("id") Integer id);
    }

    interface MovieReviews {
        @GET("{id}/reviews")
        Call<Reviews> getReviews(@Path("id") Integer id);
    }
}
