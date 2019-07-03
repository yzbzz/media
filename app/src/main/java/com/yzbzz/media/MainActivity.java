package com.yzbzz.media;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.yzbzz.media.ui.FFmpegActivity;
import com.yzbzz.media.ui.MediaActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnFFmpeg;
    private Button btnMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnFFmpeg = findViewById(R.id.btn_ffmpeg);
        btnMedia = findViewById(R.id.btn_media);

        btnFFmpeg.setOnClickListener(this);
        btnMedia.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String permissions = Manifest.permission.WRITE_EXTERNAL_STORAGE;

            if (this.checkSelfPermission(permissions) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{permissions}, REQUEST_CODE_CONTACT);
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ffmpeg) {
            startActivity(new Intent(this, FFmpegActivity.class));
        } else if (id == R.id.btn_media) {
            startActivity(new Intent(this, MediaActivity.class));
        }
    }
}