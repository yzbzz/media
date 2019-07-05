package com.yzbzz.media.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.yzbzz.media.R;

import java.io.File;

public class ExoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private PlayerView playerView;
    private ExoPlayer player;
    private boolean playWhenReady;
    private int currentWindow;
    private long playbackPosition;

    private boolean isPlayMedia;

    public static Intent getExoPlayerIntent(Context context, String mediaUrl) {
        Intent intent = new Intent(context, ExoPlayerActivity.class);
        intent.putExtra("mediaUrl", mediaUrl);
        return intent;
    }

    public static Intent getExoPlayerIntent(Context context, String videoUrl, String audioUrl) {
        Intent intent = new Intent(context, ExoPlayerActivity.class);
        intent.putExtra("videoUrl", videoUrl);
        intent.putExtra("audioUrl", audioUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exoplayer);
        playerView = findViewById(R.id.pv_view);

        init();
    }

    public void init() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);

        player.setPlayWhenReady(true);
//        player.seekTo(currentWindow,playbackPosition);

        MediaSource mediaSource;
        String mediaUrl = getIntent().getStringExtra("mediaUrl");
        if (!TextUtils.isEmpty(mediaUrl)) {
            mediaSource = buildMedia();
        } else {
            mediaSource = buildMergingMedia();
        }

        player.prepare(mediaSource);
        player.addListener(new Player.DefaultEventListener() {
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }

    private MediaSource buildMedia() {
        DataSource.Factory dataSourceFactory = buildDataSourceFactory(this);

        String mediaUrl = getIntent().getStringExtra("mediaUrl");

        File mediaFile = new File(mediaUrl);
        Uri audioUri = Uri.fromFile(mediaFile);

        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(audioUri);
        return mediaSource;
    }

    private MediaSource buildMergingMedia() {
        DataSource.Factory dataSourceFactory = buildDataSourceFactory(this);

        String videoUrl = getIntent().getStringExtra("videoUrl");
        File videoFile = new File(videoUrl);
        Uri videoUri = Uri.fromFile(videoFile);
        MediaSource video = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);

        String audioUrl = getIntent().getStringExtra("audioUrl");
        File audioFile = new File(audioUrl);
        Uri audioUri = Uri.fromFile(audioFile);
        MediaSource audio = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(audioUri);

        return new MergingMediaSource(video, audio);
    }

    private DataSource.Factory buildDataSourceFactory(Context context) {
        return new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getString(R.string.app_name)));
    }


    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void finish() {
        releasePlayer();
        super.finish();

    }
}
