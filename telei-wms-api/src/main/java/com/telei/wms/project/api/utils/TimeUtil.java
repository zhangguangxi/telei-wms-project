package com.telei.wms.project.api.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Description: 获取时间工具类
 * @Auther: Dean
 * @Date: 2020/6/10 10:20
 */
public class TimeUtil {

    public static String getUtcDateTime() {
        return getTimeByZone("yyyy-MM-dd HH:mm:ss", "GMT-0");
    }

    public static String getUtcDate() {
        return getTimeByZone("yyyyMMdd", "GMT-0");
    }

    public static String getUtcTime(String format) {
        return getTimeByZone(format, "GMT-0");
    }

    public static String getDateByZone(String timeZone) {
        return getTimeByZone("yyyyMMdd", timeZone);
    }

    public static String getTimeByZone(String format, String timeZone) {
        DateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        return formatter.format(new Date());
    }
}
