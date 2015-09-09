package com.calebwhang.spotifystreamer.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.calebwhang.spotifystreamer.service.MediaPlayerService.MediaPlayerServiceBinder;

/**
 * Interface for handling the state of the MediaPlayerService at the application level.
 */
public class MediaPlayerServiceConnection implements ServiceConnection {

    private final String LOG_TAG = MediaPlayerServiceConnection.class.getSimpleName();

    private MediaPlayerService mMediaPlayerService;
    private boolean mIsMediaPlayerServiceBound = false;

    // This is the object that receives interactions from service.
    private MediaPlayerServiceListenerInterface mListener;

    public MediaPlayerServiceConnection(MediaPlayerServiceListenerInterface listener) {
        setServiceListener(listener);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MediaPlayerServiceBinder binder = (MediaPlayerServiceBinder) service;
        mMediaPlayerService = binder.getService();
        mIsMediaPlayerServiceBound = true;

        // Invoke callback.
        if (mListener != null) {
            mListener.onServiceConnected(mMediaPlayerService);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mIsMediaPlayerServiceBound = false;
    }

    /**
     * Set the callback listener that should be used to communicate with the
     * activity. This will be forwarded to the Service and *must* be called
     * before onBind() in this case.
     *
     * @param listener the callback that will be run.
     */
    public void setServiceListener(MediaPlayerServiceListenerInterface listener)
    {
        mListener = listener;
    }

}
