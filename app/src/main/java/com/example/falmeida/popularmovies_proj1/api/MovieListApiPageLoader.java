package com.example.falmeida.popularmovies_proj1.api;

import android.util.Log;
import android.util.Pair;

import com.example.falmeida.popularmovies_proj1.data.PagedItemLoader;
import com.example.falmeida.popularmovies_proj1.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by falmeida on 28/01/17.
 */

public abstract class MovieListApiPageLoader implements PagedItemLoader<Movie> {
    private static final String LOG_TAG = MovieListApiPageLoader.class.getSimpleName();
    private static final int FIRST_PAGE = 1;
    private static final int MAX_RESULTS_PER_PAGE = 20;

    protected int mTotalPages;
    protected int mTotalResults;
    protected TmdbApiClient mClient;


    public MovieListApiPageLoader(TmdbApiClient client ) {
        this.mClient = client;
        mTotalPages = 0;
        mTotalResults = 0;
    }

    protected TmdbApiClient getApiClient() {
        return this.mClient;
    }

    protected abstract JSONObject getMoviesList(int page);

    private int calculatePage(int resultPosition ) {
        // Calculate the number of pages to request
        int page = (int) ( resultPosition / MAX_RESULTS_PER_PAGE + 1 );
        return page;
    }

    /**
     * Calculate the range of pages where the results range is present
     * @param fromResult
     * @param toResult
     * @return The page range where the results are mapped
     */
    private Pair<Integer, Integer> calculatePageRange(int fromResult, int toResult ) {
        // Calculate the number of pages to request
        Integer from_page= calculatePage( fromResult );
        Integer to_page = calculatePage( toResult );
        return new Pair<>(from_page, to_page);
    }

    public boolean isPageValid( int page ) {
        return page >= FIRST_PAGE && page <= mTotalPages;
    }

    @Override
    public int firstPage() {
        return FIRST_PAGE;
    }

    @Override
    public int lastPage() {
        return mTotalPages;
    }

    @Override
    public int maxItemsPerPage() {
        return MAX_RESULTS_PER_PAGE;
    }

    @Override
    public int totalPages() {
        return mTotalPages;
    }

    @Override
    public int totalResults() {
        return mTotalResults;
    }

    @Override
    public List<Movie> getItems(int page) {
        Log.d(LOG_TAG, "Processing page " + page);

        List<Movie> newMovies = new ArrayList<>();

        JSONObject jsonResponse = getMoviesList(page);
        if (jsonResponse == null) {
            Log.e(LOG_TAG, "No JSON response");
            return null;
        }
        if (!jsonResponse.has("results")) {
            Log.e(LOG_TAG, "No results found in JSON response");
            return null;
        }

        try {

            JSONArray results = jsonResponse.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject movieJson = results.getJSONObject(i);
                Movie newMovie = Movie.fromJSON(movieJson);
                if (newMovie == null) {
                    Log.d(LOG_TAG, "Could not parse movie from JSON");
                    continue;
                }

                newMovies.add(newMovie);
            }

            // Update total pages
            mTotalPages = jsonResponse.getInt("total_pages");

            // Update total results
            mTotalResults = jsonResponse.getInt("total_results");
        } catch (JSONException ex ) {
            ex.printStackTrace();
        }

        return newMovies;
    }

}