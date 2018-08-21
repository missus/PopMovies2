package com.example.android.popmovies;

import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popmovies.adapter.ReviewAdapter;
import com.example.android.popmovies.adapter.TrailerAdapter;
import com.example.android.popmovies.data.EntryContract.Entry;
import com.example.android.popmovies.loader.ReviewLoader;
import com.example.android.popmovies.loader.TrailerLoader;
import com.example.android.popmovies.model.Movie;
import com.example.android.popmovies.model.Review;
import com.example.android.popmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.VERTICAL;
import static com.example.android.popmovies.BuildConfig.API_KEY;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {

    private static final String MAIN_URL = "http://api.themoviedb.org/3/movie";
    private static final int TRAILER_LOADER_ID = 1;
    private static final String TRAILER_URI = "TRAILER_URI";
    private static final String TRAILER_PATH = "videos";
    private static final int REVIEW_LOADER_ID = 2;
    private static final String REVIEW_URI = "REVIEW_URI";
    private static final String REVIEW_PATH = "reviews";

    private LoaderManager mLoaderManager;
    private RecyclerView mTrailerRecyclerView;
    private TrailerAdapter mTrailerAdapter;
    private String mTrailerUri;

    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;
    private String mReviewUri;

    private Movie mMovie;
    private TextView mTrailer;
    private TextView mReview;

    private boolean mIsFav = false;
    private int mId = -1;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        mMovie = (Movie) getIntent().getSerializableExtra(Movie.class.getSimpleName());
        ImageView PosterImage = findViewById(R.id.poster);
        TextView Title = findViewById(R.id.title);
        TextView Rating = findViewById(R.id.rating);
        TextView ReleaseDate = findViewById(R.id.release_date);
        TextView Overview = findViewById(R.id.overview);
        mTrailer = findViewById(R.id.trailer_title);
        mReview = findViewById(R.id.review_title);

        Title.setText(mMovie.getTitle());
        Rating.setText(mMovie.getRating() + "/10");
        ReleaseDate.setText(mMovie.getReleaseDate());
        Overview.setText(mMovie.getOverview());
        Picasso.with(this).load(mMovie.getPosterUrl()).into(PosterImage);
        mId = mMovie.getDbId();
        if (mId != -1) {
            mIsFav = true;
            mUri = Uri.withAppendedPath(Entry.CONTENT_URI, String.valueOf(mId));
        } else {
            mId = Utils.isFavorite(this, mMovie.getId());
            if (mId != -1) {
                mIsFav = true;
                mUri = Uri.withAppendedPath(Entry.CONTENT_URI, String.valueOf(mId));
            }
        }

        List<Trailer> trailers = new ArrayList<>();
        List<Review> reviews = new ArrayList<>();

        mTrailerRecyclerView = findViewById(R.id.trailer_grid);
        mTrailerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mTrailerAdapter = new TrailerAdapter(this, trailers,
                new TrailerAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String key = mTrailerAdapter.getItem(position).getKey();
                        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.youtube.com/watch?v=" + key));
                        try {
                            startActivity(appIntent);
                        } catch (ActivityNotFoundException ex) {
                            startActivity(webIntent);
                        }
                    }
                }
        );
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerRecyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        mTrailerRecyclerView.addItemDecoration(itemDecor);

        mReviewRecyclerView = findViewById(R.id.review_grid);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mReviewAdapter = new ReviewAdapter(this, reviews);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        mReviewRecyclerView.setHasFixedSize(false);
        mReviewRecyclerView.setNestedScrollingEnabled(false);
        mReviewRecyclerView.addItemDecoration(itemDecor);

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            if (savedInstanceState != null && !savedInstanceState.getString(TRAILER_URI).isEmpty() && !savedInstanceState.getString(REVIEW_URI).isEmpty()) {
                mTrailerUri = savedInstanceState.getString(TRAILER_URI);
                mLoaderManager = getLoaderManager();
                mLoaderManager.initLoader(TRAILER_LOADER_ID, savedInstanceState, DetailsActivity.this);
                mReviewUri = savedInstanceState.getString(REVIEW_URI);
                mLoaderManager.initLoader(REVIEW_LOADER_ID, savedInstanceState, DetailsActivity.this);
            } else {
                search();
            }
        }
    }

    private void search() {
        Uri baseUri = Uri.parse(MAIN_URL);
        Uri.Builder trailerUriBuilder = baseUri.buildUpon();
        trailerUriBuilder.appendPath(String.valueOf(mMovie.getId()));
        trailerUriBuilder.appendPath(TRAILER_PATH);
        trailerUriBuilder.appendQueryParameter("api_key", API_KEY);
        mTrailerUri = trailerUriBuilder.toString();
        Bundle search = new Bundle();
        search.putString(TRAILER_URI, mTrailerUri);
        mLoaderManager = getLoaderManager();
        mLoaderManager.restartLoader(TRAILER_LOADER_ID, search, this);

        Uri.Builder reviewUriBuilder = baseUri.buildUpon();
        reviewUriBuilder.appendPath(String.valueOf(mMovie.getId()));
        reviewUriBuilder.appendPath(REVIEW_PATH);
        reviewUriBuilder.appendQueryParameter("api_key", API_KEY);
        mReviewUri = reviewUriBuilder.toString();
        search.putString(REVIEW_URI, mReviewUri);
        mLoaderManager.restartLoader(REVIEW_LOADER_ID, search, this);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(TRAILER_URI, mTrailerUri);
        state.putString(REVIEW_URI, mReviewUri);
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        if (i == TRAILER_LOADER_ID) {
            return new TrailerLoader(this, bundle.getString(TRAILER_URI));
        } else if (i == REVIEW_LOADER_ID) {
            return new ReviewLoader(this, bundle.getString(REVIEW_URI));
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (loader.getId() == TRAILER_LOADER_ID) {
            mTrailerAdapter.clear();
            List<Trailer> trailers = (List<Trailer>) data;
            if (trailers != null && !trailers.isEmpty()) {
                mTrailerAdapter = new TrailerAdapter(DetailsActivity.this, trailers,
                        new TrailerAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                String key = mTrailerAdapter.getItem(position).getKey();
                                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("http://www.youtube.com/watch?v=" + key));
                                try {
                                    startActivity(appIntent);
                                } catch (ActivityNotFoundException ex) {
                                    startActivity(webIntent);
                                }
                            }
                        }
                );
                mTrailerRecyclerView.setAdapter(mTrailerAdapter);
            } else {
                mTrailerRecyclerView.setVisibility(View.GONE);
                mTrailer.setVisibility(View.GONE);
            }
        } else if (loader.getId() == REVIEW_LOADER_ID) {
            mReviewAdapter.clear();
            List<Review> reviews = (List<Review>) data;
            if (reviews != null && !reviews.isEmpty()) {
                mReviewAdapter = new ReviewAdapter(DetailsActivity.this, reviews);
                mReviewRecyclerView.setAdapter(mReviewAdapter);
            } else {
                mReviewRecyclerView.setVisibility(View.GONE);
                mReview.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == TRAILER_LOADER_ID) {
            mTrailerAdapter.clear();
        } else if (loader.getId() == REVIEW_LOADER_ID) {
            mReviewAdapter.clear();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details, menu);
        MenuItem addItem = menu.findItem(R.id.action_fav_add);
        addItem.setVisible(!mIsFav);
        MenuItem removeItem = menu.findItem(R.id.action_fav_remove);
        removeItem.setVisible(mIsFav);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_fav_add) {
            addToFav();
            mIsFav = true;
            invalidateOptionsMenu();
        } else if (id == R.id.action_fav_remove) {
            removeFromFav();
            mIsFav = false;
            invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addToFav() {
        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_TMDB_ID, mMovie.getId());
        values.put(Entry.COLUMN_TITLE, mMovie.getTitle());
        values.put(Entry.COLUMN_POSTER, mMovie.getPoster());
        values.put(Entry.COLUMN_OVERVIEW, mMovie.getOverview());
        values.put(Entry.COLUMN_RATING, mMovie.getRating());
        values.put(Entry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());

        mUri = getContentResolver().insert(Entry.CONTENT_URI, values);
        if (mUri == null) {
            Toast.makeText(this, getString(R.string.fav_add_failed), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.fav_add_successful), Toast.LENGTH_SHORT).show();
            mId = (int) ContentUris.parseId(mUri);
        }
    }


    private void removeFromFav() {
        if (mUri != null) {
            int rowsDeleted = getContentResolver().delete(mUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.fav_remove_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.fav_remove_successful), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
