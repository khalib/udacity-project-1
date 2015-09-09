package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;

import java.util.Locale;
import java.util.TreeMap;

/**
 * A {@link ListPreference} that displays a list of locale entries as a dialog.
 */
public class LocaleListPreference extends ListPreference {

    private final String LOG_TAG = LocaleListPreference.class.getSimpleName();

    private TreeMap<String, Locale> mLocales;

    public LocaleListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocaleListPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateDialogView() {
        super.onCreateDialogView();

        Pair<String[], String[]> entries = getListEntries();

        // Add entry/values for the country preference.
        ListView view = new ListView(getContext());
        setEntries(entries.first);
        setEntryValues(entries.second);

        // Set the default value from the user preferences.
        setValue(Utility.getCountryCodeSettings(getContext()));

        return view;
    }

    /**
     * Gets a generated Pair of local entries and values.
     *
     * @return a Pair where the first attribute is a CharSequence of locale entries, and the second
     *         attribute is a CharSequence of locale values.
     */
    private Pair getListEntries() {
        mLocales = Utility.getLocales();

        String entries[] = new String[mLocales.size()];
        String values[] = new String[mLocales.size()];

        int i = 0;
        for (String key : mLocales.keySet()) {
            Locale locale = mLocales.get(key);
            entries[i] = locale.getDisplayCountry();
            values[i] = locale.getCountry();
            i++;
        }

        return new Pair(entries, values);
    }

}
