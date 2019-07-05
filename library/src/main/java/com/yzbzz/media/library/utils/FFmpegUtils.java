package com.yzbzz.media.library.utils;

import android.content.Context;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.yzbzz.media.library.callback.Callback;

/**
 * Created by yzbzz on 2019-07-02.
 */
public class FFmpegUtils {

    private static boolean LOADED;


    public static boolean isLoaded() {
        return LOADED;
    }

    public static void load(Context context, final Callback<String> callback) {
        try {
            FFmpeg.getInstance(context).loadBinary(new FFmpegLoadBinaryResponseHandler() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess() {
                    LOADED = true;
                    callback.onSuccess("FFmpeg lib load success");
                }

                @Override
                public void onFailure() {
                    LOADED = false;
                    callback.onFailure(new Exception("Failed to loaded FFmpeg lib"));
                }

                @Override
                public void onFinish() {

                }
            });
        } catch (Exception e) {
            LOADED = false;
            callback.onFailure(e);
        }
    }


    public static void executeCmd(Context context, String[] cmd, final Callback<String> callback) {
        if (!isLoaded()) {
            callback.onFailure(new Exception("FFmpeg not loaded, Please Call FFmpegUtils.Load"));
        }
        try {
            FFmpeg.getInstance(context).execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    callback.onSuccess(message);
                }

                @Override
                public void onProgress(String message) {

                }

                @Override
                public void onFailure(String message) {
                    callback.onFailure(new Exception(message));
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFinish() {
                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }
}
