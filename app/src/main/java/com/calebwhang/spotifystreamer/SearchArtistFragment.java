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

/**
 * Encapsulates fetching artist search rersults and displaying it as a {@link ListView} layout.
 */
public class SearchArtistFragment extends Fragment {

    private final String LOG_TAG = SearchArtistFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";
    private static final String ARTIST_SEARCH_RESULTS_KEY = "artist_search_results";
    private static final String ARTIST_SEARCH_TEXT_KEY = "artist_search_text";

    private String mSearchText;
    private View mRootView;
    private ListView mListView;
    private SearchArtistAdapter mSearchArtistAdapter;
    private ArrayList<ArtistParcelable> mArtistSearchResults;
    private int mPosition = ListView.INVALID_POSITION;

    public SearchArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_search_artist, container, false);

        if (savedInstanceState != null) {
            // Load the stored states.
            mSearchText = savedInstanceState.getString(ARTIST_SEARCH_TEXT_KEY);
            mArtistSearchResults = savedInstanceState.getParcelableArrayList(ARTIST_SEARCH_RESULTS_KEY);
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        } else {
            mSearchText = "";
            mArtistSearchResults = new ArrayList<ArtistParcelable>();
        }

        // Initialize the layout.
        mSearchArtistAdapter = new SearchArtistAdapter(getActivity(), R.layout.list_item_artist, mArtistSearchResults);

        // Get a reference to the RecyclerView, and attach this adapter to it.
        mListView = (ListView) mRootView.findViewById(R.id.list_view_search);
        mListView.setAdapter(mSearchArtistAdapter);

        // Set item click listener to trigger the artist's top tracks activity.
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArtistParcelable artistParcelable = (ArtistParcelable) parent.getItemAtPosition(position);

                if (artistParcelable != null) {
                    // Pass the artist ID and name.
                    Intent intent = new Intent(getActivity(), TopTracksActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, artistParcelable.id)
                            .putExtra(Intent.EXTRA_TITLE, artistParcelable.name);

                    startActivity(intent);
                }

                mPosition = position;
                Log.v(LOG_TAG, Integer.toString(mPosition));
            }
        });

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Set a listener for when a search query is being typed in.
        EditText searchEditText = (EditText) mRootView.findViewById(R.id.search_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Assign the search string and perform the search.
                if (!mSearchText.equals(s.toString())) {
                    mSearchText = s.toString();
                    getArtistSearchResults();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Restore scroll position.
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store activity states.
        outState.putString(ARTIST_SEARCH_TEXT_KEY, mSearchText);
        outState.putParcelableArrayList(ARTIST_SEARCH_RESULTS_KEY, mArtistSearchResults);

        // Store the scroll position.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * Fetches artist search results from the Spotify API.
     */
    private void getArtistSearchResults() {
        SpotifyArtistSearchTask spotifyArtistSearchTask = new SpotifyArtistSearchTask(getActivity(), mSearchArtistAdapter);
        spotifyArtistSearchTask.execute(mSearchText);
    }

}
