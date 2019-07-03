package com.yzbzz.media.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private Button btnExtractor;
    private Button btnMerge;

    private static String VIDEO_PATH = SDCardUtils.MEDIA_PATH + "/4594.mp4";
    private static String FFMPEG_PATH = SDCardUtils.OUT_PUT_PATH + "/";


    private ProgressDialog progressDialog;

    private long beginTime;
    private long endTime;

    private Handler myHandler;

    private List<String> fileList = new ArrayList<>();

    private static int FAIL_ACTION = 0;
    private static int EXTRACT_ACTION = FAIL_ACTION++;
    private static int CLIP_ACTION = FAIL_ACTION++;
    private static int COMBINE_ACTION = FAIL_ACTION++;
    private static int DONE_ACTION = FAIL_ACTION++;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg);

        btnMerge = findViewById(R.id.btn_merge);
        btnExtractor = findViewById(R.id.btn_extractor);

        btnMerge.setOnClickListener(this);
        btnExtractor.setOnClickListener(this);

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
            myHandler.sendEmptyMessage(EXTRACT_ACTION);
        } else if (id == R.id.btn_extractor) {
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
                myHandler.sendEmptyMessage(CLIP_ACTION);
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

        DateUtils.caculateTime(items);

        clipAudios(items, 0);
    }


    private void clipAudios(final List<AudioBean> items, final int count) {
        if (count >= items.size()) {
            myHandler.sendEmptyMessage(COMBINE_ACTION);
            return;
        } else {
            AudioBean item = items.get(count);

            Log.v("lhzz",item.toString() +" count: " + count);

            final boolean canRead = item.canRead;
            final String canReadPath = FFMPEG_PATH + count + ".mp3";
            final String blankPath = FFMPEG_PATH + "/blank/" + count + ".mp3";

            String[] cmd = FFmpegCmdUtils.cutAudio(FFMPEG_PATH + "out_put.mp3", item.beginTime, item.endTime, canRead ? canReadPath : blankPath);
            FFmpegUtils.executeCmd(this, cmd, new Callback<String>() {
                @Override
                public void onSuccess(String msg) {
                    fileList.add(canRead ? canReadPath : blankPath);
                    clipAudios(items, count + 1);
                }

                @Override
                public void onFailure(Exception e) {
                    myHandler.sendEmptyMessage(FAIL_ACTION);
                }
            });
        }
    }

    private void combine() {
        String[] cmd = FFmpegCmdUtils.concatAudios(fileList, FFMPEG_PATH + "combine.mp3");
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
            } else if (what == EXTRACT_ACTION) { // 分离音频
                extractorAudio();
            } else if (what == CLIP_ACTION) { // 切割音频
                clipAudio();
            } else if (what == COMBINE_ACTION) { // 合成音频
                combine();
            } else if (what == DONE_ACTION) { // 完成
                endTime = System.currentTimeMillis();
                showToast("合成完成 耗时: " + (endTime - beginTime) + "秒");
                dismiss();
            }
        }
    }
}
