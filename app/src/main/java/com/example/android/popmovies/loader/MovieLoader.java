package com.example.android.popmovies.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popmovies.R;
import com.example.android.popmovies.Utils;
import com.example.android.popmovies.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private final String mUrl;

    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        List<Movie> movies;
        if (mUrl == null) {
            return null;
        }
        if (mUrl.equals(getContext().getString(R.string.settings_order_by_favorites_value))) {
            movies = Utils.fetchMovies(getContext());
            return movies;
        } else {
            movies = Utils.fetchMovieData(mUrl);
            return movies;
        }
    }
}