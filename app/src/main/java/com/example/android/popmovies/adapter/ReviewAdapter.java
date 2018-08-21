package com.example.android.popmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> mReviews = new ArrayList<>();
    private final LayoutInflater mInflater;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.mInflater = LayoutInflater.from(context);
        this.mReviews = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.review_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = mReviews.get(position);
        holder.mAuthor.setText(review.getAuthor());
        holder.mContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mAuthor;
        public final TextView mContent;

        public ViewHolder(View itemView) {
            super(itemView);
            mAuthor = itemView.findViewById(R.id.author);
            mContent = itemView.findViewById(R.id.content);
        }
    }

    public void clear() {
        mReviews.clear();
        this.notifyDataSetChanged();
    }
}
