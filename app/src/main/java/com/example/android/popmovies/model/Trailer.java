package com.example.android.popmovies.model;

import java.io.Serializable;

public class Trailer implements Serializable {

    private final String mId;
    private final String mTitle;
    private final String mKey;


    public Trailer(String id, String title, String key) {
        mId = id;
        mTitle = title;
        mKey = key;
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getKey() {
        return mKey;
    }
}
