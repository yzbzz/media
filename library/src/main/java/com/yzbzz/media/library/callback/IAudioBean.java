package com.yzbzz.media.library.callback;

/**
 * Created by yzbzz on 2019-07-03.
 */
public interface IAudioBean<T> {

    T getBeginTime();

    T getEndTime();

    IAudioBean create(T beingTime, T endTime);
}
