package com.calebwhang.spotifystreamer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by caleb on 6/13/15.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private ArrayList<Artist> artists;

    // Provide a reference to the views for each data item.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
        }
    }

    // Provide a suitable constructor depending on the dataset.
    public ArtistAdapter(ArrayList<Artist> artists) {
        this.artists = artists;
    }

    // Create new views.
    @Override
    public ArtistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_row, parent, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get element from the dataset at this position
        TextView name = (TextView) holder.view.findViewById(R.id.name);

        // Replace the contents of the view with the element.
        name.setText(artists.get(position).getName());
    }

    // Return the size of the dataset.
    @Override
    public int getItemCount() {
        return artists.size();
    }

}
