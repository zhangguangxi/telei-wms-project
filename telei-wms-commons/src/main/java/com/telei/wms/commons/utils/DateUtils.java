package com.telei.wms.commons.utils;

import com.telei.infrastructure.component.commons.utils.TimeZoneUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public abstract class DateUtils {
    public  static final String yyyyMMdd = "yyyy-MM-dd";
    public  static final String HHmmss = "HH:mm:ss";
    public  static final String yyyyMMddHHmmss = String.join(" ", yyyyMMdd, HHmmss);
    public  static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public  static final String RANGE_TIME_START = " 00:00:00";
    public  static final String RANGE_TIME_END= " 23:59:59";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    /**
     * 解析日期字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Date parse(String date, String pattern) {
        return DateTime.parse(date, DateTimeFormat.forPattern(pattern)).toDate();
    }

    /**
     * 格式化日期对象
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        return new DateTime(date).toString(pattern);
    }

    /**
     * 格式化当前日期对象
     *
     * @param pattern
     * @return
     */
    public static String formatNow(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 使用 yyyy-MM-dd 格式进行格式化
     *
     * @param date
     * @return
     */
    public static String formatUseYmd(Date date) {
        return format(date, yyyyMMdd);
    }

    /**
     * 使用 HH:mm:ss 格式进行格式化
     *
     * @param date
     * @return
     */
    public static String formatUseHms(Date date) {
        return format(date, HHmmss);
    }

    /**
     * 使用 yyyy-MM-dd HH:mm:ss 格式进行格式化
     *
     * @param date
     * @return
     */
    public static String formatUseYmdHms(Date date) {
        return format(date, yyyyMMddHHmmss);
    }

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(yyyyMMdd);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(yyyyMMddHHmmss);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(yyyyMMdd, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public static final Date dateTime(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String dateTime() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        return parseDate(str.toString());
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 描述:获取上一个月时间.
     *
     * @return
     */
    public static Date getPreMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        SimpleDateFormat dft = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        Date time = cal.getTime();
        return time;
    }

    /**
     * 当前UTC 时间
     *
     * @return
     */
    public static Date nowWithUTC() {
        return TimeZoneUtils.nowWithUTC();
    }

    public static Date nowWithDate() {
        return new Date();
    }

    /**
     * 昨天UTC 时间
     *
     * @return
     */
    public static Date leftWithDate() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //把日期往后增加一天.整数往后推,负数往前移动
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime();
    }

}


