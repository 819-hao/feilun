package com.seeease.flywheel.serve.base;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * <p>时间工具包</p>
 *
 * @date : 2021-11-04 20:22
 **/
public class DateUtils {
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String yyyyMMdd = "yyyyMMdd";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取格式化后的日期
     *
     * @param dateFormat
     * @return
     */
    public static String getNowDate(String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(getNowDate());
    }


    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String parseStrToDate(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
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
        return DateFormatUtils.format(now, YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTime(Date date) {

        return DateFormatUtils.format(date, YYYY_MM_DD_HH_MM_SS);
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
     * 时间-天数
     * 2020-09-22-y
     */
    public static String timeSubtractDay(Date time, Integer day) {
        SimpleDateFormat df = new SimpleDateFormat(DateUtils.YYYY_MM_DD);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.DATE, 0 - day);
        return df.format(cal.getTime());
    }

    /**
     * 获取日期时间差，返回天数
     */
    public static Integer getDay(Date startDate, Date endDate) {
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endDate);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);
        Long day = (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);
        return Integer.valueOf(day.intValue());
    }

    public static Integer dateDiff(String startTime, String endTime, String formatter) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatter);
        LocalDate date = LocalDate.parse(startTime, dateTimeFormatter);
        LocalDate date1 = LocalDate.parse(endTime, dateTimeFormatter);
        return (int) ChronoUnit.DAYS.between(date, date1);
    }

    /**
     * 日期 + 时间
     *
     * @param startTime
     * @param calendarTimeType
     * @param number
     * @return
     */
    public static Date dateAddTime(Date startTime, int calendarTimeType, int number) {
        //设置生效时间为一小时后
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        cal.add(calendarTimeType, number);// 24小时制
        return cal.getTime();
    }

    /**
     * 时间戳 转换 年月日 时分秒格式
     *
     * @param time
     * @return
     */
    public static Date timestampTransDateStr(Long time) {
        if (time == null || time == 0) {
            return null;
        }
        return new Date(time);
    }

    /**
     * 获取距离今天 addDay 天后的某个时间点
     *
     * @param addDay
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date getTimeOfAddDay(Date date, Integer addDay, int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, addDay);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        return cal.getTime();
    }

    /**
     * 比较时间大小
     *
     * @param targetTime 预计时间
     * @param nowTime    当前时间
     * @return
     */
    public static boolean compareTime(Date targetTime, Date nowTime) {
        return nowTime.getTime() > targetTime.getTime();
    }

    /**
     * 获取某天开始时间
     *
     * @param date       日期
     * @param offsetDays 偏移量
     * @return
     */
    public static Date getStartDate(Date date, int offsetDays) {
        Date baseDate = org.apache.commons.lang3.time.DateUtils.addDays(date, offsetDays);
        return org.apache.commons.lang3.time.DateUtils.setMinutes(org.apache.commons.lang3.time.DateUtils.setSeconds(org.apache.commons.lang3.time.DateUtils.setHours(baseDate, 0), 0), 0);
    }

    /**
     * 获取某天开始结束时间
     *
     * @param date       日期
     * @param offsetDays 偏移量
     * @return
     */
    public static Date getEndDate(Date date, int offsetDays) {
        Date baseDate = org.apache.commons.lang3.time.DateUtils.addDays(date, offsetDays);
        return org.apache.commons.lang3.time.DateUtils.setMinutes(org.apache.commons.lang3.time.DateUtils.setSeconds(org.apache.commons.lang3.time.DateUtils.setHours(baseDate, 23), 59), 59);
    }


    /**
     * 校验 时间
     *
     * @param insuranceCardTime
     * @param warrantyDate
     * @return
     */
    public static Boolean checkOutTime(String insuranceCardTime, String warrantyDate) {
        Date date = parseStrToDate(warrantyDate, "yyyy-MM");
        if (StringUtils.isEmpty(insuranceCardTime) || date == null) return false;
        return stepMonth(date, Integer.parseInt(insuranceCardTime)).after(new Date());
    }

    public static void main(String[] args) throws ParseException {
//        String time = "2022-3-2 11:00:00";
//        SimpleDateFormat df = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS);
//        Date parse = df.parse(time);
//        Date date = new Date();
//        System.out.println(compareTime(parse, date));
//
//        Date date1 = dateAddTime(date, Calendar.HOUR, 2);
//        System.out.println(df.format(date1));
        String startTime = "2022-05-01 00:00:00";
        String toTime = "2022-09-01 00:00:00";
        System.out.println(dateDiff(startTime, toTime, YYYY_MM_DD_HH_MM_SS));
//        Date endTime = new Date();
//        //System.out.println(isEffectiveDate(parseStrToDate(toTime), parseStrToDate(startTime), endTime));
//        Date date = DateUtils.parseStrToDate("2021-10", "yyyy-MM");
//        System.out.println(DateUtils.stepMonth(date, 12).before(new Date()));
    }

    public static Date stepMonth(Date sourceDate, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.MONTH, month);

        return c.getTime();
    }

    public static Date stepHour(Date sourceDate, int hour) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.HOUR, hour);

        return c.getTime();
    }

    public static Date stepMinute(Date sourceDate, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(sourceDate);
        c.add(Calendar.MINUTE, minute);

        return c.getTime();
    }

    public static Date parseStrToDate(String date, String format) {
        try {
            if (StringUtils.isEmpty(format) || StringUtils.isEmpty(date))
                return null;
            SimpleDateFormat df = new SimpleDateFormat(format);
            return df.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseStrToDate(String date) {
        try {
            if (StringUtils.isEmpty(date)) {
                return null;
            }
            SimpleDateFormat df = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS);
            return df.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取前n天的开始时间
     * @param n
     * @return
     */
    public static LocalDateTime getStartTime(int n) {
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.minus(n, ChronoUnit.DAYS).with(LocalTime.MIN);
    }

    /**
     * 获取当前结束时间
     * @return
     */
    public static LocalDateTime getEndTimeOfToday() {
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.with(LocalTime.MAX);
    }

    /**
     * 去除日期的时间单位
     * @param date
     * @return
     */
    public static Date clearTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 清除时分秒，只保留日期
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
