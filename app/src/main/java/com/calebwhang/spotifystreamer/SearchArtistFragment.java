package com.calebwhang.spotifystreamer;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Encapsulates fetching artist search rersults and displaying it as a {@link ListView} layout.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
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

    public SearchArtistFragment() {}

    /**
     * A callback interface that all activities containing this fragment must implement.
     * This mechanism allows activities to be notified of item selections.
     */
    public interface Callback {
        /**
         * SearchArtistFragment Callback for when an artist has been selected.
         */
        public void onArtistSelected(ArtistParcelable artistParcelable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_search_artist, container, false);

        if (savedInstanceState != null) {
            // Load the stored states.
            mSearchText = savedInstanceState.getString(ARTIST_SEARCH_TEXT_KEY);
            mArtistSearchResults = savedInstanceState.getParcelableArrayList(ARTIST_SEARCH_RESULTS_KEY);
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
                    ((Callback) getActivity()).onArtistSelected(artistParcelable);
                }
            }
        });

        // Initialize search view.
        SearchView searchText = (SearchView) mRootView.findViewById(R.id.search_text);
        searchText.setIconifiedByDefault(false);
        searchText.setQueryHint(getResources().getString(R.string.artist_search_hint));
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Search is already performed on text change.
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Assign the search string and perform the search.
                if (!mSearchText.equals(s.toString())) {
                    mSearchText = s.toString();
                    getArtistSearchResults();
                }

                return false;
            }
        });



        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store activity states.
        outState.putString(ARTIST_SEARCH_TEXT_KEY, mSearchText);
        outState.putParcelableArrayList(ARTIST_SEARCH_RESULTS_KEY, mArtistSearchResults);

        super.onSaveInstanceState(outState);
    }

    /**
     * Fetches artist search results from the Spotify API.
     */
    private void getArtistSearchResults() {
        SpotifyArtistSearchTask spotifyArtistSearchTask = new SpotifyArtistSearchTask(getActivity(), mSearchArtistAdapter);
        spotifyArtistSearchTask.execute(mSearchText);
    }

    /**
     * FOR TESTING FOR TESTING FOR TESTING FOR TESTING FOR TESTING FOR TESTING FOR TESTING
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSearchText = "d";
//        getArtistSearchResults();
    }
}
