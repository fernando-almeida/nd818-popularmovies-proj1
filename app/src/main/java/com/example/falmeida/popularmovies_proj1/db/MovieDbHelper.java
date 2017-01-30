package com.example.falmeida.popularmovies_proj1.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by falmeida on 17/01/17.
 */

public class MovieDbHelper
        extends SQLiteOpenHelper
{
    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "TheMovieDB.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_POPULAR_MOVIES =
            "CREATE TABLE " + TmbdContract.MovieEntry.TABLE_NAME + " (" +
                    TmbdContract.MovieEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    TmbdContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE + " TEXT," +
                    TmbdContract.MovieEntry.COLUMN_NAME_POSTER_PATH + " TEXT," +
                    TmbdContract.MovieEntry.COLUMN_NAME_OVERVIEW + " TEXT," +
                    TmbdContract.MovieEntry.COLUMN_NAME_RELEASE_DATE + " TEXT," +
                    TmbdContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE + " REAL," +
                    TmbdContract.MovieEntry.COLUMN_NAME_POPULARITY + " REAL," +
                    TmbdContract.MovieEntry.COLUMN_NAME_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    private static final String SQL_DELETE_POPULAR_MOVIES =
            "DROP TABLE IF EXISTS " + TmbdContract.MovieEntry.TABLE_NAME;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(LOG_TAG, "Creating tables \n" + SQL_CREATE_POPULAR_MOVIES );
        db.execSQL(SQL_CREATE_POPULAR_MOVIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_POPULAR_MOVIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    /**
     * Useful methods
     */


}
