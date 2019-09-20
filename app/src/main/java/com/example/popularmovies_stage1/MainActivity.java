package com.example.popularmovies_stage1;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.example.popularmovies_stage1.R;

import com.example.popularmovies_stage1.Database.AppDatabase;
import com.example.popularmovies_stage1.Database.MainViewModel;
import com.example.popularmovies_stage1.model.Movie;
import com.example.popularmovies_stage1.utils.JsonUtils;
import com.example.popularmovies_stage1.utils.NetworkUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();

    @BindView(R.id.rv_movies)
    RecyclerView recyclerView;
    // Create a variable to store a reference to the error message TextView
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    // Create a ProgressBar variable to store a reference to the ProgressBar
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    //private Movie[] movies = null;
    private List<Movie> movies = null;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public AppDatabase mDb; // TODO todo o programa devia usar esta variável publica, em vez de ter nos details também

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind the view using butterknife
        ButterKnife.bind(this);

        mDb = AppDatabase.getInstance(getApplicationContext());

        /**
         * If something goes wrong (like there is no internet connection), the usar taps the
         * TextView and a new call to the API is made.
         */
        mErrorMessageDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMovies(getSortMethod());
            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a grid layout manager
        //layoutManager = new GridLayoutManager(this, 2);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new MovieAdapter(this, movies);

        recyclerView.setAdapter(mAdapter);

        // Get Movies - API call + JSON parse + GridView populate
        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.parcel_movie))) {
            getMovies(getSortMethod());
        } else {
            Parcelable[] parcelable = savedInstanceState.
                    getParcelableArray(getString(R.string.parcel_movie));

            if (parcelable != null) {
                int numMovieObjects = parcelable.length;
                //Movie[] movies = new Movie[numMovieObjects];
                List<Movie> movies = new ArrayList<>();
                for (int i = 0; i < numMovieObjects; i++) {
                    //movies[i] = (Movie) parcelable[i];
                    movies.add((Movie) parcelable[i]);
                }

                ((MovieAdapter) mAdapter).setMovies(movies);
            }
        }
    }

    private void getMovies(String sortMethod) {
        /**
         * If the sort method is favorites, the data is persisted locally. Else, calls the API
         */
        if (sortMethod == getResources().getString(R.string.tmdb_sort_favorites)) {
            //Log.d(LOG_TAG, "Actively retrieving the movies from the database");
            //final LiveData<List<Movie>> movies = mDb.movieDao().getMovies();
            // Setup ViewModel
            MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies_) {
                    Log.d(LOG_TAG, "Updating list of movies from LiveData in ViewModel");
                    ((MovieAdapter) mAdapter).setMovies(movies_);
                    invalidateOptionsMenu();
                }
            });
        } else {
            URL tmdbSearchUrl = NetworkUtils.buildUrl(sortMethod);
            if (isOnline()) {
                new tmdbQueryTask().execute(tmdbSearchUrl);
            } else {
                showErrorMessage(R.string.error_message_network);
            }
        }
    }

    /** Checks if there is internet connection
     * Reference: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out/2001824#2001824
     */
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private class tmdbQueryTask extends AsyncTask<URL, Void, String> {
        // Override onPreExecute to set the loading indicator to visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String tmdbSearchResults = null;
            try {
                tmdbSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                Log.d("POP-", tmdbSearchResults);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tmdbSearchResults;
        }

        @Override
        protected void onPostExecute(String tmdbSearchResults) {
            // As soon as the loading is complete, hide the loading indicator
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (tmdbSearchResults != null && !tmdbSearchResults.equals("")) {
                /**
                 * Call showJsonDataView, parse the JSON and populate the RecyclerView if we have
                 * valid, non-null results
                  */
                showMoviesPosters();
                movies = JsonUtils.parseMovieJson(tmdbSearchResults);
                ((MovieAdapter) mAdapter).setMovies(movies);
                // Updates menu items (hides the active option)
                invalidateOptionsMenu();
            } else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage(R.string.error_message_results);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /**
         * Hides the selected option, so the user won't call the API again for the results that
         * he already has on the screen
         */
        MenuItem itemPopular = menu.findItem(R.id.sort_pop_desc);
        MenuItem itemVoteAverage = menu.findItem(R.id.sort_vote_count_desc);
        MenuItem itemFavorites = menu.findItem(R.id.sort_favorites);

        if (getSortMethod() == getResources().getString(R.string.tmdb_sort_popular)) {
            itemPopular.setVisible(false);
        } else if (getSortMethod() == getResources().getString(R.string.tmdb_sort_top_rated)) {
            itemVoteAverage.setVisible(false);
        } else if (getSortMethod() == getResources().getString(R.string.tmdb_sort_favorites)) {
            itemFavorites.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sort_pop_desc:
                updateSharedPrefs(getResources().getString(R.string.tmdb_sort_popular));
                getMovies(getResources().getString(R.string.tmdb_sort_popular));
                return true;
            case R.id.sort_vote_count_desc:
                updateSharedPrefs(getResources().getString(R.string.tmdb_sort_top_rated));
                getMovies(getResources().getString(R.string.tmdb_sort_top_rated));
                return true;
            case R.id.sort_favorites:
                updateSharedPrefs(getResources().getString(R.string.tmdb_sort_favorites));
                getMovies(getResources().getString(R.string.tmdb_sort_favorites));
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private String getSortMethod() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        return prefs.getString(getString(R.string.pref_sort_method_key),
                getString(R.string.tmdb_sort_popular));
    }

    private void updateSharedPrefs(String sortMethod) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_sort_method_key), sortMethod);
        editor.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int numMovieObjects = mAdapter.getItemCount();
        if (numMovieObjects > 0) {
            Movie[] movies = new Movie[numMovieObjects];
            for (int i = 0; i < numMovieObjects; i++) {
                movies[i] = ((MovieAdapter) mAdapter).getItem(i);
            }

            outState.putParcelableArray(getString(R.string.parcel_movie), movies);
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * This method will make the RecyclerView visible and hide the error message.
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showMoviesPosters() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.GONE);
        // Then, make sure the JSON data is visible
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the RecyclerView.
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage(int errorMessage) {
        // First, hide the currently visible data
        recyclerView.setVisibility(View.GONE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(getResources().getString(errorMessage));
    }
}
