package com.calebwhang.spotifystreamer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;


/**
 * A {@link ActionBarActivity} that presents the media player controls and track information.
 */
public class MediaPlayerActivity extends ActionBarActivity {

    private final String LOG_TAG = MediaPlayerActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_media_player);
    }

}
