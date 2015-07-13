package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Iterator;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Fetches artist search results from the Spotify API.
 */
public class SpotifyArtistSearchTask extends AsyncTask<String, Void, ArtistsPager> {

    private final String LOG_TAG = SpotifyArtistSearchTask.class.getSimpleName();

    private final Context mContext;
    private final SearchArtistAdapter mSearchArtistAdapter;

    public SpotifyArtistSearchTask(Context mContext, SearchArtistAdapter mSearchArtistAdapter) {
        this.mContext = mContext;
        this.mSearchArtistAdapter = mSearchArtistAdapter;
    }

    @Override
    protected ArtistsPager doInBackground(String... params) {
        String searchText = params[0];

        if (searchText.length() > 0) {
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();

            return spotifyService.searchArtists(searchText);
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArtistsPager artistsPager) {
        super.onPostExecute(artistsPager);

        if (artistsPager != null) {
            if (artistsPager.artists.items.size() > 0) {
                mSearchArtistAdapter.clear();

                for (Iterator<Artist> i = artistsPager.artists.items.iterator(); i.hasNext();) {
                    Artist artist = i.next();

                    // Update the artist search results.
                    mSearchArtistAdapter.add(artist);
                }
            } else {
                Toast.makeText(mContext, R.string.toast_error_no_artist_found, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
