package com.example.popularmovies.network;

import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.Movies;
import com.example.popularmovies.model.Reviews;
import com.example.popularmovies.model.Trailer;
import com.example.popularmovies.model.Review;
import com.example.popularmovies.model.Trailers;

import java.util.List;

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
