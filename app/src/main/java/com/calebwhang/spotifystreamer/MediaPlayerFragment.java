package com.calebwhang.spotifystreamer;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.calebwhang.spotifystreamer.service.MediaPlayerService;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceConnection;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceListenerInterface;
import com.calebwhang.spotifystreamer.service.MediaPlayerServiceManager;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Media player controls which works with the MediaPlayerService that plays audio tracks and
 * displays the meta data of the track being played.
 */
public class MediaPlayerFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayerServiceListenerInterface,
        MediaPlayerService.OnTrackChangeListener, MediaPlayerService.OnTrackPlayListener,
        MediaPlayerService.OnTrackPauseListener, MediaPlayerService.OnTrackCompletionListener,
        MediaPlayerService.OnTrackPreparedListener {

    private final String LOG_TAG = MediaPlayerFragment.class.getSimpleName();

    private final String SHARE_INTENT_TYPE = "text/plain";
    private final Integer ALBUM_COVER_IMAGE_WIDTH = 1200;
    private final Integer ALBUM_COVER_IMAGE_HEIGHT = 1200;
    private final Integer SEEK_BAR_TASK_UPDATE = 1;  // in milliseconds

    // View elements.
    private TextView mArtistView;
    private TextView mAlbumView;
    private TextView mTrackView;
    private ImageView mAlbumCoverView;
    private ImageButton mNextTrackButton;
    private ImageButton mPreviousTrackButton;
    private ImageButton mPlayTrackButton;
    private ImageButton mPausePlaybackButton;
    private SeekBar mMediaSeekBar;
    private TextView mCurrentTimeView;
    private TextView mEndTimeView;

    private MediaPlayerService mMediaPlayerService;
    private MediaPlayerServiceManager mMediaPlayerServiceManager;
    private TrackParcelable mCurrentTrack;
    private Handler mHandler = new Handler();
    private ShareActionProvider mShareActionProvider;

    public MediaPlayerFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "===== onCreate()");

        super.onCreate(savedInstanceState);

        // Bind the media player service.
        mMediaPlayerServiceManager = ((SpotifyStreamerApplication) getActivity().getApplicationContext()).getServiceManager();
        mMediaPlayerServiceManager.bindService(new MediaPlayerServiceConnection(this));

        // Check if the fragment is to be displayed as a dialog.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(MediaPlayerService.INTENT_EXTRA_IS_MODAL)) {
            boolean isModal = intent.getBooleanExtra(MediaPlayerService.INTENT_EXTRA_IS_MODAL, false);
            setShowsDialog(isModal);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "===== onCreateView()");

        View rootView = inflater.inflate(R.layout.fragment_media_player, container, false);

        // Load view elements.
        mArtistView = (TextView) rootView.findViewById(R.id.track_artist_textview);
        mAlbumView = (TextView) rootView.findViewById(R.id.track_album_textview);
        mAlbumCoverView = (ImageView) rootView.findViewById(R.id.track_album_cover_imageview);
        mTrackView = (TextView) rootView.findViewById(R.id.track_track_name_textview);
        mPreviousTrackButton = (ImageButton) rootView.findViewById(R.id.track_previous_track_button);
        mNextTrackButton = (ImageButton) rootView.findViewById(R.id.track_next_track_button);
        mPlayTrackButton = (ImageButton) rootView.findViewById(R.id.track_play_track_button);
        mPausePlaybackButton = (ImageButton) rootView.findViewById(R.id.track_pause_playback_button);
        mMediaSeekBar = (SeekBar) rootView.findViewById(R.id.track_media_seekbar);
        mCurrentTimeView = (TextView) rootView.findViewById(R.id.track_current_time_textview);
        mEndTimeView = (TextView) rootView.findViewById(R.id.track_end_time_textview);

        // Set listeners.
        mMediaSeekBar.setOnSeekBarChangeListener(this);

        // Enable the menu.
        setHasOptionsMenu(true);

        // Set button click handlers.
        mPreviousTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "PREVIOUS TAPPED");
                playPreviousTrack();
            }
        });

        mNextTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "NEXT TAPPED");
                playNextTrack();
            }
        });

        mPlayTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "PLAY TAPPED");
                startPlayback();
            }
        });

        mPausePlaybackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "PAUSE TAPPED");
                pausePlayback();
            }
        });

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "===== onCreateDialog()");

        Dialog dialog = super.onCreateDialog(savedInstanceState);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(LOG_TAG, "===== onCreateOptionsMenu()");

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_media_player_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
//        mShareActionProvider.setShareIntent(createShareTrackIntent());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            // Update the current time display as the user slides the seek bar around.
            updateSeekBar(false);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.v(LOG_TAG, "===== onStartTrackingTouch()");

        stopSeekBarUpdateTask();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.v(LOG_TAG, "===== onStopTrackingTouch()");

        // Play the track from where the user slid the seek bar from.
        updateSeekBar(true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.v(LOG_TAG, "===== onCompletion()");

        renderControlButtons();
    }

    /**
     * Creates the share intent for sharing the current track being played.
     *
     * @return the constructed shared intent.
     */
    private Intent createShareTrackIntent() {
        // Construct the text to be shared.
        String shareText = String.format(getActivity().getString(R.string.format_track_share_notification),
                mCurrentTrack.name,
                mCurrentTrack.artist,
                mCurrentTrack.externalSpotifyUrl);

        // Create the share intent.
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType(SHARE_INTENT_TYPE);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        return shareIntent;
    }

    /**
     * Converts a time given in milliseconds to a human readable format.
     *
     * @param time a date/time in unixtimestamp format.
     * @return a human readable time in the format mm:ss
     */
    public String getReadableTimeDuration(long time) {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(date);
    }

    /**
     * Gets the Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // Calculating percentage
        percentage =(((double)currentSeconds) / totalSeconds) * 100;

        return percentage.intValue();
    }

    /**
     * Background runnable thread that updates the seek bar.
     */
    private Runnable mUpdateSeekBarTask = new Runnable() {
        @Override
        public void run() {
            if (mMediaPlayerService != null) {
                int currentPosition = mMediaPlayerService.getCurrentPlaybackPosition();
                int progress = getProgressPercentage(currentPosition, mCurrentTrack.previewDuration);

                // Update the seek bar and the current time.
                mMediaSeekBar.setProgress(progress);
                mCurrentTimeView.setText(getReadableTimeDuration(currentPosition));

                // Update states when track is done playing.
                if (progress == 100) {
                    stopPlayback();
                } else {
                    runSeekBarUpdateTask();
                }
            }
        }
    };

    /**
     * Starts the progress updater on the seek bar.
     */
    private void runSeekBarUpdateTask() {
        mHandler.postDelayed(mUpdateSeekBarTask, SEEK_BAR_TASK_UPDATE);
    }

    /**
     * Stop the track seek bar progress display updates.
     */
    private void stopSeekBarUpdateTask() {
        mHandler.removeCallbacks(mUpdateSeekBarTask);
    }

    /**
     * Handles updates to the progress displays when the seek bar is moved.
     *
     * @param seekToPosition whether the track should be updated to where the seek bar is.
     */
    private void updateSeekBar(boolean seekToPosition) {
        // Calculate positions.
        int currentPosition = mMediaSeekBar.getProgress();
        int position = (int) (currentPosition * mCurrentTrack.previewDuration / 100);

        if (seekToPosition) {
            // Play the track from where the seek bar is positioned.
            mMediaPlayerService.setPlaybackPosition(position);
        }

        // Display the track time from where the seek bar is positioned.
        mCurrentTimeView.setText(getReadableTimeDuration(position));

        // Run the track progress display updates.
        runSeekBarUpdateTask();
    }

    /**
     * Renders the control buttons and the different states.
     *
     * The button states are as follows:
     *   Play: Displayed when media is stopped or paused.
     *   Pause: Displayed when media is playing.
     *   Next: Clickable if a next track is available.
     *   Previous: Clickable if a previous track is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void renderControlButtons() {
        // Play/Pause states.
        if (mMediaPlayerService.isPlaying()) {
            mPlayTrackButton.setVisibility(View.GONE);
            mPausePlaybackButton.setVisibility(View.VISIBLE);
        } else {
            mPlayTrackButton.setVisibility(View.VISIBLE);
            mPausePlaybackButton.setVisibility(View.GONE);
        }

        // Next track states.
        if (!mMediaPlayerService.hasNextTrack()) {
            mNextTrackButton.setClickable(false);
            mNextTrackButton.setAlpha(MediaPlayerService.BUTTON_DISABLED_OPACITY);
        } else {
            mNextTrackButton.setClickable(true);
            mNextTrackButton.setAlpha(MediaPlayerService.BUTTON_ENABLED_OPACITY);
        }

        // Previous track states.
        if (!mMediaPlayerService.hasPreviousTrack()) {
            mPreviousTrackButton.setClickable(false);
            mPreviousTrackButton.setAlpha(MediaPlayerService.BUTTON_DISABLED_OPACITY);
        } else {
            mPreviousTrackButton.setClickable(true);
            mPreviousTrackButton.setAlpha(MediaPlayerService.BUTTON_ENABLED_OPACITY);
        }
    }

    /**
     * Handles the actions for when the player is played from the interface.
     */
    private void startPlayback() {
        mMediaPlayerService.playTrack(true);

        runSeekBarUpdateTask();
        renderControlButtons();
    }

    /**
     * Handles the actions for when the player is stopped from the interface.
     */
    private void stopPlayback() {
        stopSeekBarUpdateTask();
        renderControlButtons();
    }

    /**
     * Handles the actions for when the player is paused from the interface.
     */
    private void pausePlayback() {
        mMediaPlayerService.pauseTrack();

        stopSeekBarUpdateTask();
        renderControlButtons();
    }

    /**
     * Handles the actions for when the next track is selected in the interface.
     */
    private void playNextTrack() {
        mMediaPlayerService.playNextTrack();

        loadTrackInfo();
        runSeekBarUpdateTask();
        renderControlButtons();
    }

    /**
     * Handles the actions for when the next track is selected in the interface.
     */
    private void playPreviousTrack() {
        mMediaPlayerService.playPreviousTrack();

        loadTrackInfo();
        runSeekBarUpdateTask();
        renderControlButtons();
    }

    /**
     * Loads the track info to be displayed.
     */
    private void loadTrackInfo() {
        mCurrentTrack = mMediaPlayerService.getCurrentTrack();

        mArtistView.setText(mCurrentTrack.artist);
        mAlbumView.setText(mCurrentTrack.album);
        mTrackView.setText(mCurrentTrack.name);
        mCurrentTimeView.setText(getReadableTimeDuration(0));
        mEndTimeView.setText(getReadableTimeDuration(mCurrentTrack.previewDuration));

        // Display artist image.
        Picasso.with(getActivity())
                .load(mCurrentTrack.image)
                .resize(ALBUM_COVER_IMAGE_WIDTH, ALBUM_COVER_IMAGE_HEIGHT)
                .error(getActivity().getResources().getDrawable(R.mipmap.ic_launcher))
                .into(mAlbumCoverView);

        // Display the correct controls.
        renderControlButtons();

        // Update the share intent with the current track.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareTrackIntent());
        }
    }

    @Override
    public void onServiceConnected(MediaPlayerService mediaPlayerService) {
        mMediaPlayerService = mediaPlayerService;

        // Set as listener for the callback interfaces.
        mMediaPlayerService.setOnTrackChangeListener(this);
        mMediaPlayerService.setOnTrackPlayListener(this);
        mMediaPlayerService.setOnTrackPauseListener(this);
        mMediaPlayerService.setOnTrackCompletionListener(this);
        mMediaPlayerService.setOnTrackPreparedListener(this);

        // Get track info/state.
        mCurrentTrack = mMediaPlayerService.getCurrentTrack();

        // Run the track progress display updates.
        runSeekBarUpdateTask();

        // Load the track information into the view.
        loadTrackInfo();
    }

    @Override
    public void onTrackChange() {
        Log.v(LOG_TAG, "===== onTrackChange()");

        loadTrackInfo();
    }

    @Override
    public void onTrackPause() {
        Log.v(LOG_TAG, "===== onTrackPause()");

        renderControlButtons();
    }

    @Override
    public void onTrackPlay() {
        Log.v(LOG_TAG, "===== onTrackPlay()");

        renderControlButtons();
    }

    @Override
    public void onTrackCompletion() {
        Log.v(LOG_TAG, "===== onTrackCompletion()");

        renderControlButtons();
    }

    @Override
    public void onTrackPrepared() {
        Log.v(LOG_TAG, "===== onTrackPrepared()");

        renderControlButtons();
    }
}
