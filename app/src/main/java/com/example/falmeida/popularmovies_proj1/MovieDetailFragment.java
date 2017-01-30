package com.example.falmeida.popularmovies_proj1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.falmeida.popularmovies_proj1.api.TmdbApiClient;
import com.example.falmeida.popularmovies_proj1.data.Movie;
import com.example.falmeida.popularmovies_proj1.db.TmbdDataSource;
import com.example.falmeida.popularmovies_proj1.picasso.MultiFallBackTarget;
import com.example.falmeida.popularmovies_proj1.utilities.AppUtils;
import com.example.falmeida.popularmovies_proj1.utilities.ScreenUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM = "item";
    public static final String ARG_ITEM_BUNDLE = "item_bundle";

    /**
     * The dummy content this fragment is presenting.
     */
    private Movie movie;

    @BindView(R.id.tv_movie_id)
    TextView tvId;

    @BindView(R.id.tv_movie_original_title)
    TextView tvOriginalTitle;

    @BindView(R.id.tv_movie_overview)
    TextView tvOverview;

    @BindView(R.id.iv_poster_image)
    ImageView ivPosterImage;

    @BindView(R.id.tv_movie_release_date)
    TextView tvReleaseDate;

    @BindView(R.id.tv_movie_popularity)
    TextView tvPopularity;

    @BindView(R.id.tv_movie_vote_average)
    TextView tvVoteAverage;

    /**
     * Status controls
     */
    @BindView(R.id.pb_indicator)
    ProgressBar pbProgressIndicator;

    @BindView(R.id.tv_progress_message)
    TextView tvProgressMessage;

    /**
     * Data variables
     */
    FetchMovieDetailsAsyncTask mFetchMovieDetails;


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        /**
         * Handle local broadcasts for system and application relevant events
         * @param context Context in which the change occured
         * @param intent Intent that conveys the change
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            // Network state changed
            if ( intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION )) {

                // Change adapters used to provide data for the application
                if ( AppUtils.useLocalData( context ) ) {
                    if ( mFetchMovieDetails instanceof FetchMovieDetailsFromApiAsyncTask ) {
                        mFetchMovieDetails = new FetchMovieDetailsFromDbAsyncTask();
                    }
                } else {
                    if ( mFetchMovieDetails instanceof FetchMovieDetailsFromDbAsyncTask ) {
                        mFetchMovieDetails = new FetchMovieDetailsFromApiAsyncTask();
                    }
                }
            }
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "ONCREATE");
        super.onCreate(savedInstanceState);

        if ( !getArguments().containsKey(ARG_ITEM_BUNDLE)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            throw new AssertionError("Movie ID is not within provided arguments");
        }

        Bundle bundle = getArguments().getBundle(ARG_ITEM_BUNDLE);
        if ( bundle.containsKey(ARG_ITEM) ) {
            movie = (Movie) bundle.getParcelable(ARG_ITEM);
        }
        // Initialize the async task that will be used to fetch the data
        mFetchMovieDetails = AppUtils.useLocalData(getContext())
                            ? new FetchMovieDetailsFromDbAsyncTask()
                            : new FetchMovieDetailsFromApiAsyncTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        ButterKnife.bind(this, rootView);

        // Get movie ID data and get details
        long movieId = getArguments().getLong(ARG_ITEM_ID);
        if ( movie == null) {
            mFetchMovieDetails.execute(movieId);
        } else {
            loadMovie(movie, getLargePosterImageUrl(movie));
        }

        return rootView;
    }


    private void loadMovie( final Movie movie, final String posterImageUrl ) {
        if ( movie == null ) {
            Log.e(LOG_TAG, "No movie to load on loadMovie");
            return;
        }

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("About");

        String fallbackPosterUrl = getFallbackPosterImageUrl(movie);
        MultiFallBackTarget posterTargetFallback = new MultiFallBackTarget(ivPosterImage, fallbackPosterUrl);
        Picasso.with(getContext()).load(posterImageUrl).into( posterTargetFallback );
        tvId.setText( String.valueOf(movie.getId()) );
        tvOriginalTitle.setText( movie.getOriginalTitle() );
        tvOverview.setText( movie.getPlotSynopsis() );
        tvReleaseDate.setText(TmdbApiClient.DATE_FORMATTER.format( movie.getReleaseDate() ) );
        tvPopularity.setText( String.format("%.2f", movie.getPopularity() ));
        tvVoteAverage.setText( String.format("%.2f", movie.getUserRating() ) );

    }

    protected int getPosterImageMaxWidth( ) {
        int maxImageWidth = ScreenUtils.getScreenWidth( getContext() ) / 3;
        return maxImageWidth;
    }

    protected String getLargestPosterImageSize( int max_width ) {
        String posterImageSize = TmdbApiClient.getInstance().getPosterImageMaxWidth( getPosterImageMaxWidth() );
        return posterImageSize;
    }

    protected String getLargePosterImageUrl(final Movie movie) {
        String posterPath = movie.getPosterPath();
        if ( posterPath == null || posterPath.isEmpty() )
            return TmdbApiClient.NO_POSTER_IMAGE_URL;


        String posterImageSize = getLargestPosterImageSize( getPosterImageMaxWidth() );
        return TmdbApiClient.getInstance().buildImageUrl( movie.getPosterPath(), posterImageSize ).toString();
    }

    protected String getFallbackPosterImageUrl(final Movie movie) {
        return TmdbApiClient.getInstance().buildImageUrl( movie.getPosterPath(), getContext().getString(R.string.movie_list_poster_size) ).toString();
    }

    /**
     * Async tasks
     */
    abstract class FetchMovieDetailsAsyncTask extends AsyncTask< Long, Void, Pair< Movie, String > > {

        protected String posterImageUrl;


        @Override
        protected void onPreExecute() {
            // Show progress bar
            pbProgressIndicator.setProgress(0);
            pbProgressIndicator.setVisibility(View.VISIBLE);

            // Show progress message
            tvProgressMessage.setText( getString( R.string.loading_movie_details));
            tvProgressMessage.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Pair< Movie, String > movie) {
            // Show progress bar
            pbProgressIndicator.setProgress(pbProgressIndicator.getMax());
            pbProgressIndicator.setVisibility(View.GONE);

            // Show progress message
            tvProgressMessage.setVisibility(View.GONE);

            // Show movie data
            if ( movie == null ) {
                Log.e(LOG_TAG, "Could not retrieve movie information");
                return;
            }

            loadMovie(movie.first, movie.second);
        }
    }

    class FetchMovieDetailsFromApiAsyncTask extends FetchMovieDetailsAsyncTask {

        @Override
        protected Pair< Movie, String > doInBackground(Long... movieIds) {
            assert( movieIds.length == 1 );

            long movieId = movieIds[0];
            TmdbApiClient apiClient = TmdbApiClient.getInstance();

            JSONObject jsonResponse = apiClient.getMovieDetails(movieIds[0]);

            Movie movie = jsonResponse != null ? Movie.fromJSON( jsonResponse ) : null;
            String posterImageUrl = null;
            if ( movie != null ) {
                posterImageUrl = getLargePosterImageUrl(movie);
                Picasso.with(getContext()).load(posterImageUrl);
            }

            return new Pair( movie, posterImageUrl );
        }

    }

    class FetchMovieDetailsFromDbAsyncTask extends FetchMovieDetailsAsyncTask {


        @Override
        protected Pair< Movie, String > doInBackground(Long... movieIds) {
            assert( movieIds.length == -1 );

            long movieId = movieIds[0];
            TmbdDataSource dataSource = TmbdDataSource.getInstance( MovieDetailFragment.this.getContext() );

            Movie movie = dataSource.getMovie( movieId );

            String posterImageUrl = null;
            if ( movie != null) {
                posterImageUrl = getLargePosterImageUrl(movie);
                // Preload image with Picasso
                Picasso.with(getContext()).load(posterImageUrl);
            }


            return new Pair<>(movie, posterImageUrl);
        }

    }
}
