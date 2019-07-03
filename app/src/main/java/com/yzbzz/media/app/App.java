package com.yzbzz.media.app;

import android.app.Application;

import com.yzbzz.media.library.callback.Callback;
import com.yzbzz.media.library.utils.FFmpegUtils;
import com.yzbzz.media.log.LogUtils;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FFmpegUtils.load(this, new Callback<String>() {
            @Override
            public void onSuccess(String s) {
                LogUtils.log(s);
            }

            @Override
            public void onFailure(Exception error) {
                LogUtils.log(error.getMessage());
            }
        });
    }
}
