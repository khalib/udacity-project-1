package com.calebwhang.spotifystreamer.service;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.calebwhang.spotifystreamer.MediaPlayerActivity;
import com.calebwhang.spotifystreamer.MediaPlayerFragment;
import com.calebwhang.spotifystreamer.R;
import com.calebwhang.spotifystreamer.TrackParcelable;

import java.util.ArrayList;

/**
 *
 *
 * Created by caleb on 8/11/15.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final String LOG_TAG = MediaPlayerService.class.getSimpleName();

    private MediaPlayer mMediaPlayer;
    private ArrayList<TrackParcelable> mTracks;
    private int mCurrentTrackPosition = 0;
    private TrackParcelable mCurrentTrack;
    private Intent mPlayerControlsIntent;
    private final IBinder mMediaPlayerBinder = new MediaPlayerServiceBinder();

    public MediaPlayerService() {

    }

    /**
     * Binder class for the client to bind to this service.
     */
    public class MediaPlayerServiceBinder extends Binder {

        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }

    }

    @Override
    public void onCreate() {
        Log.v(LOG_TAG, "===== onCreate()");

        super.onCreate();

        // Initialize service.
        if (mMediaPlayer == null) {
            Log.v(LOG_TAG, "MediaPlayerService Initilized.");

            // Configure the media player.
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnErrorListener(this);

            mCurrentTrackPosition = 0;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "===== onBind()");

        return mMediaPlayerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "===== onUnbind()");

        mMediaPlayer.stop();
        mMediaPlayer.release();

        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.v(LOG_TAG, "===== onCompletion()");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v(LOG_TAG, "===== onError()");

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.v(LOG_TAG, "===== onPrepared()");

        mp.start();
    }

    /**
     *
     * @param tracks
     */
    public void setTrackList(ArrayList<TrackParcelable> tracks) {
        mTracks = tracks;
    }

    /**
     * Set the current track to play.
     *
     * @param trackPosition the track index of the track list.
     */
    public void setTrack(int trackPosition) {
        mCurrentTrackPosition = trackPosition;
        mCurrentTrack = mTracks.get(mCurrentTrackPosition);
    }

    /**
     * Gets the current track being played.
     */
    public TrackParcelable getCurrentTrack() {
        return mCurrentTrack;
    }

    /**
     *
     */
    public void playTrack() {
        playTrack(false);
    }

    /**
     *
     */
    public void playTrack(boolean resume) {
        if (!resume) {
            loadAndStartTrack();
        } else {
            Log.v(LOG_TAG, "Resuming playback.");

            // Resuming from a paused state.
            mMediaPlayer.start();
        }

        displayNotification();
    }

    /**
     *
     */
    public void loadAndStartTrack() {
        // Playing the selected track for the first time.
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            // Load the track.
            mMediaPlayer.setDataSource(mCurrentTrack.previewUrl);
        } catch (Exception e) {
            Log.v(LOG_TAG, "Error playing song.", e);
            Toast.makeText(this, R.string.toast_error_media_player_data_load, Toast.LENGTH_LONG).show();
        }

        mMediaPlayer.prepareAsync();
    }

    /**
     *
     */
    public void pauseTrack() {
        mMediaPlayer.pause();
    }

    /**
     *
     */
    public void playPreviousTrack() {
        if (hasPreviousTrack()) {
            setTrack(mCurrentTrackPosition - 1);
            loadAndStartTrack();
        }
    }

    /**
     *
     */
    public void playNextTrack() {
        if (hasNextTrack()) {
            setTrack(mCurrentTrackPosition + 1);
            loadAndStartTrack();
        }
    }

    /**
     *
     * @return
     */
    public boolean hasNextTrack() {
        return mCurrentTrackPosition < mTracks.size() - 1;
    }

    /**
     *
     * @return
     */
    public boolean hasPreviousTrack() {
        return mCurrentTrackPosition > 0;
    }

    /**
     * Sets the position of the current track being played.
     *
     * @param position the position percentage of the track.
     */
    public void setPlaybackPosition(int position) {
        mMediaPlayer.seekTo(position);
    }

    /**
     * Gets the current playback position.
     *
     * @return the current position in milliseconds
     */
    public int getCurrentPlaybackPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     *
     *
     * @param fragmentManager
     * @param isModal
     */
    private void displayMediaPlayer(FragmentManager fragmentManager, boolean isModal) {
        Log.v(LOG_TAG, "===== displayMediaPlayer()");

        boolean isSmallScreen = false;
        MediaPlayerFragment mediaPlayerFragment = new MediaPlayerFragment();

        if (isModal) {
            // The device is using a large layout, so show the fragment as a dialog
            mediaPlayerFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(android.R.id.content, mediaPlayerFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    /**
     *
     *
     * @param tracks
     * @param position
     * @param fragmentManager
     * @param isModal
     */
    public void playAndDisplayTrack(ArrayList<TrackParcelable> tracks, int position, FragmentManager fragmentManager, boolean isModal) {
        // Play the track via the service.
        setTrackList(tracks);
        setTrack(position);
        playTrack();

        // Display track player modal.
        displayMediaPlayer(fragmentManager, isModal);
    }


    /**
     *
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void displayNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(mCurrentTrack.name)
                .setContentText(mCurrentTrack.artist);

        // Create an explicit intent for the activity to open.
        Intent resultsIntent = new Intent(this, MediaPlayerActivity.class);

        // Create artificial backstack to direct the user.
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this)
                .addNextIntent(resultsIntent);

        // Show notification.
        PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(123, notificationBuilder.build());
    }

}
