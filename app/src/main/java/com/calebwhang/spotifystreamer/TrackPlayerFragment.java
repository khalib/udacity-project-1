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
public class TrackPlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

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
            }
        });

        mPauseTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "PAUSE TAPPED");
                mMediaPlayer.pause();
                mIsMediaPlaying = false;
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
        Log.v(LOG_TAG, "======================= onProgressChanged");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.v(LOG_TAG, "======================= onStartTrackingTouch");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.v(LOG_TAG, "======================= onStopTrackingTouch");
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
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // Calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

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

            mHandler.postDelayed(this, SEEK_BAR_TASK_UPDATE);
        }
    };

}
