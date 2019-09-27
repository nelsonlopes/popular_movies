package com.example.popularmovies.data;

import android.app.Application;

import com.example.popularmovies.data.database.MovieRoomDatabase;
import com.example.popularmovies.data.database.MovieDao;
import com.example.popularmovies.model.Movie;

import java.util.concurrent.ExecutionException;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class MovieRepository {

    private MovieDao mMovieDao;
    private LiveData<List<Movie>> mAllMovies;

    public MovieRepository(Application application) {
        MovieRoomDatabase db = MovieRoomDatabase.getDatabase(application);
        mMovieDao = db.movieDao();
        mAllMovies = mMovieDao.getMovies();
    }

    public LiveData<List<Movie>> getAllMovies() {
        return mAllMovies;
    }

    public void insert(Movie movie) {
        new insertAsyncTask(mMovieDao).execute(movie);
    }

    public void delete(Movie movie) {
        new removeAsyncTask(mMovieDao).execute(movie);
    }

    public Movie getMovie(Integer integer) {
        try {
            return new getMovieAsyncTask(mMovieDao).execute(integer).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static class insertAsyncTask extends AsyncTask<Movie, Void, Void> {
        private MovieDao asyncTaskDao;

        insertAsyncTask(MovieDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            asyncTaskDao.insertMovie(movies[0]);
            return null;
        }
    }

    private static class removeAsyncTask extends AsyncTask<Movie, Void, Void>{
        private MovieDao asyncTaskDao;

        removeAsyncTask(MovieDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            asyncTaskDao.deleteMovie(movies[0]);
            return null;
        }
    }

    private static class getMovieAsyncTask extends AsyncTask<Integer, Void, Movie> {
        private MovieDao asyncTaskDao;

        getMovieAsyncTask(MovieDao movieDao) {
            asyncTaskDao = movieDao;
        }

        @Override
        protected Movie doInBackground(Integer... integers) {
            return asyncTaskDao.getMovieById(integers[0]);
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);
        }
    }
}
