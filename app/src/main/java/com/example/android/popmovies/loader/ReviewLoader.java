package com.example.android.popmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popmovies.Utils;
import com.example.android.popmovies.model.Review;

import java.util.List;

public class ReviewLoader extends AsyncTaskLoader<List<Review>> {

    private final String mUrl;

    public ReviewLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Review> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<Review> reviews = Utils.fetchReviewData(mUrl);
        return reviews;
    }
}