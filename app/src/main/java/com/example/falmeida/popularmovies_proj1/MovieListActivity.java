package com.example.falmeida.popularmovies_proj1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import com.example.falmeida.popularmovies_proj1.data.Movie;
import com.example.falmeida.popularmovies_proj1.api.TmdbApiClient;
import com.example.falmeida.popularmovies_proj1.data.PagedItemLoader;
import com.example.falmeida.popularmovies_proj1.db.TmbdDataSource;
import com.example.falmeida.popularmovies_proj1.ui.RecyclerViewVisiblePositionsObserver;
import com.example.falmeida.popularmovies_proj1.utilities.AppUtils;
import com.example.falmeida.popularmovies_proj1.utilities.NetworkUtils;
import com.example.falmeida.popularmovies_proj1.utilities.ScreenUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity
        extends AppCompatActivity {

    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    /**
     * Loading indicator
     */
    @BindView(R.id.pb_indicator)
    protected ProgressBar pbMovieList;

    @BindView(R.id.pb_indicator_after)
    protected ProgressBar pbMovieListAfter;

    /**
     * Progress message indicator
     */
    @BindView(R.id.tv_progress_message)
    protected TextView tvProgressMessage;


    @BindView(R.id.sb_page)
    protected SeekBar sbPage;


    /**
     * Movies recycler view
     */
    @BindView(R.id.movie_list)
    protected RecyclerView rvMovies;


    /**
     * Reference to the current movie loader instance
     */
    PagedItemLoader<Movie> mMovieLoader;

    /**
     * Listen to changes in preferences
     */
    SharedPreferences.OnSharedPreferenceChangeListener mPreferencesChangedListener;


    /**
     * Listener for clicks on View in RecyclerView that contains movie info
     * Assumes that the view's tag object stores a reference to the movie data source
     * This object is reused for all clicks
     */
    private View.OnClickListener movieViewClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final Movie movie = (Movie) v.getTag();


            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putLong(MovieDetailFragment.ARG_ITEM_ID, movie.getId());
                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment)
                        .commit();
            } else {
                Context context = v.getContext();
                Intent intent = new Intent(context, MovieDetailActivity.class);

                Bundle bundle = new Bundle();
                bundle.putParcelable(MovieDetailFragment.ARG_ITEM, movie);
                bundle.putLong(MovieDetailFragment.ARG_ITEM_ID, movie.getId() );

                intent.putExtra(MovieDetailFragment.ARG_ITEM_BUNDLE, bundle);
                context.startActivity(intent);
            }
        }
    };


    /**
     * Page load listener
     */
    RecyclerViewPageLoader.PageLoadListener pageLoadListener = new RecyclerViewPageLoader.PageLoadListener() {
        @Override
        public void onPreviousPageLoadStart() {
            pbMovieList.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPreviousPageLoadEnd() {
            pbMovieList.setVisibility(View.GONE);

        }

        @Override
        public void onNextPageLoadStart() {
            pbMovieList.setVisibility(View.VISIBLE);
        }

        @Override
        public void onNextPageLoadEnd() {
            pbMovieList.setVisibility(View.GONE);
        }


    };

    /**
     * Broadcast Receiver
     */
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Network state changed
            if ( intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION )) {
                loadMovies();
            }
        }
    };

    RecyclerViewPageLoader mMovieListLoadManager;

    RecyclerViewVisiblePositionsObserver visiblePositionsObserver;

    /**
     * Data variables
     */
    private MovieListRecyclerViewAdapter mMoviesAdapter;
    private TmbdDataSource mDataSource =  TmbdDataSource.getInstance(this);
    TmdbApiClient tmbdApiClient;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangedListener;

    /**
     * Cache-Storage data structure for movies
     */
    private List<Movie> mItems = new ArrayList<>();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle( getTitle() );
        toolbar.setSubtitle( AppUtils.getSortCriteriaTitle(this) );
        setSupportActionBar(toolbar);

        // Preferences change listener
        mPreferencesChangedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                Log.v(LOG_TAG, "Shared preferences changed " + s);
                if ( s == getString(R.string.pref_movie_sort_criteria_key )) {
                    new AsyncTask<Void, Void, PagedItemLoader<Movie> >() {
                        @Override
                        protected PagedItemLoader<Movie>  doInBackground(Void... params) {
                            PagedItemLoader<Movie> movieLoader = AppUtils.getMovieLoader(MovieListActivity.this);
                            return movieLoader;
                        }

                        @Override
                        protected void onPostExecute(PagedItemLoader<Movie> moviePagedItemLoader) {
                            mMovieLoader = moviePagedItemLoader;
                            mMovieListLoadManager.setItemLoader(mMovieLoader);
                            mMovieListLoadManager.reset();
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    mMovieListLoadManager.loadNextPage();

                                    return null;
                                }
                            }.execute();
                        }
                    }.execute();
                } else if ( s == getString(R.string.pref_online_key )) {

                }
            }
        };
        // Register listener
        PreferenceManager.getDefaultSharedPreferences( this ).registerOnSharedPreferenceChangeListener(mPreferencesChangedListener);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        setupHttpCache(this);

        setupRecyclerView(rvMovies);

        setupBroadcastReceiver();

        loadMovies();
    }

    private void loadMovies() {
        if ( !NetworkUtils.isNetworkAvailable(this )) {
            tvProgressMessage.setText( getString(R.string.network_not_available) );
            tvProgressMessage.setVisibility(View.VISIBLE);
        } else {
            // Hide status message
            tvProgressMessage.setVisibility(View.GONE);
        }

        if ( mMovieListLoadManager != null ) {
            return;
            /*visiblePositionsObserver.removeOnVisibleItemsChangeListener(mMovieListLoadManager);
            mMovieListLoadManager.unregisterPageLoadListener(pageLoadListener); */
        }

        // Initialize moviesLoader
        new AsyncTask<Void, Void, PagedItemLoader<Movie> >() {
            @Override
            protected PagedItemLoader<Movie> doInBackground(Void... voids) {
                PagedItemLoader<Movie> movieLoader = AppUtils.getMovieLoader(MovieListActivity.this);
                return movieLoader;
            }

            @Override
            protected void onPostExecute(PagedItemLoader<Movie> moviePagedItemLoader) {
                mMovieLoader = moviePagedItemLoader;
                mMovieListLoadManager =  new RecyclerViewPageLoader<>(mItems, mMovieLoader, mMoviesAdapter );
                mMovieListLoadManager.registerPageLoadListener( pageLoadListener );
                visiblePositionsObserver.addOnVisibleItemsChangeListener(mMovieListLoadManager);

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        mMovieListLoadManager.loadNextPage();
                        return null;
                    }
                }.execute();
            }
        }.execute();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG_TAG, "ON_DESTROY");
        super.onDestroy();
        destroyBroadcastReceiver();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        destroyHttpCache(this);
    }

    private void setupBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION );
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void destroyBroadcastReceiver() {
        unregisterReceiver(mBroadcastReceiver);
    }

    private void setupHttpCache(Context context) {
        try {
            File httpCacheDir = new File(context.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(LOG_TAG, "HTTP response cache installation failed:" + e);
        }
    }

    private void destroyHttpCache(Context context) {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    private Intent createSettingsIntent() {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra( PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                         SettingsActivity.GeneralPreferenceFragment.class.getName() );
        intent.putExtra( PreferenceActivity.EXTRA_NO_HEADERS, true );
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem settingsMenu = menu.findItem(R.id.menu_settings);


        settingsMenu.setIntent( createSettingsIntent() );

        return super.onCreateOptionsMenu(menu);
    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MovieListRecyclerViewAdapter( this, mItems );
        recyclerView.setAdapter(mMoviesAdapter);

        GridLayoutManager layoutMgr = new GridLayoutManager(this, getMaxColumns() );
        recyclerView.setLayoutManager(layoutMgr);


        visiblePositionsObserver = new RecyclerViewVisiblePositionsObserver( );
        recyclerView.addOnScrollListener( visiblePositionsObserver );

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        GridLayoutManager gridLayoutManager = (GridLayoutManager) rvMovies.getLayoutManager();
        gridLayoutManager.setSpanCount( getMaxColumns() );
    }

    /**
     * Get the maximum number of columns that can be rendered on the screen
     * @return
     */
    private int getMaxColumns() {
        final int POSTER_IMAGE_WIDTH_PX = 185;
        int ncolumns = ( ScreenUtils.getScreenWidth(this) / POSTER_IMAGE_WIDTH_PX);
        return ncolumns;
    }

    /**
     *
     * @return
     */
    private int getMaxVisibleMovies() {
        int screenWidth = ScreenUtils.getScreenWidth(this);
        int screenHeight = ScreenUtils.getScreenHeight(this);
        boolean orientationPortrait = ScreenUtils.getDeviceOrientation(this) == Configuration.ORIENTATION_PORTRAIT;
        final int LIST_MOVIE_WIDTH = 185;
        final int LIST_MOVIE_HEIGHT = 185;

        int maxMovies = orientationPortrait
                ? screenWidth / LIST_MOVIE_WIDTH * screenHeight * LIST_MOVIE_HEIGHT
                : screenWidth / LIST_MOVIE_HEIGHT * screenHeight / LIST_MOVIE_WIDTH;
        return maxMovies;
    }


    /**
     * Recycler view classes
     */

    /**
     * View holder for a movie in a list
     */
    public class MovieViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_poster_image)
        protected ImageView mPosterImage;

        @BindView(R.id.tv_user_rating)
        protected TextView mUserRating;

        @BindView(R.id.tv_popularity)
        protected TextView mPopularity;

        protected final View mView;
        protected Movie mMovie;

        protected final Context mContext;

        public MovieViewHolder(View view, Context context) {
            super(view);
            mView = view;
            mContext = context;
            ButterKnife.bind(this, mView);
        }

        private String getPosterImageUrl(final Movie movie) {
            String posterPath = movie.getPosterPath();
            if ( posterPath == null || posterPath.isEmpty() )
                return TmdbApiClient.NO_POSTER_IMAGE_URL;
            return TmdbApiClient.getInstance().buildImageUrl( movie.getPosterPath(),
                    getString(R.string.movie_list_poster_size) ).toString();
        }

        public void bindData( final Movie movie,
                              final View.OnClickListener clickListener ) {
            mMovie = movie;

            String posterImageUrl = getPosterImageUrl(movie);
            Picasso.with(mContext).load(posterImageUrl).into(mPosterImage);

            String userRating = String.format( "%.2f", movie.getUserRating());
            mUserRating.setText(  userRating );

            String popularity = String.format( "%.2f", movie.getPopularity());
            mPopularity.setText( popularity );

            mView.setOnClickListener( clickListener );
            mView.setTag(movie);
        }

        public Movie getMovie() {
            return mMovie;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + (mMovie == null ? "NULL" : mMovie.toString()) + "'";
        }
    }

    /**
     * Movie Recycler View Adapter
     */
    public class MovieListRecyclerViewAdapter
            extends RecyclerView.Adapter<MovieViewHolder> {
        private final String LOG_TAG = MovieListRecyclerViewAdapter.class.getSimpleName();

        protected List<Movie> mMoviesList;
        protected Context mContext;
        // private Comparator< ? super Movie> mMovieComparator;

        public MovieListRecyclerViewAdapter( Context context, List<Movie> movies ) {
            setHasStableIds(true);
            this.mContext = context;
            mMoviesList = movies;
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_content, parent, false);
            return new MovieViewHolder(view, mContext);
        }

        @Override
        public void onBindViewHolder(final MovieViewHolder holder, final int position) {
            Movie movie = mMoviesList.get( position );
            holder.bindData( movie, movieViewClickListener);
        }

        @Override
        public long getItemId(int position) {
            return mMoviesList.get(position).getId();
        }

        @Override
        public int getItemCount() {
            return mMoviesList.size();
        }

    }

}
