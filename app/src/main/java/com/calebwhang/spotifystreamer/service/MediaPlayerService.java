package com.calebwhang.spotifystreamer.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.calebwhang.spotifystreamer.MediaPlayerActivity;
import com.calebwhang.spotifystreamer.MediaPlayerFragment;
import com.calebwhang.spotifystreamer.R;
import com.calebwhang.spotifystreamer.SpotifyStreamerApplication;
import com.calebwhang.spotifystreamer.TrackParcelable;
import com.calebwhang.spotifystreamer.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Service class that handles media audio tracks being played.  It also display player controls
 * along with meta data of the audio track that is being played.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final String LOG_TAG = MediaPlayerService.class.getSimpleName();

    public static final String INTENT_EXTRA_IS_MODAL = "intent_extra_is_modal";
    public static final String INTENT_EXTRA_NOTIFICATION_CLICK = "intent_extra_notification_click";

    public static final String ACTION_NOTIFICATION_PLAY_TRACK = "action_notification_play_track";
    public static final String ACTION_NOTIFICATION_PAUSE_TRACK = "action_notification_pause_track";
    public static final String ACTION_NOTIFICATION_NEXT_TRACK = "action_notification_next_track";
    public static final String ACTION_NOTIFICATION_PREVIOUS_TRACK = "action_notification_previous_track";

    public static final int NOTIFICATION_NOW_PLAYING_VIEW_TYPE_COLLAPSED = 1;
    public static final int NOTIFICATION_NOW_PLAYING_VIEW_TYPE_EXPANDED = 2;

    public static final Float BUTTON_DISABLED_OPACITY = (float) 0.35;
    public static final Float BUTTON_ENABLED_OPACITY = (float) 1.0;

    private final IBinder mMediaPlayerBinder = new MediaPlayerServiceBinder();
    private final int NOTIFICATION_CURRENT_TRACK_ID = 123;
    private MediaPlayer mMediaPlayer;
    private ArrayList<TrackParcelable> mTracks;
    private TrackParcelable mCurrentTrack;
    private int mCurrentTrackPosition = 0;

    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private RemoteViews mNotificationContentView;
    private RemoteViews mNotificationBigContentView;

    // Listeners
    private OnTrackChangeListener mOnTrackChangeListener;
    private OnTrackPlayListener mOnTrackPlayListener;
    private OnTrackPauseListener mOnTrackPauseListener;
    private OnTrackCompletionListener mOnTrackCompletionListener;
    private OnTrackPreparedListener mOnTrackPreparedListener;

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

    /**
     * Interface definition for a callback to be invoked when a track selection change has been made.
     */
    public interface OnTrackChangeListener {
        void onTrackChange();
    }

    /**
     * Interface definition for a callback to be invoked when playback of a media source been paused.
     */
    public interface OnTrackPauseListener {
        void onTrackPause();
    }

    /**
     * Interface definition for a callback to be invoked when playback of a media source has started.
     */
    public interface OnTrackPlayListener {
        void onTrackPlay();
    }

    /**
     * Interface definition for a callback to be invoked when playback of a media source has completed.
     */
    public interface OnTrackCompletionListener {
        void onTrackCompletion();
    }

    /**
     * Interface definition for a callback to be invoked when the selected media source is loaded.
     */
    public interface OnTrackPreparedListener {
        void onTrackPrepared();
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
        updateNotifications();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v(LOG_TAG, "===== onError() ERROR ERROR ERROR");

        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.v(LOG_TAG, "===== onPrepared()");

        mp.start();
        showNotification();

        // Invoke callback.
        if (mOnTrackPreparedListener != null) {
            mOnTrackPreparedListener.onTrackPrepared();
        }
    }

    /**
     * Handles the incoming intents from the notification media controller.
     *
     * @param intent
     */
    private void handleIntent(Intent intent) {
        Log.v(LOG_TAG, "===== handleIntent()");

        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_PLAY_TRACK)) {
                Log.v(LOG_TAG, "===== Notification PLAY Tapped");

                // Play track actions.
                playTrack(true);
            } else if (intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_PAUSE_TRACK)) {
                Log.v(LOG_TAG, "===== Notification PAUSE Tapped");

                // Pause track actions.
                pauseTrack();
            } else if(intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_NEXT_TRACK)) {
                Log.v(LOG_TAG, "===== Notification NEXT Tapped");

                // Next track actions.
                playNextTrack();

            } else if(intent.getAction().equalsIgnoreCase(ACTION_NOTIFICATION_PREVIOUS_TRACK)) {
                Log.v(LOG_TAG, "===== Notification PREVIOUS Tapped");

                // Previous track actions.
                playPreviousTrack();
            }

            updateNotifications();
        }
    }

    /**
     * Sets the list of tracks queued up to be played in the playlist.
     *
     * @param tracks a list of TrackParcelable objects.
     */
    public void setTrackList(ArrayList<TrackParcelable> tracks) {
        Log.v(LOG_TAG, "===== setTrackList()");

        mTracks = tracks;
    }

    /**
     * Set the current track to play.
     *
     * @param trackPosition the track index of the track list.
     */
    public void setTrack(int trackPosition) {
        Log.v(LOG_TAG, "===== setTrack()");

        mCurrentTrackPosition = trackPosition;
        mCurrentTrack = mTracks.get(mCurrentTrackPosition);
    }

    /**
     * Gets the current track being played.
     */
    public TrackParcelable getCurrentTrack() {
        Log.v(LOG_TAG, "===== getCurrentTrack()");

        return mCurrentTrack;
    }

    /**
     * Overloads the playTrack() method with the default state of the resume parameter.
     */
    public void playTrack() {
        Log.v(LOG_TAG, "===== playTrack()");

        playTrack(false);
    }

    /**
     * Plays the current track.
     *
     * @param resume whether or not a track is to be resumed or started from the beginning.
     */
    public void playTrack(boolean resume) {
        Log.v(LOG_TAG, "===== playTrack(PARAM)");

        if (!resume) {
            // Start track from the beginning.
            loadAndStartTrack();
        } else {
            Log.v(LOG_TAG, "Resuming playback.");

            // Resuming from a paused state.
            mMediaPlayer.start();
            updateNotifications();
        }

        // Invoke callback.
        if (mOnTrackPlayListener != null) {
            mOnTrackPlayListener.onTrackPlay();
        }
    }

    /**
     * Loads the track from the remote source to be played.
     */
    public void loadAndStartTrack() {
        Log.v(LOG_TAG, "===== loadAndStartTrack()");

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
     * Pauses the currently playing track
     */
    public void pauseTrack() {
        Log.v(LOG_TAG, "===== pauseTrack()");

        mMediaPlayer.pause();
        updateNotifications();

        // Invoke callback.
        if (mOnTrackPauseListener != null) {
            mOnTrackPauseListener.onTrackPause();
        }
    }

    /**
     * Plays the previous track in the track list if one exitst.
     */
    public void playPreviousTrack() {
        Log.v(LOG_TAG, "===== playPreviousTrack()");

        if (hasPreviousTrack()) {
            setTrack(mCurrentTrackPosition - 1);

            // Invoke callback.
            if (mOnTrackChangeListener != null) {
                mOnTrackChangeListener.onTrackChange();
            }

            loadAndStartTrack();
        }
    }

    /**
     * Plays the previous track in the track list if one exitst.
     */
    public void playNextTrack() {
        Log.v(LOG_TAG, "===== playNextTrack()");

        if (hasNextTrack()) {
            setTrack(mCurrentTrackPosition + 1);

            // Invoke callback.
            if (mOnTrackChangeListener != null) {
                mOnTrackChangeListener.onTrackChange();
            }

            loadAndStartTrack();
        }
    }

    /**
     * Checks if the media player is currently playing a track.
     */
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    /**
     * Checks if there is another song in the track list after the current track that is
     * being played.
     *
     * @return boolean whether a track exists after the currently playing track.
     */
    public boolean hasNextTrack() {
        return mCurrentTrackPosition < mTracks.size() - 1;
    }

    /**
     * Checks if there is another song in the track list before the current track that is
     * being played.
     *
     * @return boolean whether a track exists before the currently playing track.
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
     * Displays the media player controls and the meta data of the track being played.
     *
     * @param fragmentManager
     * @param isModal
     */
    public static void displayMediaPlayer(FragmentManager fragmentManager, boolean isModal) {
        Log.v(LOG_TAG, "===== displayMediaPlayer()");

        MediaPlayerFragment mediaPlayerFragment = new MediaPlayerFragment();

        if (isModal) {
            // The device is using a large layout, so show the fragment as a dialog
            mediaPlayerFragment.show(fragmentManager, "dialog");
        } else {
            Log.v(LOG_TAG, "===== getShowsDialog(): " + Boolean.toString(mediaPlayerFragment.getShowsDialog()));

            Context appContext = SpotifyStreamerApplication.getStaticApplicationContext();
            Intent mediaPlayerIntent = new Intent(appContext, MediaPlayerActivity.class);
            mediaPlayerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mediaPlayerIntent.putExtra(INTENT_EXTRA_IS_MODAL, false);
            appContext.startActivity(mediaPlayerIntent);
        }
    }

    /**
     * Loads a track list and plays the selected track.
     *
     * @param tracks a list of tracks.
     * @param position the selected position of the track list.
     * @param fragmentManager the fragment manager that is rendering the media player controls.
     * @param isModal whether the media player controls are to be displayed as a modal or a full screen.
     */
    public void playAndDisplayTrack(ArrayList<TrackParcelable> tracks, int position, FragmentManager fragmentManager, boolean isModal) {
        Log.v(LOG_TAG, "===== playAndDisplayTrack()");

        // Play the track via the service.
        setTrackList(tracks);
        setTrack(position);
        playTrack();

        // Display track player modal.
        displayMediaPlayer(fragmentManager, isModal);
    }


    /**
     * Shows the meta data of the current track that is playing in the notification drawer.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void showNotification() {
        Log.v(LOG_TAG, "===== showNotification()");

        // Set visibility according to the user preference.
        int visibility = Notification.VISIBILITY_PUBLIC;
        if (Utility.getLockScreenNotificationSettings(getApplicationContext()) == false) {
            visibility = Notification.VISIBILITY_SECRET;
        }

        // Set the notification click behavior.
        Intent mediaPlayerIntent = new Intent(this, MediaPlayerActivity.class);
        mediaPlayerIntent.putExtra(INTENT_EXTRA_IS_MODAL, false);

//        Intent mediaPlayerIntent = new Intent(this, SearchArtistActivity.class);
//        mediaPlayerIntent.setAction(Intent.ACTION_MAIN);
//        mediaPlayerIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        mediaPlayerIntent.putExtra(INTENT_EXTRA_NOTIFICATION_CLICK, true);

        PendingIntent playerPendingIntent = PendingIntent.getActivity(this, 0, mediaPlayerIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification.
        mNotification = new NotificationCompat.Builder(getApplicationContext())
                .setVisibility(visibility)
                .setContentTitle(mCurrentTrack.artist)
                .setContentText(mCurrentTrack.name)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setContentIntent(playerPendingIntent)
                .build();

        mNotificationContentView = getRemoteView(NOTIFICATION_NOW_PLAYING_VIEW_TYPE_COLLAPSED, mNotification);
        mNotification.contentView = mNotificationContentView;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mNotificationBigContentView = getRemoteView(NOTIFICATION_NOW_PLAYING_VIEW_TYPE_EXPANDED, mNotification);
            mNotification.bigContentView = mNotificationBigContentView;
        }

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_CURRENT_TRACK_ID, mNotification);
    }

    /**
     * Updates the notification states and metadata displayed.
     */
    public void updateNotifications() {
        Log.v(LOG_TAG, "===== updateNotifications()");

        renderControlButtons(mNotificationContentView);
        renderControlButtons(mNotificationBigContentView);
        mNotificationManager.notify(NOTIFICATION_CURRENT_TRACK_ID, mNotification);
    }

    /**
     * Constructs the custom remote views of the service notifications.
     *
     * @param viewType the remote view to be used.
     * @param notification the notification instance.
     * @return the constructed remote view.h
     */
    private RemoteViews getRemoteView(int viewType, Notification notification) {
        Log.v(LOG_TAG, "===== getRemoteView(): " + Integer.toString(viewType));

        int layout = 0;

        // Load the layout from the type.
        if (viewType == NOTIFICATION_NOW_PLAYING_VIEW_TYPE_COLLAPSED) {
            layout = R.layout.notification_now_playing;
        } else if (viewType == NOTIFICATION_NOW_PLAYING_VIEW_TYPE_EXPANDED) {
            layout = R.layout.notification_now_playing_expanded;
        }

        RemoteViews notificationView = new RemoteViews(getPackageName(), layout);

        // Set the track meta data in the remote view.
        notificationView.setTextViewText(R.id.notification_track_textview, mCurrentTrack.name);
        notificationView.setTextViewText(R.id.notification_artist_textview, mCurrentTrack.artist);
        notificationView.setTextViewText(R.id.notification_album_textview, mCurrentTrack.album);

        // Load the album art.
        Picasso.with(getApplicationContext())
                .load(mCurrentTrack.image)
                .resize(100, 100)
                .error(R.mipmap.ic_launcher)
                .into(notificationView, R.id.notification_album_image_imageview, NOTIFICATION_CURRENT_TRACK_ID, notification);

        // Set control button states.
        renderControlButtons(notificationView);

        // Set the intents for the media controls in the notification.
        Intent intent = new Intent(getApplicationContext(), MediaPlayerService.class);

        intent.setAction(ACTION_NOTIFICATION_PLAY_TRACK);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_play_imagebutton, pendingIntent);

        intent.setAction(ACTION_NOTIFICATION_PAUSE_TRACK);
        pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        notificationView.setOnClickPendingIntent(R.id.notification_pause_imagebutton, pendingIntent);

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
     *
     * @param remoteViews the remote view to change the button states.
     */
    private void renderControlButtons(RemoteViews remoteViews) {
        Log.v(LOG_TAG, "===== renderControlButtons()");

        // The previous track button state.
        if (hasPreviousTrack()) {
            remoteViews.setInt(R.id.notification_previous_imagebutton, "setVisibility", View.VISIBLE);
            remoteViews.setInt(R.id.notification_previous_imagebutton_disabled, "setVisibility", View.GONE);
        } else {
            remoteViews.setInt(R.id.notification_previous_imagebutton, "setVisibility", View.GONE);
            remoteViews.setInt(R.id.notification_previous_imagebutton_disabled, "setVisibility", View.VISIBLE);
        }

        // The play/pause track button states.
        if (isPlaying()) {
            remoteViews.setInt(R.id.notification_play_imagebutton, "setVisibility", View.GONE);
            remoteViews.setInt(R.id.notification_pause_imagebutton, "setVisibility", View.VISIBLE);
        } else {
            remoteViews.setInt(R.id.notification_play_imagebutton, "setVisibility", View.VISIBLE);
            remoteViews.setInt(R.id.notification_pause_imagebutton, "setVisibility", View.GONE);
        }

        // The next track button state.
        if (hasNextTrack()) {
            remoteViews.setInt(R.id.notification_next_imagebutton, "setVisibility", View.VISIBLE);
            remoteViews.setInt(R.id.notification_next_imagebutton_disabled, "setVisibility", View.GONE);
        } else {
            remoteViews.setInt(R.id.notification_next_imagebutton, "setVisibility", View.GONE);
            remoteViews.setInt(R.id.notification_next_imagebutton_disabled, "setVisibility", View.VISIBLE);
        }
    }

    /**
     * Register a callback to be invoked when the media track has changed.
     *
     * @param listener the callback that will be run.
     */
    public void setOnTrackChangeListener(OnTrackChangeListener listener) {
        mOnTrackChangeListener = listener;
    }

    /**
     * Register a callback to be invoked when the media track has been played.
     *
     * @param listener the callback that will be run.
     */
    public void setOnTrackPlayListener(OnTrackPlayListener listener) {
        mOnTrackPlayListener = listener;
    }

    /**
     * Register a callback to be invoked when the media track has been paused.
     *
     * @param listener the callback that will be run.
     */
    public void setOnTrackPauseListener(OnTrackPauseListener listener) {
        mOnTrackPauseListener = listener;
    }

    /**
     * Register a callback to be invoked when the media track has finished playing.
     *
     * @param listener the callback that will be run.
     */
    public void setOnTrackCompletionListener(OnTrackCompletionListener listener) {
        mOnTrackCompletionListener = listener;
    }

    /**
     * Register a callback to be invoked when the media track has finished loading.
     *
     * @param listener the callback that will be run.
     */
    public void setOnTrackPreparedListener(OnTrackPreparedListener listener) {
        mOnTrackPreparedListener = listener;
    }

}
