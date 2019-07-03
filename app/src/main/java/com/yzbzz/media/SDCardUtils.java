package com.yzbzz.media;

import android.os.Environment;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class SDCardUtils {

    public static String SD_PATH = Environment.getExternalStorageDirectory().getPath();

    public static String MEDIA_PATH = SD_PATH + "/x_demo";

}
