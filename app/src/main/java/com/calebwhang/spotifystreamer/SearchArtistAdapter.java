package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link SearchArtistAdapter} exposes a list of artist search results
 * to a {@link android.widget.ListView}.
 */
public class SearchArtistAdapter extends ArrayAdapter<ArtistParcelable> {

    private final String LOG_TAG = SearchArtistAdapter.class.getSimpleName();

    private final Integer ARTIST_IMAGE_WIDTH = 100;
    private final Integer ARTIST_IMAGE_HEIGHT = 100;

    private ViewHolder mViewHolder;

    public SearchArtistAdapter(Context context, int resource, ArrayList<ArtistParcelable> objects) {
        super(context, resource, objects);
    }

    public static class ViewHolder {
        public final TextView artistName;
        public final ImageView artistImage;

        public ViewHolder(View view) {
            artistName = (TextView) view.findViewById(R.id.list_item_artist_textview);
            artistImage = (ImageView) view.findViewById(R.id.list_item_artist_image);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ArtistParcelable artist = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
        }

        // Load the view elements.
        mViewHolder = new ViewHolder(convertView);
        mViewHolder.artistName.setText(artist.name);

        // Account for images not existing for the artist.
        if (artist.image != null) {
            // Display artist image.
            Picasso.with(getContext())
                    .load(artist.image)
                    .resize(ARTIST_IMAGE_WIDTH, ARTIST_IMAGE_HEIGHT)
                    .centerCrop()
                    .error(getContext().getResources().getDrawable(R.mipmap.ic_launcher))
                    .into(mViewHolder.artistImage);
        } else {
            // Display default image.
            mViewHolder.artistImage.setImageResource(R.mipmap.ic_launcher);
        }

        // Set image descriptions on for accessibility.
        mViewHolder.artistImage.setContentDescription(artist.name);

        return convertView;
    }
}
