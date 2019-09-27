package com.example.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.example.popularmovies_stage1.R;

import com.example.popularmovies.adapters.MovieAdapter;
import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.database.MoviesViewModel;
import com.example.popularmovies.model.Movie;
import com.example.popularmovies.model.Movies;
import com.example.popularmovies.network.TmdbRestClient;
import com.example.popularmovies.network.NetworkUtils;

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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // TODO rever sugestão da revisão de código da stage 1
    private static final String LOG_TAG = AppDatabase.class.getSimpleName();

    @BindView(R.id.rv_movies)
    RecyclerView recyclerView;
    // Create a variable to store a reference to the error message TextView
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    // Create a ProgressBar variable to store a reference to the ProgressBar
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private List<Movie> movies = null;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // TODO Use this variable in all the program?
    public AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Bind the view using Butter Knife
         */
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

        /**
         * Use this setting to improve performance as we know that changes in content do not change
         * the layout size of the RecyclerView
         */
        recyclerView.setHasFixedSize(true);

        /**
         * Use a grid layout manager
         */
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        /**
         * Specify an adapter
         */
        mAdapter = new MovieAdapter(this, movies);

        recyclerView.setAdapter(mAdapter);

        // Get Movies - API call + JSON parse + GridView populate
        // TODO as ViewModel was implemented, should I use savedInstanceState only for popular and
        //  top rated movies, and not for the favorites?
        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.parcel_movie))) {
            getMovies(getSortMethod());
        } else {
            Parcelable[] parcelable = savedInstanceState.
                    getParcelableArray(getString(R.string.parcel_movie));

            if (parcelable != null) {
                int numMovieObjects = parcelable.length;
                List<Movie> movies = new ArrayList<>();
                for (int i = 0; i < numMovieObjects; i++) {
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
            Log.d(LOG_TAG, "Actively retrieving the movies from the database");
            // Setup ViewModel
            MoviesViewModel viewModel = ViewModelProviders.of(this).get(MoviesViewModel.class);
            viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movies_) {
                    Log.d(LOG_TAG, "Updating list of movies from LiveData in ViewModel");
                    ((MovieAdapter) mAdapter).setMovies(movies_);
                    invalidateOptionsMenu();
                }
            });
        } else {
            if (NetworkUtils.isOnline(getApplicationContext())) {
                Call<Movies> call = TmdbRestClient.getInstance().getMovies().getMovies(sortMethod);
                Callback<Movies> callback = new Callback<Movies>() {
                    @Override
                    public void onResponse(Call<Movies> call, Response<Movies> response) {
                        if (!response.isSuccessful()) {
                            showErrorMessage(R.string.error_message_results);
                            return;
                        }
                        movies = response.body().getMovies();
                        ((MovieAdapter) mAdapter).setMovies(movies);
                        showMoviesPosters();
                    }

                    @Override
                    public void onFailure(Call<Movies> call, Throwable t) {
                        showErrorMessage(R.string.error_message_results);
                    }
                };
                call.enqueue(callback);
            } else {
                showErrorMessage(R.string.error_message_network);
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
