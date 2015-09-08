package com.calebwhang.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.calebwhang.spotifystreamer.service.MediaPlayerService;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceConnection;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceListenerInterface;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceManager;

import java.util.ArrayList;


/**
 * A {@link ActionBarActivity} that presents the Artist Top Tracks.
 */
public class TopTracksActivity extends ActionBarActivity implements TopTracksFragment.Callback,
        MediaPlayerServiceListenerInterface {

    private final String LOG_TAG = TopTracksActivity.class.getSimpleName();

    private MediaPlayerService mMediaPlayerService;
    private MediaPlayerServiceManager mMediaPlayerServiceManager;
    private boolean mTwoPane = false;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_top_tracks);

        // Bind the media player service.
        mMediaPlayerServiceManager = ((SpotifyStreamerApplication) this.getApplicationContext()).getServiceManager();
        mMediaPlayerServiceManager.bindService(new MediaPlayerServiceConnection(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, mMenu);

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
                mMediaPlayerService.displayMediaPlayer(getSupportFragmentManager(), mTwoPane);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTrackSelected(ArrayList<TrackParcelable> tracks, int position) {
        Log.v(LOG_TAG, "===== onTrackSelected()");

        // Play and display the track info and player controls.
        mMediaPlayerService.playAndDisplayTrack(tracks, position, getSupportFragmentManager(), false);
    }

    @Override
    public void onServiceConnected(MediaPlayerService mediaPlayerService) {
        Log.v(LOG_TAG, "===== onServiceConnected()");

        mMediaPlayerService = mediaPlayerService;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display the "Now Playing" button if a track is playing.
        Utility.displayCurrentTrackButton(mMediaPlayerService, mMenu);
    }

}
