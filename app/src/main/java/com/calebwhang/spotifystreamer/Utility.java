package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.calebwhang.spotifystreamer.service.MediaPlayerService;

import java.util.Locale;
import java.util.TreeMap;

/**
 * Utility helper methods.
 */
public class Utility {

    private final static String LOG_TAG = Utility.class.getSimpleName();

    /**
     * Gets the preference settings for country code which defaults to the device's locale.
     *
     * @param context
     * @return
     */
    public static String getCountryCodeSettings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.preference_country_code_key);

        return preferences.getString(key, Locale.getDefault().getCountry());
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
        Log.v(LOG_TAG, "===== displayCurrentTrackButton()");

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

    /**
     * Get a Map of available locales with the redundancy removed and sorted by country name.
     *
     * @return
     */
    public static TreeMap getLocales() {
        Locale[] locales = Locale.getAvailableLocales();
        TreeMap<String, Locale> countries = new TreeMap<>();

        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.containsKey(country)) {
                countries.put(locale.getDisplayCountry(), locale);
            }
        }

        return countries;
    }

}
