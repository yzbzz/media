package com.yzbzz.media.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
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

        MediaSource mediaSource = buildMergingMedia();
        player.prepare(mediaSource, false, true);
    }

    private MediaSource buildMergingMedia() {
        DataSource.Factory dataSourceFactory = buildDataSourceFactory(this);

        String videoUrl = getIntent().getStringExtra("videoUrl");
        String audioUrl = getIntent().getStringExtra("audioUrl");

        File audioFile = new File(videoUrl);
        Uri audioUri = Uri.fromFile(audioFile);

        MediaSource audio = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(audioUri);

        File videoFile = new File(audioUrl);
        Uri videoUri = Uri.fromFile(videoFile);
        MediaSource video = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);

        MergingMediaSource mergingMediaSource = new MergingMediaSource(video, audio);
        return mergingMediaSource;
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
        super.finish();
        releasePlayer();
    }
}
