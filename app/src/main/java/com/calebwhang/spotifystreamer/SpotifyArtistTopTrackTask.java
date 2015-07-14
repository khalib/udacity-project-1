package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

/**
 * Fetches artist search results from the Spotify API.
 */
public class SpotifyArtistTopTrackTask extends AsyncTask<String, Void, Tracks> {

    private final String LOG_TAG = SpotifyArtistSearchTask.class.getSimpleName();
    private final Context mContext;
    private final TopTracksAdapter mTopTracksAdapter;

    private Exception exception;

    public SpotifyArtistTopTrackTask(Context mContext, TopTracksAdapter mTopTracksAdapter) {
        this.mContext = mContext;
        this.mTopTracksAdapter = mTopTracksAdapter;
    }

    @Override
    protected Tracks doInBackground(String... params) {
        String artistId = params[0];
        Tracks tracks = null;

        // Make API call to Spotify.
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();

        try {
            // Get the user's local.
            Map<String, Object> options = new HashMap<>();
            options.put(SpotifyService.COUNTRY, Locale.getDefault().getCountry());

            tracks = spotifyService.getArtistTopTrack(artistId, options);
        } catch (RetrofitError retrofitError) {
            exception = retrofitError;
        }

        return tracks;
    }

    @Override
    protected void onPostExecute(Tracks tracks) {
        super.onPostExecute(tracks);

        if (exception != null) {
            Toast.makeText(mContext, R.string.toast_error_no_internet, Toast.LENGTH_LONG).show();
        } else if (tracks != null) {
            if (tracks.tracks.size() > 0) {
                mTopTracksAdapter.clear();

                for (Iterator<Track> i = tracks.tracks.iterator(); i.hasNext();) {
                    Track track = i.next();

                    // Update the artist search results.
                    mTopTracksAdapter.add(track);
                }
            } else {
                // Display error message for empty results.
                Toast.makeText(mContext, R.string.toast_error_no_artist_top_songs, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
