package com.calebwhang.spotifystreamer.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Interface for handling the state of the MediaPlayerService at the application level.
 *
 * Created by caleb on 8/19/15.
 */
public class MediaPlayerServiceConnection implements ServiceConnection {

    private final String LOG_TAG = MediaPlayerServiceConnection.class.getSimpleName();

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.v(LOG_TAG, "MediaPlayerService Connected");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.v(LOG_TAG, "MediaPlayerService Disconnected");
    }

}
