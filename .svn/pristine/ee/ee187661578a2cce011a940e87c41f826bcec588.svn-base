package com.jinr.core.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author danding 2014-8-11
 * @名称：TimeUtil.java
 * @描述：时间转化类
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {
    private static SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    private static SimpleDateFormat mDateHourFormat = new SimpleDateFormat("HH:mm");

    /**
     * 设置帖子发表时间
     *
     * @param oldTime
     * @param currentDate
     * @return
     */
    public static String getTimeStr(Date oldTime, Date currentDate) {
        long time1 = currentDate.getTime();
        long time2 = oldTime.getTime();
        long time = (time1 - time2) / 1000;
        if (time >= 0 && time < 60) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time > 3600 * 24) {
            return time / (3600 * 24) + "天前";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sdf.format(oldTime);
        }
    }

    /**
     * 判断时间间隔
     *
     * @return
     */
    public static long getTimeInt(long oldTime) {
        if (oldTime == 0) {
            return 7;
        }
        long time = (System.currentTimeMillis() - oldTime) / (1000 * 3600 * 24);
        return time;
    }

    /**
     * 获取时间差
     *
     * @param oldTime
     * @param currentDate
     * @return
     */
    public static long getDiffTime(long oldTime, long currentDate) {
        return (oldTime - currentDate);
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = strToDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 将字符串转化为时间
     *
     * @param str
     * @return
     */
    public static Date strToDate(String str) {
        // sample：Tue May 31 17:46:55 +0800 2011
        // E：周 MMM：字符串形式的月，如果只有两个M，表示数值形式的月 Z表示时区（＋0800）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date result = null;
        try {
            result = sdf.parse(str);
        } catch (Exception e) {
        }
        return result;

    }

    /**
     * @param ld
     * @return 2014-9-19
     *
     * @描述：将毫秒值转为时间
     */
    public static String second2Time(long ld) {
        Date date = new Date(ld);
        // 标准日历系统类
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        // java.text.SimpleDateFormat，设置时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        // 得到毫秒值转化的时间
        String time = format.format(gc.getTime());
        return time;
    }

    /**
     * 获取当前日期和时间
     *
     * @return String
     */
    @SuppressLint("SimpleDateFormat")
    public static String getNowDate() {
        Date date = new Date();
        String str = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        str = df.format(date);
        return str;
    }

    /**
     * @param time
     * @return
     *
     * @描述：时间格式化
     * @date：2014-6-26
     */
    public static String formatDateTime(long time) {
        if (0 == time) {
            return "";
        }
        return mDateFormat.format(new Date(time));
    }

    public static String formatHourDateTime(long time) {
        if (0 == time) {
            return "";
        }
        return mDateHourFormat.format(new Date(time * 1000));
    }

    public static String getNowHour() {
        String timeNow = new SimpleDateFormat("HH", Locale.CHINA).format(new Date());
        return timeNow;
    }

    /**
     * @return 显示问候语
     */
    public static String getGreetings() {
        String greeting = "";
        int timeNow = Integer.valueOf(getNowHour());
        if (timeNow < 12) {
            greeting = "早上好,";
        } else if (timeNow < 14) {
            greeting = "中午好,";
        } else if (timeNow < 18) {
            greeting = "下午好,";
        } else {
            greeting = "晚上好,";
        }
        return greeting;
    }

    /**
     * 比较两个时间大小
     *
     * @return 小于0时time1<time2,等于0时相等,大于0时time1>time2
     */
    public static int compareTime(long time1, long time2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        try {
            c1.setTime(sdf.parse(sdf.format(new Date(time1 * 1000))));
            c2.setTime(sdf.parse(sdf.format(new Date(time2 * 1000))));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return c1.compareTo(c2);
    }

    /**
     * 判断是不是今天 @author Ysw created at 2017/3/24 16:56
     * 传入的 时间  是时间戳
     */
    public static boolean IsToday(long day, long currentTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar pre = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        try {
            Date predate = getDateFormat().parse(format.format(new Date(currentTime * 1000)));
            Date date = getDateFormat().parse(format.format(new Date(day * 1000)));
            pre.setTime(predate);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是不是昨天 @author Ysw created at 2017/3/24 16:56
     * 传入的 时间  是时间戳
     */
    public static boolean IsYesterday(long day, long currentTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar pre = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        try {
            Date predate = getDateFormat().parse(format.format(new Date(currentTime * 1000)));
            Date date = getDateFormat().parse(format.format(new Date(day * 1000)));
            pre.setTime(predate);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是不是明天 @author Ysw created at 2017/3/24 16:56
     * 传入的 时间  是时间戳
     */
    public static boolean IsTomorrow(long day, long currentTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar pre = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        try {
            Date predate = getDateFormat().parse(format.format(new Date(currentTime * 1000)));
            Date date = getDateFormat().parse(format.format(new Date(day * 1000)));
            pre.setTime(predate);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
            if (diffDay == 1) {
                return true;
            }
        }
        return false;
    }

    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<>();

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }

}
