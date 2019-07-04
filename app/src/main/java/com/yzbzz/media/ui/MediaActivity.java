package com.yzbzz.media.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yzbzz.media.R;
import com.yzbzz.media.SDCardUtils;
import com.yzbzz.media.bean.AudioBean;
import com.yzbzz.media.bean.AudioEntity;
import com.yzbzz.media.utils.DateUtils;
import com.yzbzz.media.utils.FileUtils;
import com.yzbzz.media.utils.MediaUtils;
import com.yzbzz.media.utils.WavMergeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnMerge;
    private Button btnPlay;

    private TextView tvMediaPath;
    private TextView tvMediaSecond;

    private TextView tvCombinePath;
    private TextView tvCombineSecond;

    private static String VIDEO_PATH = SDCardUtils.MEDIA_PATH + "/4594.mp4";
    private static String MEDIA_PATH = SDCardUtils.MEDIA_PATH + "/media/";

    private static String RECORD_FOLDER = "record/";
    private static String BLANK_FOLDER = "blank/";

    public static String RECORD_PATH = MEDIA_PATH + RECORD_FOLDER;
    public static String BLANK_PATH = MEDIA_PATH + BLANK_FOLDER;


    private ProgressDialog progressDialog;

    private long beginTime;
    private long endTime;

    private static int FAIL_ACTION = 0;
    private static String lastTime;

    List<AudioEntity> audioEntities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        btnMerge = findViewById(R.id.btn_merge);
        btnPlay = findViewById(R.id.btn_play);

        tvMediaPath = findViewById(R.id.tv_media_path);
        tvMediaSecond = findViewById(R.id.tv_media_second);

        tvCombinePath = findViewById(R.id.tv_combine_path);
        tvCombineSecond = findViewById(R.id.tv_combine_second);

        btnMerge.setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("合成中...");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_merge) {
            beginTime = System.currentTimeMillis();
            progressDialog.show();
            clearData();

            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    extractMedia();
                    clipAudio();
                    mixAudio();
                }
            });
        } else if (id == R.id.btn_play) {
            String videoUrl = MEDIA_PATH + "combine_audio.wav";
            String audioUrl = MEDIA_PATH + "out_put.mp4";
            startActivity(ExoPlayerActivity.getExoPlayerIntent(this, videoUrl, audioUrl));
        }
    }

    private void clearData() {
        audioEntities.clear();
        FileUtils.deleteFile(new File(MEDIA_PATH));
    }

    private void extractMedia() {
        MediaUtils.extractAudio(VIDEO_PATH, MEDIA_PATH + "out_put.mp3");
        MediaUtils.extractVideo(VIDEO_PATH, MEDIA_PATH + "out_put.mp4");

        updateTime();
    }

    private void clipAudio() {
        AudioBean audioBean0 = AudioBean.create("00:00:02.988", "00:00:05.178");
        AudioBean audioBean1 = AudioBean.create("00:00:05.363", "00:00:07.970");
        AudioBean audioBean2 = AudioBean.create("00:00:08.155", "00:00:09.303");
        AudioBean audioBean3 = AudioBean.create("00:00:09.488", "00:00:10.637");
        AudioBean audioBean4 = AudioBean.create("00:00:12.613", "00:00:14.970");
        AudioBean audioBean5 = AudioBean.create("00:00:17.905", "00:00:19.220");
        AudioBean audioBean6 = AudioBean.create("00:00:19.405", "00:00:21.512");
        AudioBean audioBean7 = AudioBean.create("00:00:21.697", "00:00:22.970");
        AudioBean audioBean8 = AudioBean.create("00:00:24.530", "00:00:27.012");
        AudioBean audioBean9 = AudioBean.create("00:00:27.197", "00:00:28.137");
        AudioBean audioBean10 = AudioBean.create("00:00:28.322", "00:00:29.803");


        List<AudioBean> items = new ArrayList<>();
        items.add(audioBean0);
        items.add(audioBean1);
        items.add(audioBean2);
        items.add(audioBean3);
        items.add(audioBean4);
        items.add(audioBean5);
        items.add(audioBean6);
        items.add(audioBean7);
        items.add(audioBean8);
        items.add(audioBean9);
        items.add(audioBean10);

        if (!TextUtils.isEmpty(lastTime)) {
            AudioBean audioBean11 = AudioBean.create(audioBean10.endTime, lastTime);
            items.add(audioBean11);
        }

        DateUtils.calculateTime(items, 200);

        clipAudios(items);
    }

    private void clipAudios(final List<AudioBean> items) {
        MediaUtils.decodeAudio(SDCardUtils.MEDIA_PATH +"/u_00007.mp3",RECORD_PATH + "/u_00007.wav");
        AudioEntity audioEntity;
        for (AudioBean audioBean : items) {
            float beginTime = DateUtils.getTime(audioBean.beginTime);
            float endTime = DateUtils.getTime(audioBean.endTime);
            audioEntity = new AudioEntity(beginTime, endTime, audioBean.canRead);
            audioEntities.add(audioEntity);
        }

        MediaUtils.cutAudios(RECORD_PATH, MEDIA_PATH + "out_put.mp3", audioEntities);

    }

    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MediaActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void dismiss() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void mixAudio() {

        Collections.sort(audioEntities);
        int size = audioEntities.size();
        if (size < 2) {
            return;
        }

        List<File> files = new ArrayList<>();
        for (AudioEntity audioBean : audioEntities) {
            files.add(new File(audioBean.path));
        }

        try {
            WavMergeUtil.mergeWav(files, new File(MEDIA_PATH + "combine_audio.wav"));
            Thread.sleep(300);
            updateCombineTime();
            dismiss();
            endTime = System.currentTimeMillis();
            showToast("合并完成,耗时: " + (endTime - beginTime) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void combine() {

    }


    private void updateTime() {
        String outFile = MEDIA_PATH + "out_put.mp3";
        long time = MediaUtils.getFilePlayTime(MediaActivity.this, new File(outFile));
        lastTime = DateUtils.getTimeStr(time);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMediaPath.setText(outFile);
                tvMediaSecond.setText(String.valueOf(time));
            }
        });
    }

    private void updateCombineTime() {
        String outFile = MEDIA_PATH + "combine_audio.wav";

        long time = MediaUtils.getFilePlayTime(MediaActivity.this, new File(outFile));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvCombinePath.setText(outFile);
                tvCombineSecond.setText(String.valueOf(time));
            }
        });
    }
}
