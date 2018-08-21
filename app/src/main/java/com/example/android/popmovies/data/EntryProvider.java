package com.example.android.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.popmovies.data.EntryContract.Entry;

public class EntryProvider extends ContentProvider {

    private static final String LOG_TAG = EntryProvider.class.getSimpleName();

    private EntryDbHelper mDbHelper;

    private static final int ENTRIES = 100;

    private static final int ENTRY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(EntryContract.CONTENT_AUTHORITY, EntryContract.PATH_ENTRIES, ENTRIES);
        sUriMatcher.addURI(EntryContract.CONTENT_AUTHORITY, EntryContract.PATH_ENTRIES + "/#", ENTRY_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new EntryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ENTRIES:
                cursor = database.query(Entry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ENTRY_ID:
                selection = Entry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(Entry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ENTRIES:
                return insertEntry(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertEntry(Uri uri, ContentValues values) {
        Integer tmdbId = values.getAsInteger(Entry.COLUMN_TMDB_ID);
        if (tmdbId == null || tmdbId < 0) {
            throw new IllegalArgumentException("Valid Id is required");
        }
        String title = values.getAsString(Entry.COLUMN_TITLE);
        if (title == null || title.length() == 0) {
            throw new IllegalArgumentException("Title is required");
        }
        String poster = values.getAsString(Entry.COLUMN_POSTER);
        if (poster == null || poster.length() == 0) {
            throw new IllegalArgumentException("Poster path is required");
        }
        String overview = values.getAsString(Entry.COLUMN_OVERVIEW);
        if (overview == null || overview.length() == 0) {
            throw new IllegalArgumentException("Overview is required");
        }
        Double rating = values.getAsDouble(Entry.COLUMN_RATING);
        if (rating == null || rating < 0 || rating > 10) {
            throw new IllegalArgumentException("Valid rating is required");
        }
        String releaseDate = values.getAsString(Entry.COLUMN_RELEASE_DATE);
        if (releaseDate == null || releaseDate.length() == 0) {
            throw new IllegalArgumentException("Release date is required");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(Entry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ENTRIES:
                return updateEntry(uri, contentValues, selection, selectionArgs);
            case ENTRY_ID:
                selection = Entry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateEntry(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateEntry(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(Entry.COLUMN_TMDB_ID)) {
            Integer tmdbId = values.getAsInteger(Entry.COLUMN_TMDB_ID);
            if (tmdbId == null || tmdbId < 0) {
                throw new IllegalArgumentException("Valid Id is required");
            }
        }
        if (values.containsKey(Entry.COLUMN_TITLE)) {
            String title = values.getAsString(Entry.COLUMN_TITLE);
            if (title == null || title.length() == 0) {
                throw new IllegalArgumentException("Title is required");
            }
        }
        if (values.containsKey(Entry.COLUMN_POSTER)) {
            String poster = values.getAsString(Entry.COLUMN_POSTER);
            if (poster == null || poster.length() == 0) {
                throw new IllegalArgumentException("Poster path is required");
            }
        }
        if (values.containsKey(Entry.COLUMN_OVERVIEW)) {
            String overview = values.getAsString(Entry.COLUMN_OVERVIEW);
            if (overview == null || overview.length() == 0) {
                throw new IllegalArgumentException("Overview is required");
            }
        }
        if (values.containsKey(Entry.COLUMN_RATING)) {
            Double rating = values.getAsDouble(Entry.COLUMN_RATING);
            if (rating == null || rating < 0 || rating > 10) {
                throw new IllegalArgumentException("Valid rating is required");
            }
        }
        if (values.containsKey(Entry.COLUMN_RELEASE_DATE)) {
            String releaseDate = values.getAsString(Entry.COLUMN_RELEASE_DATE);
            if (releaseDate == null || releaseDate.length() == 0) {
                throw new IllegalArgumentException("Release date is required");
            }
        }

        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(Entry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ENTRIES:
                rowsDeleted = database.delete(Entry.TABLE_NAME, selection, selectionArgs);
                break;
            case ENTRY_ID:
                selection = Entry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(Entry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ENTRIES:
                return Entry.CONTENT_LIST_TYPE;
            case ENTRY_ID:
                return Entry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}