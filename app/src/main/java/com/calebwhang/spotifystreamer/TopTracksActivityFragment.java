package com.calebwhang.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Encapsulates fetching an artists top tracks and displaying it as a {@link ListView} layout.
 */
public class TopTracksActivityFragment extends Fragment {

    private String mArtistId;
    private TopTracksAdapter mTopTracksAdapter;
    private Tracks mTracks;

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        // The detail Activity called via intent and get the artist ID.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mArtistId = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        // Initialize the layout.
        mTopTracksAdapter = new TopTracksAdapter(getActivity(), R.layout.list_item_track, new ArrayList<Track>());

        // Set up the list view.
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_tracks);
        listView.setAdapter(mTopTracksAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        // Load the tracks.
        getTopTracksList();

        return rootView;
    }

    /**
     * Fetches Top Tracks list of a given artist from the Spotify API.
     */
    private void getTopTracksList() {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();

        // Get the user's local.
        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());

        spotifyService.getArtistTopTrack(mArtistId, options, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                mTracks = tracks;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTracks();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    /**
     * Updates the list view of the artist tracks.
     */
    private void updateTracks() {
        mTopTracksAdapter.clear();

        for (Iterator<Track> i = mTracks.tracks.iterator(); i.hasNext();) {
            Track track = i.next();
            mTopTracksAdapter.add(track);
        }
    }
}
