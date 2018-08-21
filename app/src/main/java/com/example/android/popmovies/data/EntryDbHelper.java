package com.example.android.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popmovies.data.EntryContract.Entry;

public class EntryDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Entries.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Entry.TABLE_NAME + " (" +
                    Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Entry.COLUMN_TMDB_ID + " INTEGER NOT NULL UNIQUE ON CONFLICT REPLACE," +
                    Entry.COLUMN_TITLE + " TEXT NOT NULL," +
                    Entry.COLUMN_POSTER + " TEXT NOT NULL," +
                    Entry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                    Entry.COLUMN_RATING + " REAL NOT NULL," +
                    Entry.COLUMN_RELEASE_DATE + " TEXT NOT NULL)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Entry.TABLE_NAME;

    public EntryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
