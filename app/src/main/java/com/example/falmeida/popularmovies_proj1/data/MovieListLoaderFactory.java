package com.example.falmeida.popularmovies_proj1.data;

import android.content.Context;

import com.example.falmeida.popularmovies_proj1.api.MovieListApiPageLoader;
import com.example.falmeida.popularmovies_proj1.api.PopularMoviesApiPageLoader;
import com.example.falmeida.popularmovies_proj1.api.TmdbApiClient;
import com.example.falmeida.popularmovies_proj1.api.TopRatedMoviesApiPageLoader;
import com.example.falmeida.popularmovies_proj1.db.MovieListDbPageLoader;
import com.example.falmeida.popularmovies_proj1.db.PopularMoviesDbPageLoader;
import com.example.falmeida.popularmovies_proj1.db.TmbdDataSource;
import com.example.falmeida.popularmovies_proj1.db.TopRatedMoviesDbPageLoader;

/**
 * Created by falmeida on 28/01/17.
 */

public final class MovieListLoaderFactory {


    public static PagedItemLoader<Movie> getMovieLoader(MovieDataSource source,
                                                        MovieSortCriteria sortCriteria,
                                                        Context context ) {
        switch (source) {
            case API:
                switch (sortCriteria) {
                    case MOST_POPULAR:
                        return new PopularMoviesApiPageLoader( TmdbApiClient.getInstance() );
                    case TOP_RATED:
                        return new TopRatedMoviesApiPageLoader( TmdbApiClient.getInstance() );
                }
            case DB:
                switch (sortCriteria) {
                    case MOST_POPULAR:
                        return new PopularMoviesDbPageLoader( TmbdDataSource.getInstance(context) );
                    case TOP_RATED:
                        return new TopRatedMoviesDbPageLoader( TmbdDataSource.getInstance(context) );
                }
        }

        throw new AssertionError("Unhandled movie data source and sort criteria");
    }
}