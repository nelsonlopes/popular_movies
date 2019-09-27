package com.example.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.example.popularmovies_stage1.R;

import com.example.popularmovies.DetailsActivity;
import com.example.popularmovies.model.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.popularmovies.network.NetworkUtils;
import com.example.popularmovies.network.TmdbRestClient;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private Context mContext;
    private List<Movie> movies;

    // Provide a reference to the views for each data item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(ImageView v) {
            super(v);
            imageView = v;
        }
    }

    public MovieAdapter(Context context, List<Movie> mMovies) {
        mContext = context;
        movies = mMovies;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        ImageView v = new ImageView(mContext);
        v.setAdjustViewBounds(true);
        v.setContentDescription(mContext.getResources()
                .getString(R.string.poster_content_description));

        MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from the dataset at this position
        // - replace the contents of the view with that

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Movie movie = getItem(holder.getAdapterPosition());

                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(mContext.getResources().getString(R.string.parcel_movie), movie);

                mContext.startActivity(intent);
            }
        });

        /**
         * Possible improvement: check if poster_path is null and if it is, instead of the
         * ImageView, inflate a TextView and set the text to the original title, so the user
         * now to what movie that item corresponds.
         */
        Picasso.get()
                .load(TmdbRestClient.TMDB_POSTER_BASE_URL +
                        movies.get(position).getPosterPath())
                .placeholder(R.drawable.round_local_movies_black_24dp)
                .into(holder.imageView);
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.size();
        } else {
            return 0;
        }
    }

    public Movie getItem(int position) {
        if (movies == null || movies.size() == 0) {
            return null;
        }

        return movies.get(position);
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
}