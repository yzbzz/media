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
import com.yzbzz.media.library.bean.AudioBean;
import com.yzbzz.media.library.callback.Callback;
import com.yzbzz.media.library.utils.DateUtils;
import com.yzbzz.media.library.utils.FFmpegCmdUtils;
import com.yzbzz.media.library.utils.FFmpegUtils;
import com.yzbzz.media.library.utils.FileUtils;
import com.yzbzz.media.library.utils.MediaUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FFmpegActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnMerge;
    private Button btnDubbingPart;
    private Button btnDubbingAll;
    private Button btnPlay;

    private TextView tvMediaPath;
    private TextView tvMediaSecond;

    private TextView tvCombinePath;
    private TextView tvCombineSecond;

    private static String VIDEO_PATH = SDCardUtils.MEDIA_PATH + "/4594.mp4";
    private static String FFMPEG_PATH = SDCardUtils.MEDIA_PATH + "/ffmpeg/";

    private static String DUBBING_FOLDER = "dubbing/";
    private static String DUBBING_ALL_FOLDER = "dubbing_all/";

    private static String RECORD_FOLDER = "record/";
    private static String BLANK_FOLDER = "blank/";


    private ProgressDialog progressDialog;

    private long beginTime;
    private long endTime;

    private Handler myHandler;

    private List<String> fileList = new ArrayList<>();

    private static int FAIL_ACTION = 0;
    private static int EXTRACT_AUDIO_ACTION = FAIL_ACTION++;
    private static int EXTRACT_VIDEO_ACTION = FAIL_ACTION++;
    private static int CLIP_ACTION = FAIL_ACTION++;
    private static int COMBINE_AUDIO_ACTION = FAIL_ACTION++;
    private static int COMBINE_MEDIA_ACTION = FAIL_ACTION++;
    private static int DONE_ACTION = FAIL_ACTION++;

    private static String lastTime;

    private static int COMBINE_ORIGINAL = 1;
    private static int COMBINE_PART = 2;
    private static int COMBINE_ALL = 3;

    private static int COMBINE = COMBINE_ORIGINAL;

    private int recodeCount = 1;
    private int blankCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

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

        myHandler = new MyHandler();

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
            String videoUrl = FFMPEG_PATH + "combine_audio.mp3";
            String audioUrl = FFMPEG_PATH + "out_put.mp4";
            startActivity(ExoPlayerActivity.getExoPlayerIntent(this, videoUrl, audioUrl));
        }
    }

    private void begin() {
        beginTime = System.currentTimeMillis();
        progressDialog.show();
        clearData();
        myHandler.sendEmptyMessage(EXTRACT_AUDIO_ACTION);
    }

    private void combineMedia() {
        String videoUrl = FFMPEG_PATH + "combine_audio.mp3";
        String audioUrl = FFMPEG_PATH + "out_put.mp4";
        String[] cmd = FFmpegCmdUtils.combineMedia(videoUrl, audioUrl, FFMPEG_PATH + "combine.mp4");
        FFmpegUtils.executeCmd(this, cmd, new Callback<String>() {
            @Override
            public void onSuccess(String s) {
                myHandler.sendEmptyMessage(DONE_ACTION);
            }

            @Override
            public void onFailure(Exception error) {
                myHandler.sendEmptyMessage(FAIL_ACTION);
            }
        });
    }

    private void clearData() {
        recodeCount = 1;
        blankCount = 1;
        fileList.clear();
        FileUtils.deleteFile(new File(FFMPEG_PATH), "dubbing", "dubbing_all");
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
            }

            @Override
            public void onFailure(Exception error) {
                myHandler.sendEmptyMessage(FAIL_ACTION);
            }
        });
    }

    private void clipAudio() {
        AudioBean audioBean0 = AudioBean.create("00:00:02.988", "00:00:05.178");
        AudioBean audioBean1 = AudioBean.create("00:00:05.363", "00:00:07.970");// 2
        AudioBean audioBean2 = AudioBean.create("00:00:08.155", "00:00:09.303");// 3
        AudioBean audioBean3 = AudioBean.create("00:00:09.488", "00:00:10.637");
        AudioBean audioBean4 = AudioBean.create("00:00:12.613", "00:00:14.970");
        AudioBean audioBean5 = AudioBean.create("00:00:17.905", "00:00:19.220");
        AudioBean audioBean6 = AudioBean.create("00:00:19.405", "00:00:21.512");// 7
        AudioBean audioBean7 = AudioBean.create("00:00:21.697", "00:00:22.970");
        AudioBean audioBean8 = AudioBean.create("00:00:24.530", "00:00:27.012");// 9
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
            AudioBean audioBean11 = AudioBean.create(audioBean10.endTime, lastTime, false);
            items.add(audioBean11);
        }

        DateUtils.calculateTime(items, 200);

        clipAudios(items, 0);

    }


    private void clipAudios(final List<AudioBean> items, final int count) {
        if (count >= items.size()) {
            myHandler.sendEmptyMessage(COMBINE_AUDIO_ACTION);
            return;
        } else {
            AudioBean item = items.get(count);

            final boolean canRead = item.canRead;

            DecimalFormat decimalFormat = new DecimalFormat("000");//确定格式，把1转换为001
            String suffix;

            if (canRead) {
                suffix = "u_00" + decimalFormat.format(recodeCount) + ".mp3";
            } else {
                suffix = "b_00" + decimalFormat.format(blankCount) + ".mp3";
            }

            String recodeName = RECORD_FOLDER + suffix;
            String blankName = BLANK_FOLDER + suffix;

            final String audioName = canRead ? recodeName : blankName;

            String[] cmd = FFmpegCmdUtils.cutAudio(FFMPEG_PATH + "out_put.mp3", item.beginTime, item.endTime, FFMPEG_PATH + audioName);
            FFmpegUtils.executeCmd(this, cmd, new Callback<String>() {
                @Override
                public void onSuccess(String msg) {
                    if (canRead) {
                        recodeCount++;
                    } else {
                        blankCount++;
                    }
                    fileList.add(audioName);
                    clipAudios(items, count + 1);
                }

                @Override
                public void onFailure(Exception e) {
                    myHandler.sendEmptyMessage(FAIL_ACTION);
                }
            });
        }
    }

    private void combineAudio() {
        if (COMBINE == COMBINE_PART) {
            resetFileList(FFMPEG_PATH + DUBBING_FOLDER);
        } else if (COMBINE == COMBINE_ALL) {
            resetFileList(FFMPEG_PATH + DUBBING_ALL_FOLDER);
        }

        for(String item: fileList) {
            Log.v("lhz", item);
        }

        FileUtils.writeAudioInfo(FFMPEG_PATH + "audioList.txt", fileList);
        String[] cmd = FFmpegCmdUtils.concatAudiosByFile(FFMPEG_PATH + "audioList.txt", FFMPEG_PATH + "combine_audio.mp3");
        FFmpegUtils.executeCmd(this, cmd, new Callback<String>() {
            @Override
            public void onSuccess(String msg) {
                myHandler.sendEmptyMessage(COMBINE_MEDIA_ACTION);
            }

            @Override
            public void onFailure(Exception e) {
                myHandler.sendEmptyMessage(FAIL_ACTION);
            }
        });
    }

    private void resetFileList(String path) {
        File dubbingFile = new File(path);
        File[] files = dubbingFile.listFiles();
        int length = files.length;

        int size = fileList.size();
        for (int i = 0; i < size; i++) {
            String fileName = fileList.get(i);
            File tempFile = new File(fileName);
            for (int j = 0; j < length; j++) {
                File duFile = files[j];
                if (tempFile.getName().equalsIgnoreCase(duFile.getName())) {
                    String newFileName = duFile.getParentFile().getName() + "/" + duFile.getName();
                    fileList.set(i, newFileName);
                }
            }
        }
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
                updateTime();
                extractorVideo();
            } else if (what == CLIP_ACTION) { // 切割音频
                clipAudio();
            } else if (what == COMBINE_AUDIO_ACTION) { // 合成音频
                combineAudio();
            } else if (what == COMBINE_MEDIA_ACTION) { // 合成音视频
                updateCombineTime();
                combineMedia();
            } else if (what == DONE_ACTION) { // 完成
                endTime = System.currentTimeMillis();
                showToast("合成完成 耗时: " + (endTime - beginTime) + "秒");
                dismiss();
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
        String outFile = FFMPEG_PATH + "combine_audio.mp3";
        long time = MediaUtils.getFilePlayTime(this, new File(outFile));
        tvCombinePath.setText(outFile);
        tvCombineSecond.setText(String.valueOf(time));
    }
}
