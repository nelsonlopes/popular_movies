package com.example.popularmovies.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.example.popularmovies_stage1.R;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.popularmovies.model.Review;
import com.example.popularmovies.model.Trailer;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private Context mContext;
    private List<Review> reviews;

    // Provide a reference to the views for each data item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        //public TextView textView;
        public View view;

        //public MyViewHolder(TextView v) {
        public MyViewHolder(View v) {
            super(v);
            //textView = v;
            view = v;
        }
    }

    public ReviewAdapter(Context context, List<Review> mReviews) {
        mContext = context;
        reviews = mReviews;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        //TextView v = new TextView(mContext);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);

        ReviewAdapter.MyViewHolder vh = new ReviewAdapter.MyViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ReviewAdapter.MyViewHolder holder, int position) {
        // - get element from the dataset at this position
        // - replace the contents of the view with that

        //holder.textView.setText(reviews.get(position).getContent());

        TextView reviewAuthorTv = holder.view.findViewById(R.id.tv_review_author);
        TextView reviewContentTv = holder.view.findViewById(R.id.tv_review_content);

        reviewAuthorTv.setText(reviews.get(position).getAuthor());
        reviewContentTv.setText(reviews.get(position).getContent());
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (reviews != null) {
            return reviews.size();
        } else {
            return 0;
        }
    }

    public Review getItem(int position) {
        if (reviews == null || reviews.size() == 0) {
            return null;
        }

        return reviews.get(position);
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }
}
