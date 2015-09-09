package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.RetrofitError;

/**
 * Fetches artist search results from the Spotify API.
 */
public class SpotifyArtistSearchTask extends AsyncTask<String, Void, ArtistsPager> {

    private final String LOG_TAG = SpotifyArtistSearchTask.class.getSimpleName();

    private final Context mContext;
    private final SearchArtistAdapter mSearchArtistAdapter;

    private OnPostExecute mOnPostExecute;

    private Exception mException;

    public SpotifyArtistSearchTask(Context mContext, SearchArtistAdapter mSearchArtistAdapter) {
        this.mContext = mContext;
        this.mSearchArtistAdapter = mSearchArtistAdapter;
    }

    /**
     * Interface definition for a callback to be invoked when search task is completed.
     */
    public interface OnPostExecute {

        void onPostExecute(ArtistsPager artistsPager);

    }

    /**
     * Register a callback to be invoked when the search task is completed.
     *
     * @param listener the callback that will be run.
     */
    public void setOnPostExecute(OnPostExecute listener) {
        mOnPostExecute = listener;
    }

    @Override
    protected ArtistsPager doInBackground(String... params) {
        String searchText = params[0];
        ArtistsPager artistsPager = null;

        if (searchText.length() > 0) {
            // Make API call to Spotify.
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();

            try {
                artistsPager = spotifyService.searchArtists(searchText);
            } catch (RetrofitError retrofitError) {
                mException = retrofitError;
            }
        }

        return artistsPager;
    }

    @Override
    protected void onPostExecute(ArtistsPager artistsPager) {
        super.onPostExecute(artistsPager);

        // Invoke callback.
        if (mOnPostExecute != null) {
            mOnPostExecute.onPostExecute(artistsPager);
        }

        if (mException != null) {
            Toast.makeText(mContext, R.string.toast_error_no_internet, Toast.LENGTH_LONG).show();
        }
    }
}
