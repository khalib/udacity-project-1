package com.calebwhang.spotifystreamer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.calebwhang.spotifystreamer.service.MediaPlayerServiceListenerInterface;
import com.calebwhang.spotifystreamer.service.MediaPlayerService;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceConnection;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceManager;

/**
 * An {@link Application} to maintain global application states.
 */
public class SpotifyStreamerApplication extends Application {

    private final String LOG_TAG = SpotifyStreamerApplication.class.getSimpleName();

    private MediaPlayerServiceManager mMediaPlayerServiceManager;

    public SpotifyStreamerApplication() {}

    @Override
    public void onCreate() {
        Log.v(LOG_TAG, "===== onCreate()");

        super.onCreate();

        // Create service manager for the Media Player Service.
        mMediaPlayerServiceManager = new MediaPlayerServiceManager(this);
    }

    /**
     * Gets the application service manager.
     *
     * @return the application service manager
     */
    public MediaPlayerServiceManager getServiceManager() {
        Log.v(LOG_TAG, "===== getServiceManager()");

        return mMediaPlayerServiceManager;
    }

}
