package com.example.android.popmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private List<Trailer> mTrailers = new ArrayList<>();
    private final LayoutInflater mInflater;
    private final ItemClickListener mClickListener;

    public TrailerAdapter(Context context, List<Trailer> trailers, ItemClickListener clickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mTrailers = trailers;
        this.mClickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.trailer_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trailer trailer = mTrailers.get(position);
        holder.mTitle.setText(trailer.getTitle());
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mClickListener.onItemClick(getAdapterPosition());
        }
    }

    public Trailer getItem(int position) {
        return mTrailers.get(position);
    }

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public void clear() {
        mTrailers.clear();
        this.notifyDataSetChanged();
    }
}
