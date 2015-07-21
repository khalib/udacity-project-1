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

    private ViewHolder mViewHolder;

    public TopTracksAdapter(Context context, int resource, ArrayList<TrackParcelable> objects) {
        super(context, resource, objects);
    }

    public static class ViewHolder {
        public final TextView trackTitle;
        public final TextView albumName;
        public final ImageView trackImage;

        public ViewHolder(View view) {
            trackTitle = (TextView) view.findViewById(R.id.list_item_track_title_textview);
            albumName = (TextView) view.findViewById(R.id.list_item_track_album_name_textview);
            trackImage = (ImageView) view.findViewById(R.id.list_item_track_image);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TrackParcelable track = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_track, parent, false);
            mViewHolder = new ViewHolder(convertView);
        }

        // Load the view elements.
        mViewHolder.trackTitle.setText(track.name);
        mViewHolder.albumName.setText(track.album);

        // Account for images not existing for the artist.
        if (track.image != null) {
            // Display album image.
            Picasso.with(getContext())
                    .load(track.image)
                    .error(getContext().getResources().getDrawable(R.mipmap.ic_launcher))
                    .into(mViewHolder.trackImage);
        } else {
            // Display default image.
            mViewHolder.trackImage.setImageResource(R.mipmap.ic_launcher);
        }

        return convertView;
    }

}
