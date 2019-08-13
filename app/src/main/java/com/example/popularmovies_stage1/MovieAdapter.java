package com.example.popularmovies_stage1;

import android.content.Context;
import android.content.Intent;
import android.example.popularmovies_stage1.R;

import com.example.popularmovies_stage1.model.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private Context mContext;
    private Movie[] movies;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public MyViewHolder(ImageView v) {
            super(v);
            imageView = v;
        }
    }

    public MovieAdapter(Context context, Movie[] mMovies) {
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that

        /**
         * Is this the correct way to do this? As getItem() asks position to be final, as it is
         * called from an inner class, but Lint (code inspection) warns to not treat position
         * as fixed?
         */
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
                .load(movies[position].getPosterPath())
                .placeholder(R.drawable.round_local_movies_black_24dp)
                .into(holder.imageView);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.length;
        } else {
            return 0;
        }
    }

    public Movie getItem(int position) {
        if (movies == null || movies.length == 0) {
            return null;
        }

        return movies[position];
    }

    public void setMovies(Movie[] movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }
}