package com.calebwhang.spotifystreamer;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.calebwhang.spotifystreamer.service.MediaPlayerServiceManager;

/**
 * An {@link Application} to maintain global application states.
 */
public class SpotifyStreamerApplication extends Application {

    private final String LOG_TAG = SpotifyStreamerApplication.class.getSimpleName();

    private MediaPlayerServiceManager mMediaPlayerServiceManager;
    private static Context mContext;

    public SpotifyStreamerApplication() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Set context for static calls.
        mContext = this.getApplicationContext();

        // Create service manager for the Media Player Service.
        mMediaPlayerServiceManager = new MediaPlayerServiceManager(this);
    }

    /**
     * Gets the application service manager.
     *
     * @return the application service manager
     */
    public MediaPlayerServiceManager getServiceManager() {
        return mMediaPlayerServiceManager;
    }

    /**
     * A static call to get the application context.
     *
     * @return the application context.
     */
    public static Context getStaticApplicationContext() {
        return mContext;
    }

}
