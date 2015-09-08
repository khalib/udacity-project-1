package com.calebwhang.spotifystreamer.service;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Manages the life cycle of a Service and the component (Activity/Fragment) that bind to it.
 *
 * Created by caleb on 8/19/15.
 */
public class MediaPlayerServiceManager {

    private final String LOG_TAG = MediaPlayerServiceManager.class.getSimpleName();

    private MediaPlayerService mMediaPlayerService;
    private boolean mIsServiceStarted = false;
    private boolean mIsMediaServiceBound = false;
    private Context mContext;
    private Intent mServiceIntent;

    public MediaPlayerServiceManager(Context context) {
        mContext = context;
        mServiceIntent = new Intent(mContext, MediaPlayerService.class);
    }

    /**
     * Binds a service connection between the service and the component.
     *
     * @param serviceConnection
     */
    public void bindService(ServiceConnection serviceConnection) {
        mContext.bindService(mServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Starts the service and sets the service status state.
     */
    public void startService() {
        mIsServiceStarted = true;
        mContext.startService(mServiceIntent);
    }

    /**
     * Stops the service and sets the service status state.
     */
    public void stopService() {
        mIsServiceStarted = false;
        mContext.stopService(mServiceIntent);
    }

    /**
     * Checks if the service has been started.
     *
     * @return boolean state of the service.
     */
    public boolean isServiceStarted() {
        return mIsServiceStarted;
    }

    /**
     * Unbinds a service from the component.
     *
     * @param serviceConnection
     */
    public void unbindService(ServiceConnection serviceConnection) {
        mContext.unbindService(serviceConnection);
    }

}
