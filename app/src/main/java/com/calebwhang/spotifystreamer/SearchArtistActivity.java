package com.calebwhang.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class SearchArtistActivity extends ActionBarActivity implements SearchArtistFragment.Callback {

    private final String LOG_TAG = SearchArtistActivity.class.getSimpleName();
    private static final String TOP_TRACKS_ACTIVITY_FRAGMENT_TAG = "top_tracks_activity_fragment";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_artist);

        // Check and set if a larger screen is being used.
        if (findViewById(R.id.top_tracks_detail_container) != null) {
            mTwoPane = true;

//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .add(R.id.fragment_artist_search, new SearchArtistFragment())
//                        .commit();
//            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(ArtistParcelable artistParcelable) {
        if (mTwoPane) {
            // Show the detail view in this activity by adding or replacing the detail fragment.
            Bundle args = new Bundle();
            args.putParcelable(TopTracksActivityFragment.ARTIST_PARCELABLE_KEY, artistParcelable);

            TopTracksActivityFragment topTracksActivityFragment = new TopTracksActivityFragment();
            topTracksActivityFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.top_tracks_detail_container, topTracksActivityFragment, TOP_TRACKS_ACTIVITY_FRAGMENT_TAG)
                    .commit();
        } else {
            // Pass the artist ID and name.
            Intent intent = new Intent(this, TopTracksActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, artistParcelable);

            startActivity(intent);
        }
    }
}
