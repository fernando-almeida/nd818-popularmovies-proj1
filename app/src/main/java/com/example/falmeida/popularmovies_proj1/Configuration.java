package com.example.falmeida.popularmovies_proj1;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by falmeida on 18/01/17.
 */

public final class Configuration {
    private static final String LOG_TAG = Configuration.class.getSimpleName();

    private static final String CONFIG_FILENAME = "config.json";
    private static Configuration instance;


    private static final int SECONDS_IN_MINUTE = 60;

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
    private static final String LAST_SYNC_DATE_KEY = "lastSync";

    private JSONObject config;
    private Context context;

    public static Configuration getInstance( Context context ) {
        if ( instance == null ) {
            instance = new Configuration( context );
        }
        return instance;
    }

    private Configuration( Context context ) {
        config = new JSONObject();
        this.context = context;
        loadConfig();
    }

    private String getConfigFilePath() {
        return context.getFilesDir() + File.separator + CONFIG_FILENAME;
    }

    private boolean loadConfig() {
        StringBuilder text = new StringBuilder();
        File file = new File( getConfigFilePath() );
        try {
            file.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();

            String strConfig = text.toString();
            this.config = new JSONObject( strConfig );
            return true;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return false;
    }


    public Date getLastSync( ) {
        assert( config != null );

        if ( !config.has(LAST_SYNC_DATE_KEY)) {
            return null;
        }

        try {
            Date date = DATE_FORMATTER.parse(config.getString(LAST_SYNC_DATE_KEY));
            return date;
        } catch (JSONException ex ) {
            Log.e(LOG_TAG, ex.getMessage());
        } catch (ParseException ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }

        return null;
    }

    public boolean setLastSync( Date date ) {
        Log.i(LOG_TAG, "Set last sync to " + date.toString() );
        try {
            config.put(LAST_SYNC_DATE_KEY, DATE_FORMATTER.format(date));
            return save();
        } catch ( JSONException ex ) {
            Log.e(LOG_TAG, ex.getMessage());
        }
        return false;
    }

    public boolean updateLastSync( ) {
        return setLastSync( Calendar.getInstance().getTime() );
    }

    private boolean save() {
        File file = new File( getConfigFilePath() );
        try {
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));

            bw.write( config.toString() );

            bw.close();

            return true;
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return false;
    }


    /**
     * Helper methods
     */
    public long getMinutesSinceLastSync() {
        Date lastSync = getLastSync();
        if ( lastSync == null ) {
            return Long.MAX_VALUE;
        }

        Calendar c = Calendar.getInstance();
        Date now = c.getTime();
        long secondsSinceLastSync = now.getTime() - lastSync.getTime();
        long minutesSinceLastSync = secondsSinceLastSync / SECONDS_IN_MINUTE;
        return minutesSinceLastSync;
    }

}
