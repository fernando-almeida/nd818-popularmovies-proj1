package com.example.falmeida.popularmovies_proj1.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.falmeida.popularmovies_proj1.api.TmdbApiClient;
import com.example.falmeida.popularmovies_proj1.db.TmbdContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by falmeida on 16/01/17.
 */

/**
 * @author falmeida
 * Information about a movie
 */
public class Movie implements Parcelable {
    private static final String LOG_TAG = Movie.class.getSimpleName();

    private final long id;
    private final String originalTitle;
    private final String posterPath;
    private final String overview; //  (called overview in the api)
    private final double voteAverage; // (called vote_average in the api)
    private final Date releaseDate;
    private final double popularity;

    public Movie(long id,
                 String originalTitle,
                 String posterPath,
                 String overview,
                 Date releaseDate,
                 double voteAverage,
                 double popularity ) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.popularity = popularity;
    }

    /**
     * Get the movie internal identifier
     * @return
     */
    public long getId() {
        return id;
    }

    /**
     * Get the movie release date
     * @return
     */
    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * Get the movie original title
     * @return
     */
    public String getOriginalTitle() {
        return originalTitle;
    }

    /**
     * Get the movie synopsis
     * @return
     */
    public String getPlotSynopsis() {
        return overview;
    }

    /**
     * Get the path to image poster
     * @return
     */
    public String getPosterPath() {
        return posterPath;
    }

    public double getUserRating() {
        return voteAverage;
    }

    public double getPopularity() {
        return popularity;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof  Movie ) ) {
            return false;
        }
        return ((Movie) obj).id == id;
    }

    @Override
    public String toString() {
        return "Id=\"" + id + "\"," +
                "OriginalTitle=\"" + originalTitle + "\"," +
                "PosterPath=\"" + posterPath + "\"," +
                "voteAverage=\"" + voteAverage + "\"," +
                "popularity=\"" + popularity + "\"," +
                "releaseDate=\"" + releaseDate + "\"," +
                "Overview=\"" + overview + "\"";
    }

    public static Map<String, Comparator<Movie> > comparators = new HashMap<>();

    static{
        comparators.put( "top_rated", new TopRatedComparator() );
        comparators.put( "most_popular", new PopularityComparator() );
    }

    public static class TopRatedComparator implements Comparator<Movie> {

        @Override
        public int compare(Movie movie, Movie t1) {

            return  Double.compare( t1.getUserRating(), movie.getUserRating() );
        }
    }

    public static class PopularityComparator implements Comparator<Movie> {

        @Override
        public int compare(Movie movie, Movie t1) {

            return  Double.compare( t1.getPopularity(), movie.getPopularity() );
        }
    }


    public static Movie fromJSON(JSONObject movieJson ) {
        Movie newMovie = null;
        try {
            // TODO Should improve validation of fields
            long id = movieJson.getLong("id");
            String originalTitle = movieJson.has("original_title")
                    ? movieJson.getString("original_title").trim()
                    : null;
            String overview = movieJson.has("overview")
                    ? movieJson.getString("overview").trim()
                    : null;

            String posterPath = movieJson.has("poster_path") && !movieJson.isNull("poster_path") && !movieJson.getString("poster_path").isEmpty()
                                ? movieJson.getString("poster_path")
                                : null;

            Date releaseDate = movieJson.has("release_date")
                    ? TmdbApiClient.DATE_FORMATTER.parse(movieJson.getString("release_date"))
                    : null;

            double voteAverage = movieJson.has("vote_average")
                    ? movieJson.getDouble("vote_average")
                    : null;
            double popularity = movieJson.has("popularity")
                    ? movieJson.getDouble("popularity")
                    : null;

            newMovie = new Movie(id,
                                originalTitle,
                                posterPath,
                                overview,
                                releaseDate,
                                voteAverage,
                                popularity);
        } catch (JSONException ex ) {
            Log.e(LOG_TAG, ex.getMessage());
        } catch (java.text.ParseException ex ) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return newMovie;
    }


    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(TmbdContract.MovieEntry.COLUMN_NAME_ID, id );
        cv.put(TmbdContract.MovieEntry.COLUMN_NAME_ORIGINAL_TITLE, originalTitle );
        cv.put(TmbdContract.MovieEntry.COLUMN_NAME_OVERVIEW, overview );
        cv.put(TmbdContract.MovieEntry.COLUMN_NAME_POSTER_PATH, posterPath );
        cv.put(TmbdContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, TmdbApiClient.DATE_FORMATTER.format(releaseDate) );
        cv.put(TmbdContract.MovieEntry.COLUMN_NAME_POPULARITY, popularity );
        cv.put(TmbdContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE, voteAverage );

        return cv;
    }

    /**
     * Parcelable
     */

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            long id = in.readLong();
            String originalTitle = in.readString();
            String posterPath = in.readString();
            String overview = in.readString();
            Date releaseDate = null;
            try {
                releaseDate = TmdbApiClient.DATE_FORMATTER.parse(in.readString());
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
            double voteAverage = in.readDouble();
            double popularity = in.readDouble();
            Movie newMovie = new Movie(id,
                                        originalTitle,
                                        posterPath,
                                        overview,
                                        releaseDate,
                                        voteAverage,
                                        popularity);
            return newMovie;
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(originalTitle);
        dest.writeString(posterPath);
        dest.writeString(overview);

        dest.writeString( TmdbApiClient.DATE_FORMATTER.format(releaseDate) );
        dest.writeDouble(voteAverage);
        dest.writeDouble(popularity);
    }

}

