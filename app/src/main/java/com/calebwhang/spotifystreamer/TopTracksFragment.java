package com.calebwhang.spotifystreamer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.calebwhang.spotifystreamer.service.MediaPlayerService;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceConnection;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceListenerInterface;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceManager;

import java.util.ArrayList;
import java.util.Iterator;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;


/**
 * Encapsulates fetching an artists top tracks and displaying it as a {@link ListView} layout.
 */
public class TopTracksFragment extends Fragment implements SpotifyArtistTopTrackTask.OnPostExecute,
        MediaPlayerServiceListenerInterface {

    private final String LOG_TAG = TopTracksFragment.class.getSimpleName();

    private MediaPlayerServiceManager mMediaPlayerServiceManager;
    private MediaPlayerService mMediaPlayerService;
    private ArtistParcelable mArtistParcelable;
    private ArrayList<TrackParcelable> mTopTracksList;
    private TopTracksAdapter mTopTracksAdapter;
    private ProgressDialog mProgressDialog;

    public static final String ARTIST_TOP_TRACKS_KEY = "artist_top_tracks";
    public static final String ARTIST_PARCELABLE_KEY = "artist_parcelable";

    public TopTracksFragment() {}

    /**
     * A callback interface that all activities containing this fragment must implement.
     * This mechanism allows activities to be notified of item selections.
     */
    public interface Callback {
        /**
         * TopTracksActivity Fragement Callback for when a track has been selected.
         */
        public void onTrackSelected(ArrayList<TrackParcelable> tracks, int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Bind the media player service.
        mMediaPlayerServiceManager = ((SpotifyStreamerApplication) getActivity().getApplicationContext()).getServiceManager();
        mMediaPlayerServiceManager.bindService(new MediaPlayerServiceConnection(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Get the arguments passed from the parent activity.
        Bundle arguments = getArguments();
        if (arguments != null) {
            mArtistParcelable = arguments.getParcelable(ARTIST_PARCELABLE_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        if (savedInstanceState != null) {
            // Load activity states.
            mArtistParcelable = savedInstanceState.getParcelable(ARTIST_PARCELABLE_KEY);
            mTopTracksList = savedInstanceState.getParcelableArrayList(ARTIST_TOP_TRACKS_KEY);
        } else {
            // The detail Activity called via intent and get the artist ID.
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                mArtistParcelable = intent.getParcelableExtra(Intent.EXTRA_TEXT);
            }

            // Initialize empty list.
            mTopTracksList = new ArrayList<TrackParcelable>();
        }

        // Set the artist name in the action bar.
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setSubtitle(mArtistParcelable.name);

        // Initialize the layout.
        mTopTracksAdapter = new TopTracksAdapter(getActivity(), R.layout.list_item_track, mTopTracksList);

        // Set up the list view.
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_tracks);
        listView.setAdapter(mTopTracksAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((Callback) getActivity()).onTrackSelected(mTopTracksList, position);
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
        outState.putParcelable(ARTIST_PARCELABLE_KEY, mArtistParcelable);
        outState.putParcelableArrayList(ARTIST_TOP_TRACKS_KEY, mTopTracksList);

        super.onSaveInstanceState(outState);
    }

    /**
     * Delegate callback from when the Spotify API fetch for an artists top tracks is completed.
     *
     * @param tracks a list of top tracks.
     */
    @Override
    public void onPostExecute(Tracks tracks) {
        if (tracks.tracks.size() > 0) {
            mTopTracksAdapter.clear();

            for (Iterator<Track> i = tracks.tracks.iterator(); i.hasNext();) {
                // Load the data into the parcelable object.
                Track track = i.next();
                TrackParcelable trackParcelable = new TrackParcelable(track);

                // Update the artist search results.
                mTopTracksAdapter.add(trackParcelable);
            }

            // Load track list.
            // Note: mTopTracksList is updated by reference by the adapter.
            mMediaPlayerService.setTrackList(mTopTracksList);
        }

        mProgressDialog.dismiss();
    }

    @Override
    public void onMediaPlayerServiceConnected(MediaPlayerService mediaPlayerService) {
        mMediaPlayerService = mediaPlayerService;
    }

    /**
     * Fetches Top Tracks list of a given artist from the Spotify API.
     */
    private void getTopTracksList() {
        mProgressDialog = ProgressDialog.show(getActivity(), null, getString(R.string.progress_dialog_loading));

        SpotifyArtistTopTrackTask spotifyArtistTopTrackTask = new SpotifyArtistTopTrackTask(getActivity());

        // Set as task listener.
        spotifyArtistTopTrackTask.setOnPostExecute(this);

        // Run the task.
        spotifyArtistTopTrackTask.execute(mArtistParcelable.id);
    }

}
