package com.example.android.popmovies.model;

import java.io.Serializable;

public class Review implements Serializable {

    private final String mId;
    private final String mAuthor;
    private final String mContent;


    public Review(String id, String author, String content) {
        mId = id;
        mAuthor = author;
        mContent = content;
    }

    public String getId() {
        return mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }
}
