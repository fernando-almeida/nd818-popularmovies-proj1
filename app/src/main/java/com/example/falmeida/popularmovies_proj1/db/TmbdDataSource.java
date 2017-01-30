package com.example.falmeida.popularmovies_proj1.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.falmeida.popularmovies_proj1.api.TmdbApiClient;
import com.example.falmeida.popularmovies_proj1.data.Movie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by falmeida on 17/01/17.
 */

public class TmbdDataSource {
    private static final String LOG_TAG = TmbdDataSource.class.getSimpleName();

    private static TmbdDataSource instance = null;

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    Context context;
    MovieDbHelper dbHelper;

    private TmbdDataSource(Context context){
        this.context = context;
        this.dbHelper = new MovieDbHelper(context);
    }

    public static TmbdDataSource getInstance(Context context) {
        if ( instance == null ) {
            instance = new TmbdDataSource(context);
        }

        return instance;
    }

    public Movie getMovie(long id ) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String table = TmbdContract.MovieEntry.TABLE_NAME;
        final String[] columns = null;
        final String selection = TmbdContract.MovieEntry.COLUMN_NAME_ID + "=?";
        final String[] selectionArgs = { String.valueOf(id) };
        final String groupBy = null;
        final String having = null;
        final String orderBy = null;
        final String limit = null;

        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        if ( cursor.getCount() != 1 ) {
            return null;
        }
        cursor.moveToFirst();
        // Build Movie data
        String originalTitle = cursor.getString( cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE));
        String overview = cursor.getString( cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_OVERVIEW));
        String posterPath = cursor.getString( cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_POSTER_PATH));
        Date releaseDate = null;
        try {
            releaseDate = DATE_FORMATTER.parse(cursor.getString(cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_RELEASE_DATE)));
        } catch ( java.text.ParseException ex ) {
            Log.e( LOG_TAG, ex.getMessage() );
            ex.printStackTrace();
        }
        double voteAverage = cursor.getDouble( cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE));
        double popularity = cursor.getDouble( cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_POPULARITY));

        return new Movie( id, originalTitle, posterPath, overview, releaseDate, voteAverage, popularity );
    }

    /**
     * Get the popular movies in the database
     * @return Cursor with all the popular movies stored in the database
     */
    public Cursor getMovies(String orderBy, Integer max_rows, Integer rows_offset  ) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        final String table = TmbdContract.MovieEntry.TABLE_NAME;
        final String[] columns = null;
        final String selection = null;
        final String[] selectionArgs = null;
        final String groupBy = null;
        final String having = null;
        final String limit = max_rows != null
                            ? ( rows_offset != null
                                ? String.valueOf(rows_offset) + "," + String.valueOf(max_rows)
                                : String.valueOf(max_rows) )
                            : null;

        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        return cursor;
    }

    public long getNumMovies() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        long numRows = DatabaseUtils.longForQuery(db, "SELECT count(*) FROM " + TmbdContract.MovieEntry.TABLE_NAME, null);
        db.close();
        return numRows;
    }


    /**
     * Helper methods
     * Build a movie instance from a cursor
     * @param cursor
     * @return
     */

    public static Movie buildMovieFromCursor( final Cursor cursor ) {
        long id = cursor.getLong(cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_ID));
        String originalTitle = cursor.getString(cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE));
        String posterPath = cursor.getString(cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_POSTER_PATH));
        String overview = cursor.getString(cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_OVERVIEW));

        String strReleaseDate = cursor.getString(cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_RELEASE_DATE));
        Date releaseDate = null;
        try {
            releaseDate = TmdbApiClient.DATE_FORMATTER.parse(strReleaseDate);
        } catch (ParseException ex) {
            Log.e(LOG_TAG, "Could not parse release date " + ex.getMessage());
            return null;
        }
        double voteAverage = cursor.getDouble(cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE));
        double popularity = cursor.getDouble(cursor.getColumnIndex(TmbdContract.MovieEntry.COLUMN_NAME_POPULARITY));

        Movie movie = new Movie(id, originalTitle, posterPath, overview, releaseDate, voteAverage, popularity);
        return movie;
    }
}
