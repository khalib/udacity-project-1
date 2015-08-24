package com.calebwhang.spotifystreamer;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import com.calebwhang.spotifystreamer.service.MediaPlayerService;


public class SearchArtistActivity extends ActionBarActivity implements
        SearchArtistFragment.Callback, TopTracksFragment.Callback {

    private final String LOG_TAG = SearchArtistActivity.class.getSimpleName();

    private static final String TOP_TRACKS_ACTIVITY_FRAGMENT_TAG = "top_tracks_activity_fragment";
    private final String INSTANCE_STATE_IS_MEDIA_SERVICE_BOUND_KEY = "instance_state_is_media_service_bound";

    private MediaPlayerService mMediaPlayerService;
    private boolean mIsMediaServiceBound = false;
    private boolean mTwoPane;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_artist);

        if (savedInstanceState == null) {
            // Bind to MediaPlayerService.
            Intent intent = new Intent(this, MediaPlayerService.class);
            bindService(intent, mMediaPlayerConnection, Context.BIND_AUTO_CREATE);
        } else {
            mIsMediaServiceBound = savedInstanceState.getBoolean(INSTANCE_STATE_IS_MEDIA_SERVICE_BOUND_KEY);
        }

        // Check and set if a larger screen is being used.
        if (findViewById(R.id.top_tracks_detail_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save states.
        outState.putBoolean(INSTANCE_STATE_IS_MEDIA_SERVICE_BOUND_KEY, mIsMediaServiceBound);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onArtistSelected(ArtistParcelable artistParcelable) {
        if (mTwoPane) {
            // Show the detail view in this activity by adding or replacing the detail fragment.
            Bundle args = new Bundle();
            args.putParcelable(TopTracksFragment.ARTIST_PARCELABLE_KEY, artistParcelable);

            TopTracksFragment topTracksFragment = new TopTracksFragment();
            topTracksFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_detail_container, topTracksFragment, TOP_TRACKS_ACTIVITY_FRAGMENT_TAG)
                    .commit();
        } else {
            // Pass the artist ID and name.
            Intent intent = new Intent(this, TopTracksActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, artistParcelable);

            startActivity(intent);
        }
    }

    @Override
    public void onTrackSelected(ArrayList<TrackParcelable> tracks, int position) {
        Log.v(LOG_TAG, "===== onTrackSelected()");

        // Play and display the track info and player controls.
        mMediaPlayerService.playAndDisplayTrack(tracks, position, getSupportFragmentManager(), mTwoPane);
    }

    /**
     * Manage MediaPlayerService connection.
     */
    private ServiceConnection mMediaPlayerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaPlayerService.MediaPlayerServiceBinder binder = (MediaPlayerService.MediaPlayerServiceBinder) service;
            mMediaPlayerService = binder.getService();
            mIsMediaServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsMediaServiceBound = false;
        }
    };

}
