package com.example.android.popmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popmovies.Utils;
import com.example.android.popmovies.model.Trailer;

import java.util.List;

public class TrailerLoader extends AsyncTaskLoader<List<Trailer>> {

    private final String mUrl;

    public TrailerLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Trailer> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<Trailer> trailers = Utils.fetchTrailerData(mUrl);
        return trailers;
    }
}