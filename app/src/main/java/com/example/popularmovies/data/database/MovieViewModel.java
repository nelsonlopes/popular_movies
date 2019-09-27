package com.example.popularmovies.data.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.popularmovies.data.MovieRepository;
import com.example.popularmovies.model.Movie;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String LOG_TAG = MovieViewModel.class.getSimpleName();
    private MovieRepository mRepository;
    private LiveData<List<Movie>> movies;

    public MovieViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
        movies = mRepository.getAllMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }

    public void insert(Movie movie) {
        mRepository.insert(movie);
    }

    public void delete(Movie movie) {
        mRepository.delete(movie);
    }

    public Movie getMovie(Integer id) {
        return mRepository.getMovie(id);
    }

    public boolean isFavorite(int id) {
        return this.getMovie(id) != null;
    }
}
