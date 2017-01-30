package com.example.falmeida.popularmovies_proj1.db;

/**
 * Created by falmeida on 28/01/17.
 */

public class TopRatedMoviesDbPageLoader extends MovieListDbPageLoader {
    private static final String ORDER_BY = TmbdContract.MovieEntry.COLUMN_NAME_POPULARITY + " DESC";

    public TopRatedMoviesDbPageLoader(TmbdDataSource dataSource) {
        super( dataSource );
    }

    public TopRatedMoviesDbPageLoader(TmbdDataSource dataSource, int maxItemsPerPage) {
        super( dataSource, maxItemsPerPage );
    }

    @Override
    public String getOrderBy() {
        return ORDER_BY;
    }
}
