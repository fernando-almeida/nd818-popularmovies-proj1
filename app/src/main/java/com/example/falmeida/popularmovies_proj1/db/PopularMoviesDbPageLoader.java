package com.example.falmeida.popularmovies_proj1.db;

import com.example.falmeida.popularmovies_proj1.data.Movie;

import java.util.List;

/**
 * Created by falmeida on 28/01/17.
 */

public class PopularMoviesDbPageLoader extends MovieListDbPageLoader {
    private static final String ORDER_BY = TmbdContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE + " DESC";

    public PopularMoviesDbPageLoader(TmbdDataSource dataSource) {
        super( dataSource );
    }

    public PopularMoviesDbPageLoader(TmbdDataSource dataSource, int maxItemsPerPage) {
        super( dataSource, maxItemsPerPage );
    }

    @Override
    public String getOrderBy() {
        return ORDER_BY;
    }
}
