package com.yzbzz.media.log;

import android.util.Log;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class LogUtils {

    private static final String TAG = "Yzbzz-Media";

    public static void log(String msg) {
        Log.v(TAG, msg);
    }
}
