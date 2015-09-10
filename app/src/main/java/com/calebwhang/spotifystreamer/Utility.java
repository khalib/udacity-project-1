package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
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
     * @param context the context of the preferences whose values are wanted.
     * @return the country code set in the user preferences.
     */
    public static String getCountryCodeSettings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.preference_country_code_key);

        return preferences.getString(key, Locale.getDefault().getCountry());
    }

    /**
     * Gets the preference settings for the drawer notifications.
     *
     * @param context the context of the preferences whose values are wanted.
     * @return whether notifications are set in the user preferences.
     */
    public static boolean getPlayerNotificationSettings(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String key = context.getString(R.string.preference_enable_player_notifications_key);
        boolean defaultValue = Boolean.parseBoolean(context.getString(R.string.preference_enable_player_notifications_default));

        return preferences.getBoolean(key, defaultValue);
    }

    /**
     * Helper method that displays the "Now Playing" button in the action bar if there is a track
     * and playlist that has been selected.
     *
     * @param mediaPlayerService the bound instance of the media player service.
     * @param menu the menu that is to be modified.
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

    /**
     * Get a Map of available locales with the redundancy removed and sorted by country name.
     *
     * @return a sorted map of the country locales.
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

    /**
     * Checks to see if the device has a connection source.
     *
     * @return whether or not the device is connected.
     */
    public static boolean hasConnectivity() {
        Context context = SpotifyStreamerApplication.getStaticApplicationContext();

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info != null) {
            int netType = info.getType();
            int netSubtype = info.getSubtype();

            if (netType == ConnectivityManager.TYPE_WIFI) {
                return info.isConnected();
            } else if (netType == ConnectivityManager.TYPE_MOBILE && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS) {
                return info.isConnected();
            }
        }

        return false;
    }

}
