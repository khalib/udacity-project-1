package com.calebwhang.spotifystreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */
public class TrackPlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    private final String LOG_TAG = TrackPlayerActivity.class.getSimpleName();

    private final Integer ALBUM_COVER_IMAGE_WIDTH = 1200;
    private final Integer ALBUM_COVER_IMAGE_HEIGHT = 1200;
    private final Integer SEEK_BAR_TASK_UPDATE = 1000;  // in milliseconds
    private final String IS_MEDIA_PLAYING_KEY = "is_media_playing";

    private TrackParcelable mTrackParcelable;
    private TextView mArtistView;
    private TextView mAlbumView;
    private TextView mTrackView;
    private ImageView mAlbumCoverView;
    private ImageButton mNextTrackButton;
    private ImageButton mPreviousTrackButton;
    private ImageButton mPlayTrackButton;
    private ImageButton mPauseTrackButton;
    private SeekBar mMediaSeekBar;
    private TextView mCurrentTimeView;
    private TextView mEndTimeView;

    private MediaPlayer mMediaPlayer;
    private boolean mIsMediaPlaying;
    private Handler mHandler = new Handler();

    public TrackPlayerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_track_player, container, false);

        if (savedInstanceState != null) {
            mIsMediaPlaying = savedInstanceState.getBoolean(IS_MEDIA_PLAYING_KEY);
        }

        // The detail Activity called via intent and get the artist ID.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mTrackParcelable = intent.getParcelableExtra(Intent.EXTRA_TEXT);
        }

        // Load view elements.
        mArtistView = (TextView) rootView.findViewById(R.id.track_artist_textview);
        mAlbumView = (TextView) rootView.findViewById(R.id.track_album_textview);
        mAlbumCoverView = (ImageView) rootView.findViewById(R.id.track_album_cover_imageview);
        mTrackView = (TextView) rootView.findViewById(R.id.track_track_name_textview);
        mPreviousTrackButton = (ImageButton) rootView.findViewById(R.id.track_previous_track_button);
        mNextTrackButton = (ImageButton) rootView.findViewById(R.id.track_next_track_button);
        mPlayTrackButton = (ImageButton) rootView.findViewById(R.id.track_play_track_button);
        mPauseTrackButton = (ImageButton) rootView.findViewById(R.id.track_pause_track_button);
        mMediaSeekBar = (SeekBar) rootView.findViewById(R.id.track_media_seekbar);
        mCurrentTimeView = (TextView) rootView.findViewById(R.id.track_current_time_textview);
        mEndTimeView = (TextView) rootView.findViewById(R.id.track_end_time_textview);

        // Initialize the media player.
        mMediaPlayer = new MediaPlayer();

        // Set listeners.
        mMediaSeekBar.setOnSeekBarChangeListener(this);
        mMediaPlayer.setOnCompletionListener(this);

        // Set button tap handlers.
        mPreviousTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "PREVIOUS TAPPED");
                mIsMediaPlaying = true;
            }
        });

        mNextTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "NEXT TAPPED");
                mIsMediaPlaying = true;
            }
        });

        mPlayTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "PLAY TAPPED");
                mMediaPlayer.start();
                mIsMediaPlaying = true;
                showPlayButton(false);
            }
        });

        mPauseTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "PAUSE TAPPED");
                mMediaPlayer.pause();
                mIsMediaPlaying = false;
                showPlayButton(true);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mArtistView.setText(mTrackParcelable.artist);
        mAlbumView.setText(mTrackParcelable.album);
        mTrackView.setText(mTrackParcelable.name);
        mCurrentTimeView.setText(getReadableTimeDuration(0));
        mEndTimeView.setText(getReadableTimeDuration(mTrackParcelable.previewDuration));

        // Display artist image.
        Picasso.with(getActivity())
                .load(mTrackParcelable.image)
                .resize(ALBUM_COVER_IMAGE_WIDTH, ALBUM_COVER_IMAGE_HEIGHT)
                .error(getActivity().getResources().getDrawable(R.mipmap.ic_launcher))
                .into(mAlbumCoverView);

        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(mTrackParcelable.previewUrl);
            mMediaPlayer.prepare();
        } catch (Exception e) {

        }

        // Prevent the media from simultaneously playing multiple times.
        if (!mIsMediaPlaying) {
            mMediaPlayer.start();
            mIsMediaPlaying = true;
            mUpdateSeekBarTask.run();
            showPlayButton(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Store activity states.
        outState.putBoolean(IS_MEDIA_PLAYING_KEY, mIsMediaPlaying);

        super.onSaveInstanceState(outState);
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
        // Stop the seek bar updates while the user slides the seek bar around.
        mHandler.removeCallbacks(mUpdateSeekBarTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Play the track from where the user slid the seek bar from.
        updateSeekBar(true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        showPlayButton(false);

        // Play the next track.
    }

    /**
     * Converts a time given in milliseconds to a human readable format.
     * @param time
     * @return A human readable time in the format mm:ss
     */
    public String getReadableTimeDuration(long time) {
        Date date = new Date(time);
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(date);
    }

    /**
     * Gets the Progress percentage
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
            int currentPosition = mMediaPlayer.getCurrentPosition();
            int progress = getProgressPercentage(currentPosition, mTrackParcelable.previewDuration);

            // Update the seek bar and the current time.
            mMediaSeekBar.setProgress(progress);
            mCurrentTimeView.setText(getReadableTimeDuration(currentPosition));

            runSeekBarUpdateTask();
        }
    };

    /**
     * Starts the progress updater on the seek bar.
     */
    private void runSeekBarUpdateTask() {
        mHandler.postDelayed(mUpdateSeekBarTask, SEEK_BAR_TASK_UPDATE);
    }

    /**
     * Handles updates to the progress displays when the seek bar is moved.
     * @param seekToPosition whether the track should be updated to where the seek bar is.
     */
    private void updateSeekBar(boolean seekToPosition) {
        // Stop the track progress display updates.
        mHandler.removeCallbacks(mUpdateSeekBarTask);

        // Calculate positions.
        int currentPosition = mMediaSeekBar.getProgress();
        int progress = (int) (currentPosition * mTrackParcelable.previewDuration / 100);

        if (seekToPosition) {
            // Play the track from where the seek bar is positioned.
            mMediaPlayer.seekTo(progress);
        }

        // Display the track time from where the seek bar is positioned.
        mCurrentTimeView.setText(getReadableTimeDuration(progress));

        // Run the track progress display updates.
        runSeekBarUpdateTask();
    }

    /**
     * Display the play button in the UI.  If false, the pause button is displayed.
     * @param show whether or not the play button should be visible.
     */
    private void showPlayButton(boolean show) {
        if (show) {
            mPlayTrackButton.setVisibility(View.VISIBLE);
            mPauseTrackButton.setVisibility(View.GONE);
        } else {
            mPlayTrackButton.setVisibility(View.GONE);
            mPauseTrackButton.setVisibility(View.VISIBLE);
        }
    }

}
