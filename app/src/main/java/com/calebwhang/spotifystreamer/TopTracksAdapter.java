package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * {@link TopTracksAdapter} exposes a list of artist top tracks
 * to a {@link android.widget.ListView}.
 *
 * Created by caleb on 7/13/15.
 */
public class TopTracksAdapter extends ArrayAdapter<TrackParcelable> {

    private final String LOG_TAG = TopTracksAdapter.class.getSimpleName();

    public TopTracksAdapter(Context context, int resource, ArrayList<TrackParcelable> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TrackParcelable track = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_track, parent, false);
        }

        // Load the view elements.
        TextView title = (TextView) convertView.findViewById(R.id.list_item_track_title_textview);
        title.setText(track.name);

        TextView albumName = (TextView) convertView.findViewById(R.id.list_item_track_album_name_textview);
        albumName.setText(track.album);

        ImageView image = (ImageView) convertView.findViewById(R.id.list_item_track_image);

        // Account for images not existing for the artist.
        if (track.image != null) {
            // Display album image.
            Picasso.with(getContext())
                    .load(track.image)
                    .error(getContext().getResources().getDrawable(R.mipmap.ic_launcher))
                    .into(image);
        } else {
            // Display default image.
            image.setImageResource(R.mipmap.ic_launcher);
        }

        return convertView;
    }

}
