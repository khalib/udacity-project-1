package com.calebwhang.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Implements {@link Parcelable} to store track data.
 */
public class TrackParcelable implements Parcelable {

    private final Integer TRACK_IMAGE_LARGE = 0;
    private final Integer TRACK_IMAGE_MEDIUM = 1;
    private final Integer TRACK_IMAGE_SMALL = 2;
    private final String TRACK_EXTERNAL_URL_SPOTIFY = "spotify";

    public String name;
    public String album;
    public String image;
    public String artist;
    public String previewUrl;
    public String externalSpotifyUrl;
    public long duration;
    public long previewDuration;

    public static final Parcelable.Creator<TrackParcelable> CREATOR = new Creator<TrackParcelable>() {
        @Override
        public TrackParcelable createFromParcel(Parcel source) {
            return new TrackParcelable(source);
        }

        @Override
        public TrackParcelable[] newArray(int size) {
            return new TrackParcelable[size];
        }
    };

    public TrackParcelable() {

    }

    public TrackParcelable(Track track) {
        name = track.name;
        album = track.album.name;
        artist = track.artists.get(0).name;
        duration = track.duration_ms;
        previewUrl = track.preview_url;
        previewDuration = 30000;  // in ms
        externalSpotifyUrl = track.external_urls.get(TRACK_EXTERNAL_URL_SPOTIFY);

        // Assign image to the track.
        String trackImage = null;
        if (track.album.images.size() > 0) {
            trackImage = track.album.images.get(TRACK_IMAGE_MEDIUM).url;
        }
        image = trackImage;
    }

    private TrackParcelable(Parcel in) {
        name = in.readString();
        album = in.readString();
        image = in.readString();
        artist = in.readString();
        previewUrl = in.readString();
        duration = in.readLong();
        previewDuration = in.readLong();
        externalSpotifyUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(album);
        dest.writeString(image);
        dest.writeString(artist);
        dest.writeString(previewUrl);
        dest.writeLong(duration);
        dest.writeLong(previewDuration);
        dest.writeString(externalSpotifyUrl);
    }

}
