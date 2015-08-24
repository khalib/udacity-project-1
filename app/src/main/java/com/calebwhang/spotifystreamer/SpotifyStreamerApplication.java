package com.calebwhang.spotifystreamer;

import android.app.Application;
import android.util.Log;

import com.calebwhang.spotifystreamer.service.MediaPlayerService;
import com.calebwhang.spotifystreamer.service.ServiceManager;

/**
 * Created by caleb on 8/19/15.
 */
public class SpotifyStreamerApplication extends Application {

    private final String LOG_TAG = SpotifyStreamerApplication.class.getSimpleName();

    private ServiceManager mServiceManager;

    public SpotifyStreamerApplication() {}

    @Override
    public void onCreate() {
        super.onCreate();

        // Create service manager for the Media Player Service.
//        mServiceManager = new ServiceManager(this, MediaPlayerService.class);
    }

    /**
     * Gets the application service manager.
     *
     * @return the application service manager
     */
    public ServiceManager getServiceManager() {
       return mServiceManager;
    }
}
