package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.calebwhang.spotifystreamer.service.MediaPlayerService;

/**
 * Utility helper methods.
 */
public class Utility {

    /**
     * Gets the preference settings for country code.
     *
     * @param context
     * @return
     */
    public static String getCountryCodeSettings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.preference_country_code_key);
        String defaultValue = context.getString(R.string.preference_country_code_default);

        return preferences.getString(key, defaultValue);
    }

    /**
     * Gets the preference settings for the drawer notifications.
     *
     * @param context
     * @return
     */
    public static boolean getDrawerNotificationSettings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.preference_enable_drawer_notifications_key);
        boolean defaultValue = Boolean.parseBoolean(context.getString(R.string.preference_enable_drawer_notifications_default));

        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * Gets the preference settings for the lock screen notifications.
     *
     * @param context
     * @return
     */
    public static boolean getLockScreenNotificationSettings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.preference_enable_drawer_notifications_key);
        boolean defaultValue = Boolean.parseBoolean(context.getString(R.string.preference_enable_drawer_notifications_default));

        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * Helper method that displays the "Now Playing" button in the action bar if there is a track
     * and playlist that has been selected.
     *
     * @param mediaPlayerService
     * @param menu
     */
    public static void displayCurrentTrackButton(MediaPlayerService mediaPlayerService, Menu menu) {
        // Display the "Now Playing" button if a track is playing.
        if (mediaPlayerService != null && menu != null) {
            MenuItem menuItem = menu.findItem(R.id.action_player);

            if (mediaPlayerService.getCurrentTrack() != null) {
                menuItem.setVisible(true);
            } else {
                menuItem.setVisible(false);
            }
        }
    }

}
