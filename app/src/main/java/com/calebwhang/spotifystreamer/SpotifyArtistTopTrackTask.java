package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;

/**
 * Fetches artist search results from the Spotify API.
 */
public class SpotifyArtistTopTrackTask extends AsyncTask<String, Void, Tracks> {

    private final String LOG_TAG = SpotifyArtistSearchTask.class.getSimpleName();

    private final Context mContext;
    private Exception exception;
    private OnCompletionListener mCompletionListener;

    public SpotifyArtistTopTrackTask(Context context) {
        this.mContext = context;
    }

    /**
     *
     */
    public interface OnCompletionListener {

        void onCompletion(Tracks tracks);

    }

    @Override
    protected Tracks doInBackground(String... params) {
        String artistId = params[0];
        Tracks tracks = null;

        // Make API call to Spotify.
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();

        try {
            // Get the user's local from the preference settings.
            String countryCode = Utility.getCountryCodeSettings(mContext);

            Map<String, Object> options = new HashMap<>();
            options.put(SpotifyService.COUNTRY, countryCode);

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
            mCompletionListener.onCompletion(tracks);

            if (tracks.tracks.size() == 0) {
                // Display error message for empty results.
                Toast.makeText(mContext, R.string.toast_error_no_artist_top_songs, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Register a callback to be invoked when call to the Spotify API is complete.
     *
     * @param completionListener the callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener completionListener) {
        mCompletionListener = completionListener;
    }

}
