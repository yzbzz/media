package com.yzbzz.media.bean;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class AudioEntity {

    public float beginTime;
    public float endTime;
    public boolean canRead = true;

    public AudioEntity(float beginTime, float endTime) {
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public AudioEntity(float beginTime, float endTime, boolean canRead) {
        this(beginTime, endTime);
        this.canRead = canRead;
    }

    public static AudioEntity create(float beginTime, float endTime) {
        return new AudioEntity(beginTime, endTime);
    }

    public static AudioEntity create(float beginTime, float endTime,
                                     boolean canRead) {
        return new AudioEntity(beginTime, endTime, canRead);
    }

    @Override
    public String toString() {
        return "AudioBean{" + beginTime + " " + endTime + " " + canRead + "}";
    }

}
