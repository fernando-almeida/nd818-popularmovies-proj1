package com.example.falmeida.popularmovies_proj1.db;

import android.provider.BaseColumns;

/**
 * Created by falmeida on 17/01/17.
 */

public class TmbdContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TmbdContract() {}


    /* Inner class that defines the contents of the Movie table */
    public static class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_NAME_POSTER_PATH = "poster_path";
        public static final String COLUMN_NAME_OVERVIEW = "overview";
        public static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        public static final String COLUMN_NAME_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_NAME_POPULARITY = "popularity";
        public static final String COLUMN_NAME_TIMESTAMP = "_timestamp";
    }
}


