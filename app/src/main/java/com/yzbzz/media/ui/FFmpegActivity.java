package com.yzbzz.media.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yzbzz.media.R;
import com.yzbzz.media.SDCardUtils;
import com.yzbzz.media.bean.AudioBean;
import com.yzbzz.media.library.callback.Callback;
import com.yzbzz.media.library.utils.FFmpegCmdUtils;
import com.yzbzz.media.library.utils.FFmpegUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FFmpegActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnMerge;
    private Button btnPlay;

    private TextView tvMediaPath;
    private TextView tvMediaSecond;

    private TextView tvCombinePath;
    private TextView tvCombineSecond;

    private static String VIDEO_PATH = SDCardUtils.MEDIA_PATH + "/4594.mp4";
    private static String FFMPEG_PATH = SDCardUtils.OUT_PUT_PATH + "/";


    private ProgressDialog progressDialog;

    private long beginTime;
    private long endTime;

    private Handler myHandler;

    private List<String> fileList = new ArrayList<>();

    private static int FAIL_ACTION = 0;
    private static int EXTRACT_AUDIO_ACTION = FAIL_ACTION++;
    private static int EXTRACT_VIDEO_ACTION = FAIL_ACTION++;
    private static int CLIP_ACTION = FAIL_ACTION++;
    private static int COMBINE_ACTION = FAIL_ACTION++;
    private static int DONE_ACTION = FAIL_ACTION++;

    private static String lastTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg);

        btnMerge = findViewById(R.id.btn_merge);
        btnPlay = findViewById(R.id.btn_play);

        tvMediaPath = findViewById(R.id.tv_media_path);
        tvMediaSecond = findViewById(R.id.tv_media_second);

        tvCombinePath = findViewById(R.id.tv_combine_path);
        tvCombineSecond = findViewById(R.id.tv_combine_second);

        btnMerge.setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        myHandler = new MyHandler();

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
            beginTime = System.currentTimeMillis();
            myHandler.sendEmptyMessage(EXTRACT_AUDIO_ACTION);
        } else if (id == R.id.btn_play) {
            String videoUrl = FFMPEG_PATH + "combine.mp3";
            String audioUrl = FFMPEG_PATH + "out_put.mp4";
            startActivity(ExoPlayerActivity.getExoPlayerIntent(this, videoUrl, audioUrl));
        }
    }

    private void clearData() {
        fileList.clear();
        FileUtils.deleteFile(new File(FFMPEG_PATH));
    }

    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FFmpegActivity.this, msg, Toast.LENGTH_LONG).show();
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

    private void extractorAudio() {
        String outFile = FFMPEG_PATH + "out_put.mp3";
        String[] cmd = FFmpegCmdUtils.extractorAudioByMp3(VIDEO_PATH, outFile);
        FFmpegUtils.executeCmd(this, cmd, new Callback<String>() {
            @Override
            public void onSuccess(String msg) {
                myHandler.sendEmptyMessage(EXTRACT_VIDEO_ACTION);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTime();
                    }
                });
            }

            @Override
            public void onFailure(Exception error) {
                myHandler.sendEmptyMessage(FAIL_ACTION);
            }
        });
    }

    private void extractorVideo() {
        String outFile = FFMPEG_PATH + "out_put.mp4";
        String[] cmd = FFmpegCmdUtils.extractorVideo(VIDEO_PATH, outFile);
        FFmpegUtils.executeCmd(this, cmd, new Callback<String>() {
            @Override
            public void onSuccess(String msg) {
                myHandler.sendEmptyMessage(CLIP_ACTION);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateTime();
                    }
                });
            }

            @Override
            public void onFailure(Exception error) {
                myHandler.sendEmptyMessage(FAIL_ACTION);
            }
        });
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

        DateUtils.calculateTime(items);

        clipAudios(items, 0);
    }


    private void clipAudios(final List<AudioBean> items, final int count) {
        if (count >= items.size()) {
            myHandler.sendEmptyMessage(COMBINE_ACTION);
            return;
        } else {
            AudioBean item = items.get(count);

            final boolean canRead = item.canRead;

            final String canReadPath = count + ".mp3";
            final String blankPath = "blank/" + count + ".mp3";

            String[] cmd = FFmpegCmdUtils.cutAudio(FFMPEG_PATH + "out_put.mp3", item.beginTime, item.endTime, canRead ? FFMPEG_PATH + canReadPath : FFMPEG_PATH + blankPath);
            FFmpegUtils.executeCmd(this, cmd, new Callback<String>() {
                @Override
                public void onSuccess(String msg) {
                    fileList.add(canRead ? canReadPath : blankPath);
                    clipAudios(items, count + 1);
                    Log.v("lhz", "audioBean: " + item);
                }

                @Override
                public void onFailure(Exception e) {
                    myHandler.sendEmptyMessage(FAIL_ACTION);
                }
            });
        }
    }

    private void combine() {

        FileUtils.writeAudioInfo(FFMPEG_PATH + "audioList.txt", fileList);

        String[] cmd = FFmpegCmdUtils.concatAudiosByFile(FFMPEG_PATH + "audioList.txt", FFMPEG_PATH + "combine.mp3");
        FFmpegUtils.executeCmd(this, cmd, new Callback<String>() {
            @Override
            public void onSuccess(String msg) {
                myHandler.sendEmptyMessage(DONE_ACTION);
            }

            @Override
            public void onFailure(Exception e) {
                myHandler.sendEmptyMessage(FAIL_ACTION);
            }
        });
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == FAIL_ACTION) {
                showToast("合成失败");
                dismiss();
            } else if (what == EXTRACT_AUDIO_ACTION) { // 分离音频
                extractorAudio();
            } else if (what == EXTRACT_VIDEO_ACTION) { // 分离视频
                extractorVideo();
            } else if (what == CLIP_ACTION) { // 切割音频
                clipAudio();
            } else if (what == COMBINE_ACTION) { // 合成音频
                combine();
            } else if (what == DONE_ACTION) { // 完成
                endTime = System.currentTimeMillis();
                showToast("合成完成 耗时: " + (endTime - beginTime) + "秒");
                dismiss();
                updateCombineTime();
            }
        }
    }

    private void updateTime() {
        String outFile = FFMPEG_PATH + "out_put.mp3";
        long time = MediaUtils.getFilePlayTime(this, new File(outFile));
        lastTime = DateUtils.getTimeStr(time);
        tvMediaPath.setText(outFile);
        tvMediaSecond.setText(String.valueOf(time));
    }

    private void updateCombineTime() {
        String outFile = FFMPEG_PATH + "combine.mp3";
        long time = MediaUtils.getFilePlayTime(this, new File(outFile));
        tvCombinePath.setText(outFile);
        tvCombineSecond.setText(String.valueOf(time));
    }
}
