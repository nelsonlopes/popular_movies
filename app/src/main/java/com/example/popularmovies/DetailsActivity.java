package com.example.popularmovies;

import android.content.Intent;
import android.example.popularmovies_stage1.R;

import com.example.popularmovies.adapters.ReviewAdapter;
import com.example.popularmovies.adapters.TrailerAdapter;
import com.example.popularmovies.database.AppDatabase;
import com.example.popularmovies.model.Movie;

import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.popularmovies.model.Review;
import com.example.popularmovies.model.Trailer;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailsActivity extends AppCompatActivity { ;
    @BindView(R.id.vote_average_tv)
    TextView voteAverageTv;
    @BindView(R.id.release_date_tv)
    TextView releaseDateTv;
    @BindView(R.id.overview_tv)
    TextView overviewTv;
    @BindView(R.id.backdrop_path_iv)
    ImageView backdropPathIv;
    @BindView(R.id.btnFavorite)
    Button btnFavorite;
    @BindView(R.id.rv_trailers)
    RecyclerView rv_trailers;
    @BindView(R.id.rv_reviews)
    RecyclerView rv_reviews;
    // Create a variable to store a reference to the error message TextView
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    // Create a ProgressBar variable to store a reference to the ProgressBar
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    private Movie movie;

    // Trailers
    private List<Trailer> trailers = null;
    private RecyclerView.Adapter mAdapterTrailers;
    private RecyclerView.LayoutManager layoutManagerTrailers;

    // Reviews
    private List<Review> reviews = null;
    private RecyclerView.Adapter mAdapterReviews;
    private RecyclerView.LayoutManager layoutManagerReviews;

    public AppDatabase mDb;

    // TODO the list of favorites can be empty. Add some information to the user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        /**
         * Bind the view using Butter Knife
          */
        ButterKnife.bind(this);

        mDb = AppDatabase.getInstance(getApplicationContext());

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        movie = intent.getParcelableExtra(getString(R.string.parcel_movie));

        setTitle(movie.getOriginalTitle());
        Picasso.get()
                .load(NetworkUtils.TMDB_POSTER_BASE_URL + movie.getBackdropPath())
                .placeholder(R.drawable.round_local_movies_black_24dp)
                .into(backdropPathIv);
        isFavorite();
        voteAverageTv.setText(movie.getVoteAverage() + "/10");
        releaseDateTv.setText(movie.getReleaseDate());
        overviewTv.setText(movie.getOverview());

        /**
         * Trailers
         */
        // use a linear layout manager
        layoutManagerTrailers = new LinearLayoutManager(this);
        rv_trailers.setLayoutManager(layoutManagerTrailers);

        rv_trailers.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));

        // specify an adapter
        mAdapterTrailers = new TrailerAdapter(this, trailers);

        rv_trailers.setAdapter(mAdapterTrailers);

        // Get Trailers - API call + JSON parse + RecycleView populate
        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.parcel_trailer))) {
            getTrailers(movie.getId());
        } else {
            Parcelable[] parcelableTrailers = savedInstanceState.
                    getParcelableArray(getString(R.string.parcel_trailer));

            if (parcelableTrailers != null) {
                int numTrailerObjects = parcelableTrailers.length;
                List<Trailer> trailers = new ArrayList<>();
                for (int i = 0; i < numTrailerObjects; i++) {
                    trailers.add((Trailer) parcelableTrailers[i]);
                }

                ((TrailerAdapter) mAdapterTrailers).setTrailers(trailers);
            }
        }

        /**
         * Reviews
         */
        // use a linear layout manager
        layoutManagerReviews = new LinearLayoutManager(this);
        rv_reviews.setLayoutManager(layoutManagerReviews);

        rv_reviews.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                DividerItemDecoration.VERTICAL));

        // specify an adapter
        mAdapterReviews = new ReviewAdapter(this, reviews);

        rv_reviews.setAdapter(mAdapterReviews);

        // Get Reviews - API call + JSON parse + RecycleView populate
        if (savedInstanceState == null || !savedInstanceState.containsKey(getString(R.string.parcel_review))) {
            getReviews(movie.getId());
        } else {
            Parcelable[] parcelableReviews = savedInstanceState.
                    getParcelableArray(getString(R.string.parcel_review));

            if (parcelableReviews != null) {
                int numReviewObjects = parcelableReviews.length;
                List<Review> reviews = new ArrayList<>();
                for (int i = 0; i < numReviewObjects; i++) {
                    reviews.add((Review) parcelableReviews[i]);
                }

                ((ReviewAdapter) mAdapterReviews).setReviews(reviews);
            }
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show();
    }

    public void isFavorite() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mDb.movieDao().getMovieById(movie.getId()) != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnFavorite.setBackgroundResource(R.drawable.ic_favorite_24px);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnFavorite.setBackgroundResource(R.drawable.ic_favorite_border_24px);
                        }
                    });
                }
            }
        });
    }

    @OnClick(R.id.btnFavorite)
    public void setFavorite(View view) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                /**
                 * Should this logic be inside the Runnable, or outside with one Runnable inside
                 * if condition is met (for the deletion), and another Runnable if it is'nt
                 * (for the insert)?
                 */
                if (mDb.movieDao().getMovieById(movie.getId()) != null) {
                    mDb.movieDao().deleteMovie(movie);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Removed from favorites.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    mDb.movieDao().insertMovie(movie);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Added to favorites.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        isFavorite();
    }

    // TODO merge this method and the next
    private void getTrailers(int movieId) {
        URL tmdbSearchUrl = NetworkUtils.buildUrl(movieId, "videos");
        if (NetworkUtils.isOnline(getApplicationContext())) {
            new tmdbQueryTask().execute(tmdbSearchUrl);
        } else {
            showErrorMessage(R.string.error_message_network);
        }
    }

    private void getReviews(int movieId) {
        URL tmdbSearchUrl = NetworkUtils.buildUrl(movieId, "reviews");
        if (NetworkUtils.isOnline(getApplicationContext())) {
            new tmdbQueryTaskReviews().execute(tmdbSearchUrl);
        } else {
            showErrorMessage(R.string.error_message_network);
        }
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
                showTrailersList();
                trailers = JsonUtils.parseTrailerJson(tmdbSearchResults);
                ((TrailerAdapter) mAdapterTrailers).setTrailers(trailers);
            } else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage(R.string.error_message_results);
            }
        }
    }

    private class tmdbQueryTaskReviews extends AsyncTask<URL, Void, String> {
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
                showReviewsList();
                reviews = JsonUtils.parseReviewJson(tmdbSearchResults);
                ((ReviewAdapter) mAdapterReviews).setReviews(reviews);
            } else {
                // Call showErrorMessage if the result is null in onPostExecute
                showErrorMessage(R.string.error_message_results);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int numTrailerObjects = mAdapterTrailers.getItemCount();
        if (numTrailerObjects > 0) {
            Trailer[] trailers = new Trailer[numTrailerObjects];
            for (int i = 0; i < numTrailerObjects; i++) {
                trailers[i] = ((TrailerAdapter) mAdapterTrailers).getItem(i);
            }

            outState.putParcelableArray(getString(R.string.parcel_trailer), trailers);
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * This method will make the RecyclerView visible and hide the error message.
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showTrailersList() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.GONE);
        // Then, make sure the JSON data is visible
        rv_trailers.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the RecyclerView visible and hide the error message.
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showReviewsList() {
        // First, make sure the error is invisible
        mErrorMessageDisplay.setVisibility(View.GONE);
        // Then, make sure the JSON data is visible
        rv_reviews.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the RecyclerView.
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage(int errorMessage) {
        // First, hide the currently visible data
        rv_trailers.setVisibility(View.GONE);
        // Then, show the error
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(getResources().getString(errorMessage));
    }
}
