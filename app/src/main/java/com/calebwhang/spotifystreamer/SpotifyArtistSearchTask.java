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
import retrofit.RetrofitError;

/**
 * Fetches artist search results from the Spotify API.
 */
public class SpotifyArtistSearchTask extends AsyncTask<String, Void, ArtistsPager> {

    private final String LOG_TAG = SpotifyArtistSearchTask.class.getSimpleName();

    private final Integer ARTIST_IMAGE_LARGE = 0;
    private final Integer ARTIST_IMAGE_MEDIUM = 1;
    private final Integer ARTIST_IMAGE_SMALL = 2;

    private final Context mContext;
    private final SearchArtistAdapter mSearchArtistAdapter;

    private Exception exception;

    public SpotifyArtistSearchTask(Context mContext, SearchArtistAdapter mSearchArtistAdapter) {
        this.mContext = mContext;
        this.mSearchArtistAdapter = mSearchArtistAdapter;
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
                exception = retrofitError;
            }
        }

        return artistsPager;
    }

    @Override
    protected void onPostExecute(ArtistsPager artistsPager) {
        super.onPostExecute(artistsPager);

        if (exception != null) {
            Toast.makeText(mContext, R.string.toast_error_no_internet, Toast.LENGTH_LONG).show();
        } else if (artistsPager != null) {
            if (artistsPager.artists.items.size() > 0) {
                mSearchArtistAdapter.clear();

                // Update the artist search results.
                for (Iterator<Artist> i = artistsPager.artists.items.iterator(); i.hasNext();) {
                    Artist artist = i.next();

                    // Assign image to artist.
                    String artistImage = null;
                    if (artist.images.size() > 0) {
                        artistImage = artist.images.get(ARTIST_IMAGE_MEDIUM).url;
                    }

                    // Load the data into the parcelable object.
                    ArtistParcelable artistParcelable = new ArtistParcelable(artist.id, artist.name, artistImage);
                    mSearchArtistAdapter.add(artistParcelable);
                }
            } else {
                // Display message for empty results.
                Toast.makeText(mContext, R.string.toast_error_no_artist_found, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
