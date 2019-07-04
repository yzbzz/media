package com.yzbzz.media.library.utils;

import com.yzbzz.media.library.bean.AudioBean;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by yzbzz on 2019-07-03.
 */
public class DateUtils {

    /**
     * @param items
     * @param durationTime 间隔时长
     */
    public static void calculateTime(List<AudioBean> items, long durationTime) {
        if (null != items && items.size() > 2) {
            int size = items.size();
            AudioBean temp1;
            AudioBean temp2;
            for (int i = 0; i < size - 1; i++) {
                temp1 = items.get(i);
                temp2 = items.get(i + 1);
                long tempTime = getIntervalTime(temp1.endTime, temp2.beginTime);
                if (tempTime > durationTime) {
                    items.add(AudioBean.create(temp1.endTime, temp2.beginTime,
                            false));
                } else {
                    temp2.beginTime = temp1.endTime;
                }
            }

            AudioBean firstAudioBean = items.get(0);
            String firstTime = "00:00:00.000";
            long interval = getIntervalTime(firstTime, firstAudioBean.beginTime);
            if (interval > durationTime) {
                items.add(AudioBean.create(firstTime, firstAudioBean.beginTime,
                        false));
            } else {
                firstAudioBean.beginTime = firstTime;
            }

            Collections.sort(items, new Comparator<AudioBean>() {

                @Override
                public int compare(AudioBean o1, AudioBean o2) {
                    long time = getIntervalTime(o1.beginTime, o2.beginTime);
                    if (time > 0) {
                        return -1;
                    } else if (time < 0) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
        }
    }

    public static long getIntervalTime(String timeStr1, String timeStr2) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        long intervalTime;
        try {
            Date d1 = format.parse(timeStr1);
            Date d2 = format.parse(timeStr2);
            intervalTime = d2.getTime() - d1.getTime();
        } catch (Exception e) {
            intervalTime = 0;
        }

        return intervalTime;
    }


    public static String getTimeStr(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        try {
            return format.format(time - TimeZone.getDefault().getRawOffset());
        } catch (Exception e) {
            return "";
        }
    }


    public static String addTime(String beginTime, long addTime) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        try {
            Date d1 = format.parse(beginTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d1);
            cal.add(Calendar.MILLISECOND, (int) addTime);
            return format.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static float getTime(String time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        try {
            Date date1 = format.parse(time);
            Calendar c = Calendar.getInstance();
            c.setTime(date1);

            float second = c.get(Calendar.SECOND);
            float milliSecond = c.get(Calendar.MILLISECOND);
            float sum = second + (milliSecond / 1000f);
            DecimalFormat decimalFormat = new DecimalFormat(".000");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            String p = decimalFormat.format(sum);
            return Float.valueOf(p);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
