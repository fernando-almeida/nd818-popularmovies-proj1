package com.example.falmeida.popularmovies_proj1.api;

import android.net.Uri;
import android.util.Log;

import com.example.falmeida.popularmovies_proj1.BuildConfig;
import com.example.falmeida.popularmovies_proj1.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;


/**
 * Created by falmeida on 16/01/17.
 */

public class TmdbApiClient {
    private static final String LOG_TAG = TmdbApiClient.class.getSimpleName();

    private static TmdbApiClient instance;

    private static final String PHONE_IMAGE_SIZE = "w185";
    public static final String NO_POSTER_IMAGE_URL = "https://www.themoviedb.org/assets/static_cache/e2dd052f141e33392eb749aab2414ec0/images/no-poster-w300_and_h450_bestv2-v2.png";


    private static final String API_BASE_URL = "https://api.themoviedb.org";
    private static final int VERSION = 3;

    private static final String API_KEY_QUERY = "api_key";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;


    private static final String DEFAULT_LANGUAGE = "en-US";

    // Movies now playing
    private static final String MOVIES_NOW_PLAYING_PATH = "movie/now_playing";
    private static final String MOVIES_NOW_PLAYING_QUERY_PARAM_PAGE = "page";
    private static final String MOVIES_NOW_PLAYING_QUERY_PARAM_LANGUAGE = "language";
    private static final String MOVIES_NOW_PLAYING_QUERY_REGION_QUERY = "region";

    // Top rated movies
    private static final String MOVIES_TOP_RATED_PATH = "movie/top_rated";

    // Popular movies
    private static final String MOVIES_POPULAR_PATH = "movie/popular";

    // Movie details
    private static final String MOVIE_DETAILS_PATH_PARAM_MOVIE_ID = "{movie_id}";
    private static final String MOVIE_DETAILS_PATH = "movie/" + MOVIE_DETAILS_PATH_PARAM_MOVIE_ID;

