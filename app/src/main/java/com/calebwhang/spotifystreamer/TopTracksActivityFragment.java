package com.calebwhang.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;


/**
 * Encapsulates fetching an artists top tracks and displaying it as a {@link ListView} layout.
 */
public class TopTracksActivityFragment extends Fragment {

    private final String ARTIST_ID_KEY = "artist_id";
    private final String ARTIST_NAME_KEY = "artist_name";
    private final String ARTIST_TOP_TRACKS_KEY = "artist_top_tracks";

    private String mArtistId;
    private String mArtistName;
    private ArrayList<TrackParcelable> mTopTracksList;
    private TopTracksAdapter mTopTracksAdapter;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        if (savedInstanceState != null) {
            // Load activity states.
            mArtistId = savedInstanceState.getString(ARTIST_ID_KEY);
            mArtistName = savedInstanceState.getString(ARTIST_NAME_KEY);
            mTopTracksList = savedInstanceState.getParcelableArrayList(ARTIST_TOP_TRACKS_KEY);
        } else {
            // The detail Activity called via intent and get the artist ID.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mArtistId = intent.getStringExtra(Intent.EXTRA_TEXT);
                mArtistName = intent.getStringExtra(Intent.EXTRA_TITLE);
            }

            // Initialize empty list.
            mTopTracksList = new ArrayList<TrackParcelable>();
        }

        // Set the artist name in the action bar.
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setSubtitle(mArtistName);

        // Initialize the layout.
        mTopTracksAdapter = new TopTracksAdapter(getActivity(), R.layout.list_item_track, mTopTracksList);

        // Set up the list view.
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_tracks);
        listView.setAdapter(mTopTracksAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), R.string.toast_message_implemented_stage_2, Toast.LENGTH_LONG).show();
            }
        });

        // Load the tracks from the Spotify API if not stored locally.
        if (savedInstanceState == null && mTopTracksList.isEmpty()) {
            getTopTracksList();
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store activity states.
        outState.putString(ARTIST_ID_KEY, mArtistId);
        outState.putString(ARTIST_NAME_KEY, mArtistName);
        outState.putParcelableArrayList(ARTIST_TOP_TRACKS_KEY, mTopTracksList);

        super.onSaveInstanceState(outState);
    }

    /**
     * Fetches Top Tracks list of a given artist from the Spotify API.
     */
    private void getTopTracksList() {
        SpotifyArtistTopTrackTask spotifyArtistTopTrackTask = new SpotifyArtistTopTrackTask(getActivity(), mTopTracksAdapter);
        spotifyArtistTopTrackTask.execute(mArtistId);
    }
}
