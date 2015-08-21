package com.calebwhang.spotifystreamer.service;


import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

/**
 * Manages the life cycle of a Service and the component (Activity/Fragment) that bind to it.
 *
 * Created by caleb on 8/19/15.
 */
public class ServiceManager {

    private final String LOG_TAG = ServiceManager.class.getSimpleName();

    private boolean mIsServiceStarted = false;
    private Context mContext;
    private Intent mServiceIntent;

    public ServiceManager(Context context, Class<?> intentClass) {
        mContext = context;
        mServiceIntent = new Intent(mContext, intentClass);
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
