package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.Iterator;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.*;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by caleb on 7/11/15.
 */
public class FetchSpotifyTask extends AsyncTask<String, Void, ArtistsPager> {

    private final String LOG_TAG = FetchSpotifyTask.class.getSimpleName();

    private final Context mContext;
    private ArrayAdapter<String> mSearchArtistAdapter;

    public FetchSpotifyTask(Context mContext, ArrayAdapter<String> searchArtistAdapter) {
        this.mContext = mContext;
        this.mSearchArtistAdapter = searchArtistAdapter;
    }

    @Override
    protected ArtistsPager doInBackground(String... params) {
        String searchText = params[0];

        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();

        spotifyService.searchArtists(searchText, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                Log.v(LOG_TAG, "SUCCESS");
                Log.v(LOG_TAG, artistsPager.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v(LOG_TAG, "FAILURE");
            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(ArtistsPager artistsPager) {
        super.onPostExecute(artistsPager);

        Log.v(LOG_TAG, "onPostExecute ==============");

        if (artistsPager != null && mSearchArtistAdapter != null) {
            Log.v(LOG_TAG, artistsPager.toString());

            mSearchArtistAdapter.clear();

            for (Iterator<Artist> i = artistsPager.artists.items.iterator(); i.hasNext();) {
                Artist artist = i.next();
                mSearchArtistAdapter.add("hello world");

                Log.v(LOG_TAG, artist.name);
            }
        }
    }
}
