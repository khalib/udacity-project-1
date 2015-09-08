package com.calebwhang.spotifystreamer.service;

/**
 * Allow the Service to communicate with the component via a callback listener.
 */
public interface MediaPlayerServiceListenerInterface
{
    /**
     * Callback when the service is connected.
     *
     * @param mediaPlayerService
     */
    void onServiceConnected(MediaPlayerService mediaPlayerService);
}
