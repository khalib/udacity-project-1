package com.calebwhang.spotifystreamer.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.View;
import android.widget.RemoteViews;
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

    private final int NOTIFICATION_CURRENT_TRACK_ID = 123;
    public static final String ACTION_NOTIFICATION_PLAY_PAUSE_TRACK = "action_notification_play_pause_track";
    public static final String ACTION_NOTIFICATION_NEXT_TRACK = "action_notification_next_track";
    public static final String ACTION_NOTIFICATION_PREVIOUS_TRACK = "action_notification_previous_track";
    public static final int NOTIFICATION_NOW_PLAYING_VIEW_TYPE_COLLAPSED = 1;
    public static final int NOTIFICATION_NOW_PLAYING_VIEW_TYPE_EXPANDED = 2;
    public static final Float BUTTON_DISABLED_OPACITY = (float) 0.35;
    public static final Float BUTTON_ENABLED_OPACITY = (float) 1.0;

    private MediaPlayer mMediaPlayer;
    private ArrayList<TrackParcelable> mTracks;
    private int mCurrentTrackPosition = 0;
    private TrackParcelable mCurrentTrack;
    private Intent mPlayerControlsIntent;
    private final IBinder mMediaPlayerBinder = new MediaPlayerServiceBinder();
    private boolean mIsPlaying;

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);

        return super.onStartCommand(intent, flags, startId);
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
     * Handles the incoming intents.
     *
     * @param intent
     */
    private void handleIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_PLAY_PAUSE_TRACK)) {
                Log.v(LOG_TAG, "===== Notification PLAY Tapped");

                // Play/Pause track actions.

                mIsPlaying = !mIsPlaying;
                showNotification(mIsPlaying);
            } else if(intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_NEXT_TRACK)) {
                Log.v(LOG_TAG, "===== Notification NEXT Tapped");

                // Next track actions.
                playNextTrack();

            } else if(intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_PREVIOUS_TRACK)) {
                Log.v(LOG_TAG, "===== Notification PREVIOUS Tapped");

                // Previous track actions.
                playPreviousTrack();
            }

            showNotification(true);
        }
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

        showNotification(true);
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
     * Shows the meta data of the current track that is playing in the notification drawer.
     *
     * @param isPlaying the playing state of the player.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification(boolean isPlaying) {
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setAutoCancel(true)
                .setContentText(mCurrentTrack.name)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        notification.contentView = getRemoteView(NOTIFICATION_NOW_PLAYING_VIEW_TYPE_COLLAPSED, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.bigContentView = getRemoteView(NOTIFICATION_NOW_PLAYING_VIEW_TYPE_EXPANDED, true);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    /**
     * Constructs the custom remote views of the service notifications.
     *
     * @param viewType
     * @param isPlaying
     * @return
     */
    private RemoteViews getRemoteView(int viewType, boolean isPlaying) {
        int layout = 0;

        // Load the layout from the type.
        if (viewType == NOTIFICATION_NOW_PLAYING_VIEW_TYPE_COLLAPSED) {
            layout = R.layout.notification_now_playing;
        } else if (viewType == NOTIFICATION_NOW_PLAYING_VIEW_TYPE_EXPANDED) {
            layout = R.layout.notification_now_playing_expanded;
        }

        RemoteViews notificationView = new RemoteViews(getPackageName(), layout);
        notificationView.setImageViewResource(R.id.notification_album_image_imageview, R.mipmap.ic_launcher);

        // Set the state of the pause/play button.
        if (isPlaying) {
            notificationView.setImageViewResource(R.id.notification_play_pause_imagebutton, android.R.drawable.ic_media_play);
        } else {
            notificationView.setImageViewResource(R.id.notification_play_pause_imagebutton, android.R.drawable.ic_media_pause);
        }

        notificationView.setTextViewText(R.id.notification_track_textview, mCurrentTrack.name);
        notificationView.setTextViewText(R.id.notification_artist_textview, mCurrentTrack.artist);
        notificationView.setTextViewText(R.id.notification_album_textview, mCurrentTrack.album);

        // Set control button states.
//        renderControlButtons(notificationView);

        // Set the intents for the media controls in the notification.
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);

        intent.setAction(ACTION_NOTIFICATION_PLAY_PAUSE_TRACK);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_play_pause_imagebutton, pendingIntent);

        intent.setAction(ACTION_NOTIFICATION_NEXT_TRACK);
        pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_next_imagebutton, pendingIntent);

        intent.setAction(ACTION_NOTIFICATION_PREVIOUS_TRACK);
        pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_previous_imagebutton, pendingIntent);

        return notificationView;
    }

    /**
     * Handles the media player controller button states.
     */
    private void renderControlButtons(RemoteViews remoteViews) {
        if (!hasPreviousTrack()) {
            remoteViews.setFloat(R.id.notification_previous_imagebutton, "setAlpha", BUTTON_DISABLED_OPACITY);
        } else {
            remoteViews.setFloat(R.id.notification_previous_imagebutton, "setAlpha", BUTTON_ENABLED_OPACITY);
        }

        if (!hasNextTrack()) {
            remoteViews.setFloat(R.id.notification_next_imagebutton, "setAlpha", BUTTON_DISABLED_OPACITY);
        } else {
            remoteViews.setFloat(R.id.notification_next_imagebutton, "setAlpha", BUTTON_ENABLED_OPACITY);
        }
    }

}
