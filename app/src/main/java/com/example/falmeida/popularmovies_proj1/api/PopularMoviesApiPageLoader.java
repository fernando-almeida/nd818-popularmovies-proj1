package com.example.falmeida.popularmovies_proj1.api;

import android.util.Log;

import com.example.falmeida.popularmovies_proj1.data.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by falmeida on 28/01/17.
 */

public class PopularMoviesApiPageLoader extends MovieListApiPageLoader {
    private static final String LOG_TAG = PopularMoviesApiPageLoader.class.getSimpleName();

    public PopularMoviesApiPageLoader(TmdbApiClient client ) {
        super(client);
    }

    @Override
    protected JSONObject getMoviesList(int page) {
        JSONObject jsonResponse = mClient.getMoviesPopular(page);
        return jsonResponse;
    }

}
