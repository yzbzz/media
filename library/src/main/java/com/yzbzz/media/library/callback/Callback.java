package com.yzbzz.media.library.callback;

/**
 * Created by yzbzz on 2019-07-03.
 */
public interface Callback<T> {

    void onSuccess(T t);

    void onFailure(Exception error);
}
