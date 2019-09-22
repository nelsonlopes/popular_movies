package com.example.popularmovies.adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.popularmovies.model.Trailer;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.MyViewHolder> {

    private static final String YOUTUBE_APP_BASE_URI = "vnd.youtube:";
    private static final String YOUTUBE_WEB_BASE_URL = "http://www.youtube.com/watch?v=";
    private Context mContext;
    private List<Trailer> trailers;

    // Provide a reference to the views for each data item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public TrailerAdapter(Context context, List<Trailer> mTrailers) {
        mContext = context;
        trailers = mTrailers;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TrailerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        TextView v = new TextView(mContext);

        TrailerAdapter.MyViewHolder vh = new TrailerAdapter.MyViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final TrailerAdapter.MyViewHolder holder, int position) {
        // - get element from the dataset at this position
        // - replace the contents of the view with that

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trailer trailer = getItem(holder.getAdapterPosition());

                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_APP_BASE_URI +
                        trailer.getKey()));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(YOUTUBE_WEB_BASE_URL + trailer.getKey()));
                try {
                    mContext.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    mContext.startActivity(webIntent);
                }
            }
        });

        holder.textView.setText(trailers.get(position).getName());
    }

    // Return the size of the dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (trailers != null) {
            return trailers.size();
        } else {
            return 0;
        }
    }

    public Trailer getItem(int position) {
        if (trailers == null || trailers.size() == 0) {
            return null;
        }

        return trailers.get(position);
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }
}
