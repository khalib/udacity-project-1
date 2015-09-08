package com.calebwhang.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;


/**
 * A {@link ActionBarActivity} that presents the Artist search results.
 */
public class SearchArtistActivity extends SpotifyStreamerActivity implements
        SearchArtistFragment.Callback, TopTracksFragment.Callback {

    private final String LOG_TAG = SearchArtistActivity.class.getSimpleName();

    private static final String TOP_TRACKS_ACTIVITY_FRAGMENT_TAG = "top_tracks_activity_fragment";
    private final String INSTANCE_STATE_IS_MEDIA_SERVICE_BOUND_KEY = "instance_state_is_media_service_bound";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_artist);

        // Check if the device is using the large screen layout.
        if (findViewById(R.id.top_tracks_detail_container) != null) {
            mIsLargeLayout = true;
        }
    }

    @Override
    public void onArtistSelected(ArtistParcelable artistParcelable) {
        if (mIsLargeLayout) {
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
        mMediaPlayerService.playAndDisplayTrack(tracks, position, getSupportFragmentManager(), mIsLargeLayout);
    }

}
