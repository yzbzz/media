package com.yzbzz.media;

import android.os.Environment;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class SDCardUtils {

    public static String SD_PATH = Environment.getExternalStorageDirectory().getPath();

    public static String ROOT_PATH = SD_PATH + "/x_media_demo";

    public static String BLANK_PATH = "/blank";
    public static String RECORD_PATH = "/record";
    public static String DUBBING_PATH = "/dubbing";
    public static String DUBBING_ALL_PATH = "/dubbing_all";

    public static String FFMPEG_PATH = ROOT_PATH + "/ffmpeg";
    public static String FFMPEG_PATH_BLANK = FFMPEG_PATH + BLANK_PATH;
    public static String FFMPEG_PATH_RECORD = FFMPEG_PATH + RECORD_PATH;
    public static String FFMPEG_PATH_DUBBING = FFMPEG_PATH + DUBBING_PATH;
    public static String FFMPEG_PATH_DUBBING_ALL = FFMPEG_PATH + DUBBING_ALL_PATH;

    public static String MEDIA_PATH = ROOT_PATH + "/media";
    public static String MEDIA_PATH_BLANK = MEDIA_PATH + BLANK_PATH;
    public static String MEDIA_PATH_RECORD = MEDIA_PATH + RECORD_PATH;
    public static String MEDIA_PATH_DUBBING = MEDIA_PATH + DUBBING_PATH;
    public static String MEDIA_PATH_DUBBING_ALL = MEDIA_PATH + DUBBING_ALL_PATH;

}
