package com.calebwhang.spotifystreamer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchArtistFragment extends Fragment {

    private TrackAdapter mTrackAdapter;

    public SearchArtistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // The ArrayAdapter will take data from a source and use it to populate the ListView it's attached to.
//        mTrackAdapter = new TrackAdapter(getActivity(), cursor, 0);

        View rootView = inflater.inflate(R.layout.fragment_search_artist, container, false);

        // Set a listener for when a search query is being typed in.
        EditText searchEditText = (EditText) rootView.findViewById(R.id.search_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Get a reference to the RecyclerView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_search);
        listView.setAdapter(mTrackAdapter);

        return rootView;
    }
}
