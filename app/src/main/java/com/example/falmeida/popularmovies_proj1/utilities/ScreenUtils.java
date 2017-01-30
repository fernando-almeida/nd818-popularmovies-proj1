package com.example.falmeida.popularmovies_proj1.utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by falmeida on 18/01/17.
 */

/**
 * Shortcuts to return relevant device information
 */
public final class ScreenUtils {

    public static DisplayMetrics getDisplayMetrics( Context context ) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * Get the screen width in pixels
     * @param context Context
     * @return The width of the screen in pixels
     */
    public static int getScreenWidth( Context context ) {
        return getDisplayMetrics(context).widthPixels;
    }


    /**
     * Get the screen width in dps
     * @param context  Context
     * @return The width of the screen in dp
     */
    public static int getScreenScreenWidthDp(Context context) {
        DisplayMetrics dMetrics = getDisplayMetrics(context);
        return Math.round(dMetrics.widthPixels / dMetrics.density);
    }

    /**
     * Get the screen height in dps
     * @param context
     * @return The width of the screen in dp
     */
    public static int getScreenScreenHeightDp(Context context) {
        DisplayMetrics dMetrics = getDisplayMetrics(context);
        return Math.round(dMetrics.heightPixels / dMetrics.density);
    }

    /**
     * Get the screen height in pixels
     * @param context Application context
     * @return The height of the screen in pixels
     */
    public static int getScreenHeight( Context context ) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * Get the device's current configuration
     * @param context Application context
     * @return The device's current configuration
     */
    public static Configuration getDeviceConfiguration(Context context ) {
        return context.getResources().getConfiguration();
    }

    /**
     * Get the current device orientation
     * @param context Applicaiton context
     * @return A flag for the current device orientation
     */
    public static int getDeviceOrientation( Context context ) {
        return getDeviceConfiguration(context).orientation;
    }
}
