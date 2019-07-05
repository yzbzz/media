package com.yzbzz.media.data;

import android.text.TextUtils;

import com.yzbzz.media.library.bean.AudioBean;
import com.yzbzz.media.library.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzbzz on 2019-07-05.
 */
public class AudioBeanFactory {

    public static List<AudioBean> getAudioBeans(String lastTime, long durationTime) {

        List<AudioBean> items = getAudioBeans();

        int size = items.size();

        AudioBean lastItem = items.get(size - 1);

        if (!TextUtils.isEmpty(lastTime)) {
            AudioBean audioBean = AudioBean.create(lastItem.endTime, lastTime, false);
            items.add(audioBean);
        }

        DateUtils.calculateTime(items, durationTime);

        return items;
    }

    public static List<AudioBean> getAudioBeans() {
        List<AudioBean> items = new ArrayList<>();

        AudioBean audioBean0 = AudioBean.create("00:00:02.988", "00:00:05.178");
        AudioBean audioBean1 = AudioBean.create("00:00:05.363", "00:00:07.970");
        AudioBean audioBean2 = AudioBean.create("00:00:08.155", "00:00:09.303");
        AudioBean audioBean3 = AudioBean.create("00:00:09.488", "00:00:10.637");
        AudioBean audioBean4 = AudioBean.create("00:00:12.613", "00:00:14.970");
        AudioBean audioBean5 = AudioBean.create("00:00:17.905", "00:00:19.220");
        AudioBean audioBean6 = AudioBean.create("00:00:19.405", "00:00:21.512");
        AudioBean audioBean7 = AudioBean.create("00:00:21.697", "00:00:22.970");
        AudioBean audioBean8 = AudioBean.create("00:00:24.530", "00:00:27.012");
        AudioBean audioBean9 = AudioBean.create("00:00:27.197", "00:00:28.137");
        AudioBean audioBean10 = AudioBean.create("00:00:28.322", "00:00:29.803");

        items.add(audioBean0);
        items.add(audioBean1);
        items.add(audioBean2);
        items.add(audioBean3);
        items.add(audioBean4);
        items.add(audioBean5);
        items.add(audioBean6);
        items.add(audioBean7);
        items.add(audioBean8);
        items.add(audioBean9);
        items.add(audioBean10);
        return items;
    }
}
