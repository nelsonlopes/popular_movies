package com.example.popularmovies_stage1.Database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.popularmovies_stage1.model.Movie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String LOG_TAG = MainViewModel.class.getSimpleName();
    private LiveData<List<Movie>> movies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(LOG_TAG, "Actively retrieving the movies from the database");
        movies = database.movieDao().getMovies();
    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
