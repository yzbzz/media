package com.yzbzz.media.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yzbzz.media.R;
import com.yzbzz.media.SDCardUtils;
import com.yzbzz.media.data.AudioBeanFactory;
import com.yzbzz.media.library.bean.AudioBean;
import com.yzbzz.media.library.bean.AudioEntity;
import com.yzbzz.media.library.utils.DateUtils;
import com.yzbzz.media.library.utils.FileUtils;
import com.yzbzz.media.library.utils.MediaUtils;
import com.yzbzz.media.library.utils.WavMergeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnMerge;
    private Button btnDubbingPart;
    private Button btnDubbingAll;
    private Button btnPlay;

    private TextView tvMediaPath;
    private TextView tvMediaSecond;

    private TextView tvCombinePath;
    private TextView tvCombineSecond;

    private static String VIDEO_PATH = SDCardUtils.MEDIA_PATH + "/4594.mp4";
    private static String MEDIA_PATH = SDCardUtils.MEDIA_PATH + "/media/";

    private static String DUBBING_FOLDER = "dubbing/";
    private static String DUBBING_ALL_FOLDER = "dubbing_all/";

    private static String DUBBING_WAV_FOLDER = "dubbing_wav/";

    private static String RECORD_FOLDER = "record/";
    private static String BLANK_FOLDER = "blank/";

    public static String RECORD_PATH = MEDIA_PATH + RECORD_FOLDER;
    public static String BLANK_PATH = MEDIA_PATH + BLANK_FOLDER;


    private ProgressDialog progressDialog;

    private long beginTime;
    private long endTime;

    private static String lastTime;

    List<AudioEntity> audioEntities = new ArrayList<>();

    private static int COMBINE_ORIGINAL = 1;
    private static int COMBINE_PART = 2;
    private static int COMBINE_ALL = 3;

    private static int COMBINE = COMBINE_ORIGINAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        btnMerge = findViewById(R.id.btn_merge);
        btnDubbingPart = findViewById(R.id.btn_dubbing);
        btnDubbingAll = findViewById(R.id.btn_dubbing_all);
        btnPlay = findViewById(R.id.btn_play);

        tvMediaPath = findViewById(R.id.tv_media_path);
        tvMediaSecond = findViewById(R.id.tv_media_second);

        tvCombinePath = findViewById(R.id.tv_combine_path);
        tvCombineSecond = findViewById(R.id.tv_combine_second);

        btnMerge.setOnClickListener(this);
        btnDubbingPart.setOnClickListener(this);
        btnDubbingAll.setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("合成中...");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_merge) {
            COMBINE = COMBINE_ORIGINAL;
            begin();
        } else if (id == R.id.btn_dubbing) {
            COMBINE = COMBINE_PART;
            begin();
        } else if (id == R.id.btn_dubbing_all) {
            COMBINE = COMBINE_ALL;
            begin();
        } else if (id == R.id.btn_play) {
            String videoUrl = MEDIA_PATH + "combine_audio.wav";
            String audioUrl = MEDIA_PATH + "out_put.mp4";
            startActivity(ExoPlayerActivity.getExoPlayerIntent(this, videoUrl, audioUrl));
        }
    }

    private void begin() {
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
    }

    private void clearData() {
        audioEntities.clear();
        FileUtils.deleteFile(new File(MEDIA_PATH), "dubbing", "dubbing_all");
    }

    private void extractMedia() {
        MediaUtils.extractAudio(VIDEO_PATH, MEDIA_PATH + "out_put.mp3");
        MediaUtils.extractVideo(VIDEO_PATH, MEDIA_PATH + "out_put.mp4");

        updateTime();
    }

    private void clipAudio() {


        List<AudioBean> items = AudioBeanFactory.getAudioBeans(lastTime,100);

        clipAudios(items);
    }

    private void clipAudios(final List<AudioBean> items) {

        AudioEntity audioEntity;
        for (AudioBean audioBean : items) {
            float beginTime = DateUtils.getTime(audioBean.beginTime);
            float endTime = DateUtils.getTime(audioBean.endTime);
            audioEntity = new AudioEntity(beginTime, endTime, audioBean.canRead);
            audioEntities.add(audioEntity);
        }

        MediaUtils.cutAudios(RECORD_PATH, MEDIA_PATH + "out_put.mp3", audioEntities, RECORD_PATH, BLANK_PATH);


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

        if (COMBINE == COMBINE_PART) {
            String path = MEDIA_PATH + DUBBING_FOLDER;
            File dubbingFile = new File(path);
            File[] dubbinFile = dubbingFile.listFiles();
            int length = dubbinFile.length;

            for (int j = 0; j < length; j++) {
                File duFile = dubbinFile[j];
                String du = duFile.getName();

                String duM = du.substring(0, du.indexOf("."));
                String newFileName = duFile.getParentFile().getAbsolutePath() + "/" + duM + ".wav";

                if (du.endsWith(".mp3")) {
                    MediaUtils.decodeAudio(duFile.getAbsolutePath(), newFileName);
                }
            }

            resetFileList(path, files);

        } else if (COMBINE == COMBINE_ALL) {
            String path = MEDIA_PATH + DUBBING_ALL_FOLDER;
            File dubbingFile = new File(path);
            File[] dubbinFile = dubbingFile.listFiles();
            int length = dubbinFile.length;

            for (int j = 0; j < length; j++) {
                File duFile = dubbinFile[j];
                String du = duFile.getName();
                String duM = du.substring(0, du.indexOf("."));
                String newFileName = duFile.getParentFile().getAbsolutePath() + "/" + duM + ".wav";

                if (du.endsWith(".mp3")) {
                    MediaUtils.decodeAudio(duFile.getAbsolutePath(), newFileName);
                }
            }
            resetFileList(path, files);
        }

        try {
            Thread.sleep(300);
            WavMergeUtil.mergeWav(files, new File(MEDIA_PATH + "combine_audio.wav"));
            updateCombineTime();
            dismiss();
            endTime = System.currentTimeMillis();
            showToast("合并完成,耗时: " + (endTime - beginTime) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetFileList(String path, List<File> items) {
        File dubbingFile = new File(path);
        File[] files = dubbingFile.listFiles();
        int length = files.length;

        int size = items.size();
        for (int i = 0; i < size; i++) {
            File fileName = items.get(i);
            String fn = fileName.getName();

            for (int j = 0; j < length; j++) {
                File duFile = files[j];
                String du = duFile.getName();

                if (fn.equalsIgnoreCase(du)) {
                    Log.v("lhz","fn: " + fn+" du: " + du);
                    items.set(i, duFile);
                }
            }

        }
//        for (int i = 0; i < size; i++) {
//            File fileName = items.get(i);
//            String fn = fileName.getName();
//            String fnM = fn.substring(0, fn.indexOf("."));
//
//            for (int j = 0; j < length; j++) {
//                File duFile = files[j];
//                String du = duFile.getName();
//                if (du.endsWith(".mp3")) {
//                    String duM = du.substring(0, du.indexOf("."));
//                    if (fnM.equalsIgnoreCase(duM)) {
//                        String newFileName = duFile.getParentFile().getAbsolutePath() + "/" + duM + ".wav";
//
//                        MediaUtils.decodeAudio(duFile.getAbsolutePath(), newFileName);
//                        items.set(i, new File(newFileName));
//                    }
//                }
//            }
//        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
