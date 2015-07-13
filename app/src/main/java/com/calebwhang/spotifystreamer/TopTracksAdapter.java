package com.calebwhang.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by caleb on 7/13/15.
 */
public class TopTracksAdapter extends ArrayAdapter<Track> {

    public TopTracksAdapter(Context context, int resource, List<Track> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Track track = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_track, parent, false);
        }

        // Load the view elements.
        TextView title = (TextView) convertView.findViewById(R.id.list_item_track_title_textview);
        title.setText(track.name);

        return convertView;
    }

}
