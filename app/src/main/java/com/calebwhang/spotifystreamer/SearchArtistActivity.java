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

    private MediaPlayerService mMediaPlayerService;
    private boolean mIsMediaServiceBound;
    private boolean mTwoPane;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artist);

        // Check and set if a larger screen is being used.
        if (findViewById(R.id.top_tracks_detail_container) != null) {
            mTwoPane = true;

//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.fragment_artist_search, new SearchArtistFragment())
//                        .commit();
//            }
        } else {
            mTwoPane = false;
        }

        // Bind to MediaPlayerService.
        Intent intent = new Intent(this, MediaPlayerService.class);
        bindService(intent, mMediaPlayerConnection, Context.BIND_AUTO_CREATE);
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

            // TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST
            mMediaPlayerService.setTrackList(mTracks);
            mMediaPlayerService.setTrack(0);
            mMediaPlayerService.playTrack();
            Log.v(LOG_TAG, "==============================================================================");
            Log.v(LOG_TAG, "TEST TRACK PLAYING");
            // TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsMediaServiceBound = false;
        }
    };

    // TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST
    private TrackParcelable mCurrentTrack = getTracks().get(0);
    private ArrayList<TrackParcelable> mTracks = getTracks();
    private ArrayList<TrackParcelable> getTracks() {
        ArrayList<TrackParcelable> tracks = new ArrayList<TrackParcelable>();

        TrackParcelable trackParcelable = new TrackParcelable();
        trackParcelable = new TrackParcelable();
        trackParcelable.previewUrl = "https://p.scdn.co/mp3-preview/a0a9e25b7802988350236ee30032e9dbfc516a4d";
        trackParcelable.previewDuration = 30000;
        trackParcelable.name = "La Vie Boheme";
        trackParcelable.artist = "Original Broadway Cast \"Rent\"";
        trackParcelable.album = "Rent";
        trackParcelable.image = "https://i.scdn.co/image/b0ae1df3a46bc28db3bc8f322033437e93431e07";
        tracks.add(trackParcelable);

        trackParcelable = new TrackParcelable();
        trackParcelable.previewUrl = "https://p.scdn.co/mp3-preview/273011743128c96a68595dc2ff700483e1623a07";
        trackParcelable.previewDuration = 30000;
        trackParcelable.name = "Learning To Fly";
        trackParcelable.artist = "Foo Fighters";
        trackParcelable.album = "Foo Fighters (album)";
        trackParcelable.image = "https://i.scdn.co/image/681e1253512a49cd0e84202573daca388aaa8bad";
        tracks.add(trackParcelable);

        return tracks;
    }
    // TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST

}
