package com.yzbzz.media.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class MediaUtils {

    /**
     * 获取时间长度
     */
    public static int getFilePlayTime(Context context, File file) {
        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(file.toString()));
            //使用Date格式化播放时间mediaPlayer.getDuration()
            int duration = mediaPlayer.getDuration();
            mediaPlayer.release();
            return duration;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
