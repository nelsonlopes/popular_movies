package com.example.popularmovies_stage1;

import android.content.Intent;
import android.example.popularmovies_stage1.R;

import com.example.popularmovies_stage1.model.Movie;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity { ;
    @BindView(R.id.vote_average_tv)
    TextView voteAverageTv;

    @BindView(R.id.release_date_tv)
    TextView releaseDateTv;

    @BindView(R.id.overview_tv)
    TextView overviewTv;

    @BindView(R.id.backdrop_path_iv)
    ImageView backdropPathIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // bind the view using butterknife
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        Movie movie = intent.getParcelableExtra(getString(R.string.parcel_movie));

        setTitle(movie.getOriginalTitle());
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
}
