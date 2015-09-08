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
 * Created by caleb on 8/19/15.
 */
public class SpotifyStreamerApplication extends Application {

    private final String LOG_TAG = SpotifyStreamerApplication.class.getSimpleName();

    private MediaPlayerService mMediaPlayerService;
    private MediaPlayerServiceManager mMediaPlayerServiceManager;
    private MediaPlayerServiceConnection mMediaPlayerServiceConnection;
    private MediaPlayerServiceListenerInterface mMediaPlayerListener;
    private boolean mIsMediaServiceBound;

    public SpotifyStreamerApplication() {}

    @Override
    public void onCreate() {
        Log.v(LOG_TAG, "===== onCreate()");

        super.onCreate();

        // Create service manager for the Media Player Service.
        mMediaPlayerServiceManager = new MediaPlayerServiceManager(this);

//        Intent intent = new Intent(this, MediaPlayerService.class);
//        mIsMediaServiceBound = bindService(intent, mMediaPlayerConnection, Context.BIND_AUTO_CREATE);
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

    /**
     * Manage MediaPlayerService connection.
     */
//    private ServiceConnection mMediaPlayerConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.v(LOG_TAG, "===== onServiceConnected() >> MediaPlayerService loaded");
//
//            MediaPlayerService.MediaPlayerServiceBinder binder = (MediaPlayerService.MediaPlayerServiceBinder) service;
//            mMediaPlayerService = binder.getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            unbindService(this);
//            mIsMediaServiceBound = false;
//        }
//    };

    /**
     * Get the connected instance of the Media Player Service.
     *
     * @return
     */
//    public MediaPlayerService getMediaPlayerService() {
//        Log.v(LOG_TAG, "===== getMediaPlayerService()");
//
//        return mMediaPlayerService;
//    }

}