    // Configuration
    private static final String CONFIGURATION_PATH = "configuration";
    private static final String CONFIGURATION_IMAGES_KEY = "images";
    private static final String CONFIGURATION_IMAGES_BASE_URL_KEY = "base_url";
    private static final String CONFIGURATION_IMAGES_SECURE_BASE_URL_KEY = "secure_base_url";
    private static final String CONFIGURATION_IMAGES_BACKDROP_SIZES_KEY = "backdrop_sizes";
    private static final String CONFIGURATION_IMAGES_LOGO_SIZES_KEY = "logo_sizes";
    private static final String CONFIGURATION_IMAGES_POSTER_SIZES_KEY = "poster_sizes";
    private static final String CONFIGURATION_IMAGES_PROFILE_SIZES_KEY = "profile_sizes";
    private static final String CONFIGURATION_IMAGES_STILL_SIZES_KEY = "still_sizes";
    private static final String CONFIGURATION_CHANGE_KEYS_KEY = "change_keys";

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);

    // Configuration
    public class Configuration {
        String imageBaseUrl;
        String imageSecureBaseUrl;
        String[] backdropSizes;;
        String[] logoSizes;
        String[] posterSizes;
        String[] profileSizes;
        String[] stillSizes;
        String[] changeKeys;
    }
    private Configuration configuration = new Configuration();

    private TmdbApiClient() {
        loadConfiguration();
    }


    public static TmdbApiClient getInstance() {
        if ( instance == null ) {
            instance = new TmdbApiClient();
        }
        return instance;
    }


    private void loadConfiguration() {
        Uri configurationUri = buildConfigurationUri();
        try {
            URL configurationUrl = new URL(configurationUri.toString());
            String responseText = NetworkUtils.getResponseFromHttpUrl( configurationUrl );

            JSONObject responseJson = new JSONObject( responseText );
            if ( responseJson.has(CONFIGURATION_IMAGES_KEY) ) {
                JSONObject imagesConfigJson = responseJson.getJSONObject(CONFIGURATION_IMAGES_KEY);
                if ( imagesConfigJson.has(CONFIGURATION_IMAGES_BASE_URL_KEY)) {
                    configuration.imageBaseUrl = imagesConfigJson.getString(CONFIGURATION_IMAGES_BASE_URL_KEY);
                }
                if ( imagesConfigJson.has(CONFIGURATION_IMAGES_SECURE_BASE_URL_KEY)) {
                    configuration.imageSecureBaseUrl = imagesConfigJson.getString(CONFIGURATION_IMAGES_SECURE_BASE_URL_KEY);
                }
                if ( imagesConfigJson.has(CONFIGURATION_IMAGES_BACKDROP_SIZES_KEY)) {
                    JSONArray backdropSizes = imagesConfigJson.getJSONArray(CONFIGURATION_IMAGES_BACKDROP_SIZES_KEY);
                    configuration.backdropSizes = new String[backdropSizes.length()];
                    for ( int i = 0; i < backdropSizes.length(); i++) {
                        configuration.backdropSizes[i] = backdropSizes.getString(i);
                    }
                }
                if ( imagesConfigJson.has(CONFIGURATION_IMAGES_LOGO_SIZES_KEY)) {
                    JSONArray logoSizes = imagesConfigJson.getJSONArray(CONFIGURATION_IMAGES_LOGO_SIZES_KEY);
                    configuration.logoSizes = new String[logoSizes.length()];
                    for ( int i = 0; i < logoSizes.length(); i++) {
                        configuration.logoSizes[i] = logoSizes.getString(i);
                    }
                }
                if ( imagesConfigJson.has(CONFIGURATION_IMAGES_POSTER_SIZES_KEY)) {
                    JSONArray posterSizes = imagesConfigJson.getJSONArray(CONFIGURATION_IMAGES_POSTER_SIZES_KEY);
                    configuration.posterSizes = new String[posterSizes.length()];
                    for ( int i = 0; i < posterSizes.length(); i++) {
                        configuration.posterSizes[i] = posterSizes.getString(i);
                    }
                }
                if ( imagesConfigJson.has(CONFIGURATION_IMAGES_PROFILE_SIZES_KEY)) {
                    JSONArray profileSizes = imagesConfigJson.getJSONArray(CONFIGURATION_IMAGES_PROFILE_SIZES_KEY);
                    configuration.profileSizes = new String[profileSizes.length()];
                    for ( int i = 0; i < profileSizes.length(); i++) {
                        configuration.profileSizes[i] = profileSizes.getString(i);
                    }
                }
                if ( imagesConfigJson.has(CONFIGURATION_IMAGES_STILL_SIZES_KEY)) {
                    JSONArray stillSizes = imagesConfigJson.getJSONArray(CONFIGURATION_IMAGES_STILL_SIZES_KEY);
                    configuration.stillSizes = new String[stillSizes.length()];
                    for ( int i = 0; i < stillSizes.length(); i++) {
                        configuration.stillSizes[i] = stillSizes.getString(i);
                    }
                }
                if ( imagesConfigJson.has(CONFIGURATION_IMAGES_STILL_SIZES_KEY)) {
                    JSONArray stillSizes = imagesConfigJson.getJSONArray(CONFIGURATION_IMAGES_STILL_SIZES_KEY);
                    configuration.stillSizes = new String[stillSizes.length()];
                    for ( int i = 0; i < stillSizes.length(); i++) {
                        configuration.stillSizes[i] = stillSizes.getString(i);
                    }
                }
            }
            if ( responseJson.has(CONFIGURATION_CHANGE_KEYS_KEY)) {
                JSONArray changeKeys = responseJson.getJSONArray(CONFIGURATION_CHANGE_KEYS_KEY);
                configuration.changeKeys = new String[changeKeys.length()];
                for ( int i = 0; i < changeKeys.length(); i++) {
                    configuration.changeKeys[i] = changeKeys.getString(i);
                }
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage() );
        }
    }

    private static Uri.Builder getApiBaseUriBuilder() {
        return Uri.parse(API_BASE_URL).buildUpon()
                .appendEncodedPath(String.valueOf(VERSION))
                .appendQueryParameter(API_KEY_QUERY, API_KEY );

    }

    private static Uri buildConfigurationUri() {
        Uri url = getApiBaseUriBuilder()
                .appendEncodedPath(CONFIGURATION_PATH)
                .build();
        return url;
    }

    private static Uri buildMoviesNowPlayingUri( ) {
        Uri url = getApiBaseUriBuilder()
                .appendEncodedPath(MOVIES_NOW_PLAYING_PATH)
                .build();
        return url;
    }

    private static Uri buildMoviesTopRatedUri( ) {
        Uri url = getApiBaseUriBuilder()
                .appendEncodedPath(MOVIES_TOP_RATED_PATH)
                .build();
        return url;
    }

    private static Uri buildMoviesPopularUri( ) {
        Uri url = getApiBaseUriBuilder()
                .appendEncodedPath(MOVIES_POPULAR_PATH)
                .build();
        return url;
    }


    private static Uri buildMoviesNowPlayingUri(int page ) {
        Uri url = buildMoviesNowPlayingUri().buildUpon()
                .appendQueryParameter(MOVIES_NOW_PLAYING_QUERY_PARAM_PAGE, String.valueOf(page))
                .build();
        return url;
    }

    private static Uri buildMoviesPopularUri(int page ) {
        Uri url = buildMoviesPopularUri().buildUpon()
                .appendQueryParameter(MOVIES_NOW_PLAYING_QUERY_PARAM_PAGE, String.valueOf(page))
                .build();
        return url;
    }

    private static Uri buildMoviesTopRatedUri(int page ) {
        Uri url = buildMoviesTopRatedUri().buildUpon()
                .appendQueryParameter(MOVIES_NOW_PLAYING_QUERY_PARAM_PAGE, String.valueOf(page))
                .build();
        return url;
    }

    private static Uri buildMovieDetailsUri( long movie_id ) {
        Uri uri = getApiBaseUriBuilder()
                .appendEncodedPath(MOVIE_DETAILS_PATH.replace(MOVIE_DETAILS_PATH_PARAM_MOVIE_ID, String.valueOf(movie_id) ))
                .build();
        return uri;
    }

    public JSONObject getMovieDetails( long movie_id ) {
        try {
            Uri requestUri = buildMovieDetailsUri(movie_id);
            URL requestUrl = new URL( requestUri.toString());
            Log.d(LOG_TAG, "getMoviesNowPlaying " + requestUrl.toString() );
            String textResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            JSONObject jsonResponse = new JSONObject(textResponse);

            return jsonResponse;
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return null;
    }

    public JSONObject getMoviesNowPlaying( int page ) {
        try {
            Uri requestUri = buildMoviesNowPlayingUri(page);
            URL requestUrl = new URL( requestUri.toString());
            Log.d(LOG_TAG, "getMoviesNowPlaying " + requestUrl.toString() );
            String textResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            JSONObject jsonResponse = new JSONObject(textResponse);

            return jsonResponse;
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return null;
    }

    public JSONObject getMoviesTopRated( int page ) {
        try {
            Uri requestUri = buildMoviesTopRatedUri(page);
            URL requestUrl = new URL( requestUri.toString());
            Log.d(LOG_TAG, "getMoviesTopRated " + requestUrl.toString() );
            String textResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            JSONObject jsonResponse = new JSONObject(textResponse);

            return jsonResponse;
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return null;
    }

    public JSONObject getMoviesPopular( int page ) {
        try {
            Uri requestUri = buildMoviesPopularUri(page);
            URL requestUrl = new URL( requestUri.toString());
            Log.d(LOG_TAG, "getMoviesPopular " + requestUrl.toString() );
            String textResponse = NetworkUtils.getResponseFromHttpUrl(requestUrl);
            if ( textResponse == null ) {
                return null;
            }
            JSONObject jsonResponse = new JSONObject(textResponse);

            return jsonResponse;
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return null;
    }

    /**
     * Build the absolute URI to fetch an image from a relative path
     * @param relativePath The relative path to the image
     * @return Returns the absolute URI to the image
     */
    public Uri buildImageUrl( String relativePath ) {
        return Uri.parse(configuration.imageBaseUrl).buildUpon()
                .appendEncodedPath(PHONE_IMAGE_SIZE)
                .appendEncodedPath(relativePath)
                .build();
    }

    public Uri buildImageUrl( String relativePath, String width ) {
        return Uri.parse(configuration.imageBaseUrl).buildUpon()
                .appendEncodedPath(width)
                .appendEncodedPath(relativePath)
                .build();
    }

    private Uri buildImageUrl( String[] allowes_sizes, String relativePath, int width ) throws InvalidImageSizeException {
        String strWidth = "w" + width;
        if ( ! Arrays.asList(allowes_sizes).contains(strWidth) ){
            throw new InvalidImageSizeException();
        }

        return buildImageUrl(relativePath, strWidth );
    }

    public Uri buildLogoImageUrl( String relativePath, int width ) throws InvalidImageSizeException {
        return buildImageUrl(configuration.logoSizes, relativePath, width );
    }

    public Uri buildPosterImageUrl( String relativePath, int width ) throws InvalidImageSizeException {
        return buildImageUrl(configuration.posterSizes, relativePath, width );
    }

    public Uri buildStillImageUrl( String relativePath, int width ) throws InvalidImageSizeException {
        return buildImageUrl(configuration.stillSizes, relativePath, width );
    }
    public Uri buildBackdropImageUrl( String relativePath, int width ) throws InvalidImageSizeException {
        return buildImageUrl(configuration.backdropSizes, relativePath, width );

    }
    public Uri buildProfileImageUrl( String relativePath, int width ) throws InvalidImageSizeException {
        return buildImageUrl(configuration.profileSizes, relativePath, width );
    }

    private String getImageMaxWidth( String[] sizes, int maxWidth ) {

        int bestMaxWidth = -1;
        for (int i = 0; i < sizes.length; i++) {
            String strWidth = sizes[i];
            if ( strWidth.charAt(0) != 'w') {
                // TODO Ignoring original size
                continue;
            }
            int width = Integer.parseInt(sizes[i].substring(1));
            if ( width < maxWidth && bestMaxWidth < maxWidth)
                bestMaxWidth = width;
        }

        String strBestMaxWidth = bestMaxWidth != -1 ? "w" + bestMaxWidth : null;
        return strBestMaxWidth;
    }

    public String getLogoImageMaxWidth( int maxWidth ) {
        return getImageMaxWidth( configuration.logoSizes, maxWidth );
    }
    public String getPosterImageMaxWidth( int maxWidth ) {
        return getImageMaxWidth( configuration.posterSizes, maxWidth );
    }
    public String getStillImageMaxWidth( int maxWidth ) {
        return getImageMaxWidth( configuration.stillSizes, maxWidth );
    }
    public String getBackdropImageMaxWidth( int maxWidth ) {
        return getImageMaxWidth( configuration.backdropSizes, maxWidth );
    }
    public String getProfileImageMaxWidth( int maxWidth ) {
        return getImageMaxWidth( configuration.profileSizes, maxWidth );
    }

    static class InvalidImageSizeException extends Exception {};

}
