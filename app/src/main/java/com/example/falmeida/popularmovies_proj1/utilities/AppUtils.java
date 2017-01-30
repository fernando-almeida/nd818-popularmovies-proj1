package com.example.falmeida.popularmovies_proj1.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.falmeida.popularmovies_proj1.R;
import com.example.falmeida.popularmovies_proj1.data.Movie;
import com.example.falmeida.popularmovies_proj1.data.MovieDataSource;
import com.example.falmeida.popularmovies_proj1.data.MovieListLoaderFactory;
import com.example.falmeida.popularmovies_proj1.data.MovieSortCriteria;
import com.example.falmeida.popularmovies_proj1.data.PagedItemLoader;

/**
 * Created by falmeida on 24/01/17.
 */

public class AppUtils {

    public static boolean useOnline(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getBoolean( context.getResources().getString(R.string.pref_online_key),
                                 context.getResources().getBoolean( R.bool.pref_online_default_value ));
    }

    public static boolean useLocalData(Context context) {
        return !useOnline(context) || !NetworkUtils.isNetworkAvailable(context);
    }


    public static class OutOfRangeException extends Exception {
        private long index;
        public OutOfRangeException(long index) {
            this.index = index;
        }

        public long getIndex() {
            return index;
        }
    }

    public static long convertPosition( long oldPosition, long oldMin, long oldMax, long newMin, long newMax ) throws OutOfRangeException {
        long oldRange = oldMax - oldMin;

        long newRange = newMax - newMin;

        // If ranges don't match we will get an invalid results

        long newPosition = ((oldPosition - oldMin) * newRange) / oldRange + newMin;

        if ( oldRange != newRange ) {
            throw new OutOfRangeException(newPosition);
        }

        return newPosition;
    }


    /**
     * Get sort criteria preference
     * @param context
     * @return
     */
    public static String getSortCriteriaValue(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString( context.getString(R.string.pref_movie_sort_criteria_key),
                                context.getString(R.string.pref_movie_sort_default_value));
    }

    public static MovieDataSource getDataSource(Context context) {
        return useLocalData(context)
                ?  MovieDataSource.DB
                : MovieDataSource.API;
    }

    public static MovieSortCriteria getSortCriteria(Context context) {
        String sortCriteria = getSortCriteriaValue(context);
        if ( sortCriteria.equals(context.getString(R.string.pref_movie_sort_top_rated_value))) {
            return MovieSortCriteria.TOP_RATED;
        } else if ( sortCriteria.equals(context.getString(R.string.pref_movie_sort_most_popular_value))) {
            return MovieSortCriteria.MOST_POPULAR;
        }
        throw new AssertionError("Unhandled sort criteria preference \"" + sortCriteria + "\"");
    }

    public static String getSortCriteriaTitle(Context context) {
        MovieSortCriteria sortCriteria = getSortCriteria(context);
        switch ( sortCriteria) {
            case TOP_RATED:
                return context.getString(R.string.pref_movie_sort_top_rated_title);
            case MOST_POPULAR:
                return context.getString(R.string.pref_movie_sort_most_popular_title);
        }

        throw new AssertionError("Unhandled sort criteria preference \"" + sortCriteria + "\"");
    }

    public static PagedItemLoader<Movie> getMovieLoader(Context context ) {
        return MovieListLoaderFactory.getMovieLoader( MovieDataSource.API,
                                                      AppUtils.getSortCriteria( context ),
                                                      context);
    }

}
