package com.example.android.popmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.popmovies.data.EntryContract.Entry;
import com.example.android.popmovies.model.Movie;
import com.example.android.popmovies.model.Review;
import com.example.android.popmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.popmovies.MainActivity.LOG_TAG;

public final class Utils {

    private final static String ID = "id";
    private final static String TITLE = "original_title";
    private final static String POSTER = "poster_path";
    private final static String OVERVIEW = "overview";
    private final static String RATING = "vote_average";
    private final static String RELEASE_DATE = "release_date";
    private final static String NAME = "name";
    private final static String KEY = "key";
    private final static String AUTHOR = "author";
    private final static String CONTENT = "content";

    private Utils() {
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movies JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Movie> extractMovieFromJson(String moviesJSON) {
        if (TextUtils.isEmpty(moviesJSON)) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(moviesJSON);
            JSONArray moviesArray = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject currentMovie = moviesArray.getJSONObject(i);
                int id = currentMovie.getInt(ID);
                String title = currentMovie.getString(TITLE);
                String poster = currentMovie.getString(POSTER);
                String overview = currentMovie.getString(OVERVIEW);
                double rating = currentMovie.getDouble(RATING);
                String releaseDate = currentMovie.getString(RELEASE_DATE);

                Movie movie = new Movie(id, title, poster, overview, rating, releaseDate);
                movies.add(movie);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
        return movies;
    }

    public static List<Movie> fetchMovieData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<Movie> movies = extractMovieFromJson(jsonResponse);
        return movies;
    }

    private static List<Trailer> extractTrailerFromJson(String trailersJSON) {
        if (TextUtils.isEmpty(trailersJSON)) {
            return null;
        }

        List<Trailer> trailers = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(trailersJSON);
            JSONArray trailersArray = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < trailersArray.length(); i++) {

                JSONObject currentTrailer = trailersArray.getJSONObject(i);
                String id = currentTrailer.getString(ID);
                String title = currentTrailer.getString(NAME);
                String key = currentTrailer.getString(KEY);

                Trailer trailer = new Trailer(id, title, key);
                trailers.add(trailer);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
        return trailers;
    }

    public static List<Trailer> fetchTrailerData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<Trailer> trailers = extractTrailerFromJson(jsonResponse);
        return trailers;
    }

    private static List<Review> extractReviewFromJson(String reviewsJSON) {
        if (TextUtils.isEmpty(reviewsJSON)) {
            return null;
        }

        List<Review> reviews = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(reviewsJSON);
            JSONArray reviewsArray = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < reviewsArray.length(); i++) {

                JSONObject currentTrailer = reviewsArray.getJSONObject(i);
                String id = currentTrailer.getString(ID);
                String author = currentTrailer.getString(AUTHOR);
                String content = currentTrailer.getString(CONTENT);

                Review review = new Review(id, author, content);
                reviews.add(review);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the movies JSON results", e);
        }
        return reviews;
    }

    public static List<Review> fetchReviewData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        List<Review> reviews = extractReviewFromJson(jsonResponse);
        return reviews;
    }

    public static List<Movie> fetchMovies(Context context) {
        List<Movie> movies = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Entry.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                movies.add(new Movie(
                        cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_TMDB_ID)),
                        cursor.getString(cursor.getColumnIndex(Entry.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(Entry.COLUMN_POSTER)),
                        cursor.getString(cursor.getColumnIndex(Entry.COLUMN_OVERVIEW)),
                        cursor.getDouble(cursor.getColumnIndex(Entry.COLUMN_RATING)),
                        cursor.getString(cursor.getColumnIndex(Entry.COLUMN_RELEASE_DATE)),
                        cursor.getInt(cursor.getColumnIndex(Entry._ID))));
            }
            cursor.close();
        }
        return movies;
    }

    public static int isFavorite(Context context, int id) {
        int dbId = -1;
        ContentResolver resolver = context.getContentResolver();
        String[] projection = {Entry._ID, Entry.COLUMN_TMDB_ID};
        String selection = Entry.COLUMN_TMDB_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = resolver.query(Entry.CONTENT_URI, projection, selection, selectionArgs, null);
        if (cursor == null || cursor.getCount() == 0) {
            return dbId;
        } else {
            cursor.moveToFirst();
            dbId = cursor.getInt(cursor.getColumnIndex(Entry._ID));
            cursor.close();
            return dbId;
        }
    }
}