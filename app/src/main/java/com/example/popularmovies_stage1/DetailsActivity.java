package com.example.popularmovies_stage1;

import android.content.Intent;
import android.example.popularmovies_stage1.R;

import com.example.popularmovies_stage1.Database.AppDatabase;
import com.example.popularmovies_stage1.model.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

    private Movie movie;

    public AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // bind the view using butterknife
        ButterKnife.bind(this);

        mDb = AppDatabase.getInstance(getApplicationContext());

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        movie = intent.getParcelableExtra(getString(R.string.parcel_movie));

        setTitle(movie.getOriginalTitle());
        isFavorite();
        voteAverageTv.setText(movie.getVoteAverage() + "/10");
        releaseDateTv.setText(movie.getReleaseDate());
        overviewTv.setText(movie.getOverview());
        Picasso.get()
                .load(movie.getBackdropPath())
                .placeholder(R.drawable.round_local_movies_black_24dp)
                .into(backdropPathIv);


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
                    //Toast.makeText(getApplicationContext(), "No longer a favorite!", Toast.LENGTH_SHORT).show();
                } else {
                    mDb.movieDao().insertMovie(movie);
                    //Toast.makeText(getApplicationContext(), "Added as favorite!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        isFavorite();
    }
}
