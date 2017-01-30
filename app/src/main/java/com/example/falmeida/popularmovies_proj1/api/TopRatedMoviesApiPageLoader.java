package com.example.falmeida.popularmovies_proj1.api;

import org.json.JSONObject;

/**
 * Created by falmeida on 28/01/17.
 */

public class TopRatedMoviesApiPageLoader extends MovieListApiPageLoader {
    private static final String LOG_TAG = PopularMoviesApiPageLoader.class.getSimpleName();

    public TopRatedMoviesApiPageLoader(TmdbApiClient client ) {
        super(client);
    }

    @Override
    protected JSONObject getMoviesList(int page) {
        JSONObject jsonResponse = mClient.getMoviesTopRated(page);
        return jsonResponse;
    }


}
