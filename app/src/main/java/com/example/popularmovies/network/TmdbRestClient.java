package com.example.popularmovies.network;

import android.example.popularmovies_stage1.BuildConfig;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TmdbRestClient {
    private final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public final static String TMDB_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w185";


    private MoviesApi.MoviesInterface movies;
    private MoviesApi.MovieTrailers trailers;
    private MoviesApi.MovieReviews reviews;

    private Retrofit retrofit;

    private static TmdbRestClient instance = null;

    private TmdbRestClient() {
        initializeRetrofit();
    }

    public static TmdbRestClient getInstance() {
        if (instance == null) {
            instance = new TmdbRestClient();
        }

        return instance;
    }

    public MoviesApi.MoviesInterface getMovies() {
        if (movies == null) {
            movies = retrofit.create(MoviesApi.MoviesInterface.class);
        }

        return movies;
    }

    public MoviesApi.MovieTrailers getTrailers() {
        if (trailers == null) {
            trailers = retrofit.create(MoviesApi.MovieTrailers.class);
        }

        return trailers;
    }

    public MoviesApi.MovieReviews getReviews() {
        if (reviews == null) {
            reviews = retrofit.create(MoviesApi.MovieReviews.class);
        }

        return reviews;
    }

    private void initializeRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        HttpUrl url = request.url()
                                .newBuilder()
                                .addQueryParameter("api_key", BuildConfig.TmdbApiKey)
                                .build();
                        Request.Builder builder = request.newBuilder()
                                .url(url)
                                .method(request.method(), request.body());
                        request = builder.build();

                        return chain.proceed(request);
                    }
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }
}
