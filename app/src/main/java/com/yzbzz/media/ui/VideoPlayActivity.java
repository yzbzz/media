package com.yzbzz.media.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.VideoView;

import com.yzbzz.media.R;

public class VideoPlayActivity extends AppCompatActivity {

    private VideoView vvMediaPlay;

    public static Intent getMediaActivityIntent(Context context, String path) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("videoUrl", path);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        vvMediaPlay = findViewById(R.id.vv_media_play);

        init();
    }

    private void init() {
        String path = getIntent().getStringExtra("videoUrl");
        vvMediaPlay.setVideoPath(path);
        MediaController mediaController = new MediaController(this);
        vvMediaPlay.setMediaController(mediaController);
        mediaController.setMediaPlayer(vvMediaPlay);
    }

    @Override
    public void finish() {
        super.finish();
        if (vvMediaPlay != null) {
            vvMediaPlay = null;
        }
    }
}
