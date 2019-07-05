package com.yzbzz.media;

import android.os.Environment;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class SDCardUtils {

    public static String SD_PATH = Environment.getExternalStorageDirectory().getPath();

    public static String ROOT_PATH = SD_PATH + "/x_demo";

    public static String FFMPEG_PATH = ROOT_PATH + "/ffmpeg";

    public static String MEDIA_PATH = ROOT_PATH + "/media";

}
