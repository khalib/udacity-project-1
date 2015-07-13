package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * {@link SearchArtistAdapter} exposes a list of artist search results
 * to a {@link android.widget.ListView}.
 *
 * Created by caleb on 7/11/15.
 */
public class SearchArtistAdapter extends ArrayAdapter<Artist> {

    private final String LOG_TAG = SearchArtistAdapter.class.getSimpleName();

    private final Integer ARTIST_IMAGE_LARGE = 0;
    private final Integer ARTIST_IMAGE_MEDIUM = 1;
    private final Integer ARTIST_IMAGE_SMALL = 2;
    private final Integer ARTIST_IMAGE_WIDTH = 100;
    private final Integer ARTIST_IMAGE_HEIGHT = 100;

    public SearchArtistAdapter(Context context, int resource, List<Artist> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Artist artist = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
        }

        // Load the view elements.
        TextView name = (TextView) convertView.findViewById(R.id.list_item_artist_textview);
        name.setText(artist.name);

        ImageView image = (ImageView) convertView.findViewById(R.id.list_item_artist_image);

        // Account for images not existing for the artist.
        if (artist.images.size() > 0) {
            // Display artist image.
            Picasso.with(getContext())
                    .load(artist.images.get(ARTIST_IMAGE_MEDIUM).url)
                    .resize(ARTIST_IMAGE_WIDTH, ARTIST_IMAGE_HEIGHT)
                    .centerCrop()
                    .error(getContext().getResources().getDrawable(R.mipmap.ic_launcher))
                    .into(image);
        } else {
            // Display default image.
            image.setImageResource(R.mipmap.ic_launcher);
        }

        return convertView;
    }
}
