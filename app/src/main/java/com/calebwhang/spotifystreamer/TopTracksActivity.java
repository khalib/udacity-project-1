package com.calebwhang.spotifystreamer;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;


/**
 * A {@link ActionBarActivity} that presents the Artist Top Tracks.
 */
public class TopTracksActivity extends SpotifyStreamerActivity implements
        TopTracksFragment.Callback {

    private final String LOG_TAG = TopTracksActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_top_tracks);
    }

    @Override
    public void onTrackSelected(ArrayList<TrackParcelable> tracks, int position) {
        // Play and display the track info and player controls.
        mMediaPlayerService.playAndDisplayTrack(tracks, position, getSupportFragmentManager(), mIsLargeLayout);
    }

}
