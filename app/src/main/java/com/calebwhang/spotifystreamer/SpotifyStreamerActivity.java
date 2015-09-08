package com.calebwhang.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.calebwhang.spotifystreamer.service.MediaPlayerService;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceConnection;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceListenerInterface;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceManager;

/**
 * Base {@link android.app.Activity} for the Spotify Streamer application.
 */
public class SpotifyStreamerActivity extends ActionBarActivity implements
        MediaPlayerServiceListenerInterface {

    private final String LOG_TAG = SpotifyStreamerActivity.class.getSimpleName();

    protected MediaPlayerServiceManager mMediaPlayerServiceManager;
    protected MediaPlayerService mMediaPlayerService;
    protected Menu mMenu;
    protected boolean mIsLargeLayout = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the device is using the large screen layout.
        if (findViewById(R.id.top_tracks_detail_container) != null) {
            mIsLargeLayout = true;
        }

        // Bind the media player service.
        mMediaPlayerServiceManager = ((SpotifyStreamerApplication) this.getApplicationContext()).getServiceManager();
        mMediaPlayerServiceManager.bindService(new MediaPlayerServiceConnection(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(LOG_TAG, "===== onCreateOptionsMenu()");

        super.onCreateOptionsMenu(menu);

        mMenu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, mMenu);

        // Display the "Now Playing" button if a track is playing.
        Utility.displayCurrentTrackButton(mMediaPlayerService, mMenu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Back up to where search was left off.
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;

            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_player:
                MediaPlayerService.displayMediaPlayer(getSupportFragmentManager(), mIsLargeLayout);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display the "Now Playing" button if a track is playing.
        Utility.displayCurrentTrackButton(mMediaPlayerService, mMenu);
    }

    @Override
    public void onServiceConnected(MediaPlayerService mediaPlayerService) {
        Log.v(LOG_TAG, "===== onServiceConnected()");

        mMediaPlayerService = mediaPlayerService;
    }
}
