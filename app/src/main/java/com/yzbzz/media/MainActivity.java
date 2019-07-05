package com.yzbzz.media;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yzbzz.media.library.utils.FileUtils;
import com.yzbzz.media.ui.FFmpegActivity;
import com.yzbzz.media.ui.MediaActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnInit;
    private Button btnFFmpeg;
    private Button btnMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInit = findViewById(R.id.btn_init);
        btnFFmpeg = findViewById(R.id.btn_ffmpeg);
        btnMedia = findViewById(R.id.btn_media);

        btnInit.setOnClickListener(this);
        btnFFmpeg.setOnClickListener(this);
        btnMedia.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String permissions = Manifest.permission.WRITE_EXTERNAL_STORAGE;

            if (this.checkSelfPermission(permissions) != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[]{permissions, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, REQUEST_CODE_CONTACT);
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_init) {
            createFile();
        } else if (id == R.id.btn_ffmpeg) {
            startActivity(new Intent(this, FFmpegActivity.class));
        } else if (id == R.id.btn_media) {
            startActivity(new Intent(this, MediaActivity.class));
        }
    }

    private void createFile() {

        boolean result = true;

        List<File> fileList = new ArrayList<>();
        fileList.add(new File(SDCardUtils.FFMPEG_PATH_BLANK));
        fileList.add(new File(SDCardUtils.FFMPEG_PATH_RECORD));
        fileList.add(new File(SDCardUtils.FFMPEG_PATH_DUBBING));
        fileList.add(new File(SDCardUtils.FFMPEG_PATH_DUBBING_ALL));

        fileList.add(new File(SDCardUtils.MEDIA_PATH_BLANK));
        fileList.add(new File(SDCardUtils.MEDIA_PATH_RECORD));
        fileList.add(new File(SDCardUtils.MEDIA_PATH_DUBBING));
        fileList.add(new File(SDCardUtils.MEDIA_PATH_DUBBING_ALL));

        for (File file : fileList) {
            if (!file.exists()) {
                result = result && file.mkdirs();
             }
        }

        Toast.makeText(this, result ? "初始化成功" : "初始化失败", Toast.LENGTH_SHORT).show();

        FileUtils.copyFilesFromRaw(this,R.raw.video,SDCardUtils.ROOT_PATH,"4594.mp4");
    }
}
