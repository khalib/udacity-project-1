package com.calebwhang.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by caleb on 7/20/15.
 */
public class TrackParcelable implements Parcelable {

    public String name;
    public String album;
    public String image;

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

    public TrackParcelable(String name, String album, String image) {
        this.name = name;
        this.album = album;
        this.image = image;
    }

    private TrackParcelable(Parcel in) {
        name = in.readString();
        album = in.readString();
        image = in.readString();
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
    }

}
