package com.yzbzz.media.bean;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class AudioBean {

    public String beginTime;
    public String endTime;
    public boolean canRead = true;

    public AudioBean(String beginTime, String endTime) {
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public AudioBean(String beginTime, String endTime, boolean canRead) {
        this(beginTime, endTime);
        this.canRead = canRead;
    }

    public static AudioBean create(String beginTime, String endTime) {
        return new AudioBean(beginTime, endTime);
    }

    public static AudioBean create(String beginTime, String endTime,
                                   boolean canRead) {
        return new AudioBean(beginTime, endTime, canRead);
    }

    @Override
    public String toString() {
        return "AudioBean{"+ beginTime + " " +endTime + " "+ canRead +"}";
    }
}
