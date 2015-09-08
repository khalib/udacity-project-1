package com.calebwhang.spotifystreamer.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.calebwhang.spotifystreamer.service.MediaPlayerService.MediaPlayerServiceBinder;

/**
 * Interface for handling the state of the MediaPlayerService at the application level.
 *
 * Created by caleb on 8/19/15.
 */
public class MediaPlayerServiceConnection implements ServiceConnection {

    private final String LOG_TAG = MediaPlayerServiceConnection.class.getSimpleName();

    private MediaPlayerService mMediaPlayerService;

    // This is the object that receives interactions from service.
    private MediaPlayerServiceListenerInterface mListener;

    public MediaPlayerServiceConnection(MediaPlayerServiceListenerInterface listener) {
        setServiceListener(listener);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.v(LOG_TAG, "MediaPlayerService Connected");

        MediaPlayerServiceBinder binder = (MediaPlayerServiceBinder) service;
        mMediaPlayerService = binder.getService();
        mListener.onServiceConnected(mMediaPlayerService);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.v(LOG_TAG, "MediaPlayerService Disconnected");
    }

    /**
     * Set the callback listener that should be used to communicate with the
     * activity. This will be forwarded to the Service and *must* be called
     * before onBind() in this case.
     *
     * @param listener
     */
    public void setServiceListener(MediaPlayerServiceListenerInterface listener)
    {
        mListener = listener;
    }

}
