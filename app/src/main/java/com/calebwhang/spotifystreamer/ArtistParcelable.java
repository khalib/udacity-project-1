package com.calebwhang.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Implements {@link Parcelable} to store artists data.
 */
public class ArtistParcelable implements Parcelable {

    public String id;
    public String name;
    public String image;

    public static final Parcelable.Creator<ArtistParcelable> CREATOR = new Creator<ArtistParcelable>() {
        @Override
        public ArtistParcelable createFromParcel(Parcel source) {
            return new ArtistParcelable(source);
        }

        @Override
        public ArtistParcelable[] newArray(int size) {
            return new ArtistParcelable[size];
        }
    };

    public ArtistParcelable(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    private ArtistParcelable(Parcel in) {
        id = in.readString();
        name = in.readString();
        image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(image);
    }
}
