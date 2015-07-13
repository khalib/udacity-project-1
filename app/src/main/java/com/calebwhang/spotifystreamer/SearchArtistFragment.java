package com.calebwhang.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.*;
import kaaes.spotify.webapi.android.models.Artist;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Encapsulates fetching artist search rersults and displaying it as a {@link ListView} layout.
 */
public class SearchArtistFragment extends Fragment {

    private final String LOG_TAG = SearchArtistFragment.class.getSimpleName();

    private String mSearchText;
    private SearchArtistAdapter mSearchArtistAdapter;
    private ArtistsPager mArtistsPager;

    public SearchArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_search_artist, container, false);

        // Set a listener for when a search query is being typed in.
        EditText searchEditText = (EditText) rootView.findViewById(R.id.search_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchText = s.toString();
                getArtistSearchResults();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Initialize the layout.
        mSearchArtistAdapter = new SearchArtistAdapter(getActivity(), R.layout.list_item_artist, new ArrayList<Artist>());

        // Get a reference to the RecyclerView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_search);
        listView.setAdapter(mSearchArtistAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = (Artist) parent.getItemAtPosition(position);

                if (artist != null) {
                    Intent intent = new Intent(getActivity(), TopTracksActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, artist.id);

                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    /**
     * Fetches artist search results from the Spotify API.
     */
    private void getArtistSearchResults() {
        SpotifyApi api = new SpotifyApi();
        SpotifyService spotifyService = api.getService();

        spotifyService.searchArtists(mSearchText, new Callback<ArtistsPager>() {
            @Override
            public void success(ArtistsPager artistsPager, Response response) {
                mArtistsPager = artistsPager;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateSearchResults();
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.v(LOG_TAG, "FAILURE");
            }
        });
    }

    /**
     * Updates the list view with the fetched data.
     */
    private void updateSearchResults() {
        mSearchArtistAdapter.clear();

        for (Iterator<Artist> i = mArtistsPager.artists.items.iterator(); i.hasNext();) {
            Artist artist = i.next();
            mSearchArtistAdapter.add(artist);
        }
    }
}
