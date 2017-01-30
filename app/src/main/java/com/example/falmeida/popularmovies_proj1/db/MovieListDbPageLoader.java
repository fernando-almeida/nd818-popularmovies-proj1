package com.example.falmeida.popularmovies_proj1.db;

import android.database.Cursor;

import com.example.falmeida.popularmovies_proj1.data.PagedItemLoader;
import com.example.falmeida.popularmovies_proj1.data.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by falmeida on 28/01/17.
 */

public abstract class MovieListDbPageLoader implements PagedItemLoader<Movie> {
    private static final String LOG_TAG = MovieListDbPageLoader.class.getSimpleName();

    private static final int FIRST_PAGE = 1;
    private static final int MAX_MOVIES_PER_PAGE = 20;

    protected TmbdDataSource mDataSource;
    protected int mTotalResults;
    protected int mTotalPages;
    protected int mMaxItemsPerPage;

    public MovieListDbPageLoader(TmbdDataSource dataSource) {
        mDataSource = dataSource;
        mMaxItemsPerPage = MAX_MOVIES_PER_PAGE;
    }

    public MovieListDbPageLoader(TmbdDataSource dataSource, int maxMoviesPerPage ) {
        mDataSource = dataSource;
        mMaxItemsPerPage = maxMoviesPerPage;
    }

    private void calculateTotals() {
        mTotalResults = (int) mDataSource.getNumMovies();
        mTotalPages = (int) Math.ceil(mTotalResults / mMaxItemsPerPage * 1.0);
    }

    /**
     * Get the sort criteria for the movies
     * @return
     */
    protected abstract String getOrderBy();

    /**
     * Calculate the offset required to retrieve records for the required page
     * @param page The page requested
     * @return The offset to retrieve results from the page
     */
    protected int calculatePageOffset( int page ) {
        assert( page > 0 );
        return (page - 1) * mMaxItemsPerPage;
    }

    /**
     * PageItemLoader interface implementation
     */

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
        return mMaxItemsPerPage;
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
        calculateTotals();

        int pageOffset = calculatePageOffset(page);
        Cursor cursor = mDataSource.getMovies(getOrderBy(), mMaxItemsPerPage, pageOffset );

        // Populate list from cursor
        List<Movie> movies = new ArrayList<>();
        while( cursor.moveToNext() ) {
            Movie movie = TmbdDataSource.buildMovieFromCursor( cursor );
            movies.add( movie );
        }
        return movies;
    }


}