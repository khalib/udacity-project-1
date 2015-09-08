package com.calebwhang.spotifystreamer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import com.calebwhang.spotifystreamer.service.MediaPlayerService;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceManager;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceConnection;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceListenerInterface;


/**
 * A {@link ActionBarActivity} that presents the Artist search results.
 */
public class SearchArtistActivity extends ActionBarActivity implements
        SearchArtistFragment.Callback, TopTracksFragment.Callback,
        MediaPlayerServiceListenerInterface {

    private final String LOG_TAG = SearchArtistActivity.class.getSimpleName();

    private static final String TOP_TRACKS_ACTIVITY_FRAGMENT_TAG = "top_tracks_activity_fragment";
    private final String INSTANCE_STATE_IS_MEDIA_SERVICE_BOUND_KEY = "instance_state_is_media_service_bound";

    private MediaPlayerService mMediaPlayerService;
    private MediaPlayerServiceManager mMediaPlayerServiceManager;
    private boolean mTwoPane;
    private Menu mMenu;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_artist);

        // Check and set if a larger screen is being used.
        if (findViewById(R.id.top_tracks_detail_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        // Bind the media player service.
        mMediaPlayerServiceManager = ((SpotifyStreamerApplication) this.getApplicationContext()).getServiceManager();
        mMediaPlayerServiceManager.bindService(new MediaPlayerServiceConnection(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, mMenu);

        // Display the "Now Playing" button if a track is playing.
        Utility.displayCurrentTrackButton(mMediaPlayerService, mMenu);

        return super.onCreateOptionsMenu(mMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;

            case R.id.action_player:
                mMediaPlayerService.displayMediaPlayer(getSupportFragmentManager(), mTwoPane);
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
    protected void onSaveInstanceState(Bundle outState) {
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
            Intent intent = new Intent(this, TopTracksActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, artistParcelable);
            startActivity(intent);
        }
    }

    @Override
    public void onTrackSelected(ArrayList<TrackParcelable> tracks, int position) {
        // Play and display the track info and player controls.
        mMediaPlayerService.playAndDisplayTrack(tracks, position, getSupportFragmentManager(), mTwoPane);
    }

    @Override
    public void onServiceConnected(MediaPlayerService mediaPlayerService) {
        mMediaPlayerService = mediaPlayerService;
    }

}
