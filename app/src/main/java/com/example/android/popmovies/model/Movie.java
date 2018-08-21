package com.example.android.popmovies.model;

import com.example.android.popmovies.MainActivity;

import java.io.Serializable;

public class Movie implements Serializable {

    private final int mId;
    private final String mTitle;
    private final String mPoster;
    private final String mOverview;
    private final double mRating;
    private final String mReleaseDate;
    private final int mDbId;

    public Movie(int id, String title, String poster, String overview, double rating, String releaseDate) {
        mId = id;
        mTitle = title;
        mPoster = poster;
        mOverview = overview;
        mRating = rating;
        mReleaseDate = releaseDate;
        mDbId = -1;
    }


    public Movie(int id, String title, String poster, String overview, double rating, String releaseDate, int dbId) {
        mId = id;
        mTitle = title;
        mPoster = poster;
        mOverview = overview;
        mRating = rating;
        mReleaseDate = releaseDate;
        mDbId = dbId;
    }

    public int getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPoster() {
        return mPoster;
    }

    public String getOverview() {
        return mOverview;
    }

    public double getRating() {
        return mRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getPosterUrl() {
        return MainActivity.POSTER_URL + mPoster;
    }

    public int getDbId() {
        return mDbId;
    }
}
