package com.telegram_bot_interactive.common.wrappers;


import com.telegram_bot_interactive.common.enums.DatetimeUnit;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.TimeZones;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeWrapper {

    public static final String dateTimeFormat1 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static Date now() {
        return new Date();
    }

    public static Date defaultDate() { return Date.from(Instant.EPOCH); }

    public static Date fromString(String dateStr, String format) {
        try {
            return new SimpleDateFormat(format).parse(dateStr);
        } catch (ParseException e) {
            return defaultDate();
        }

    }
    // Asia/Bangkok
    public static Date fromString(String dateStr, String format, String targetTimezone) {
        try {
            var sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(TimeZone.getTimeZone(targetTimezone));
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            return defaultDate();
        }

    }

    public static String now(String format) {
        return new SimpleDateFormat(format).format(now());
    }

    public static String toString(Date date,String format) { return format(date, format); }

    public static String getTimestampMillis() {
        return String.valueOf(System.currentTimeMillis());
    }

    /*private static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }*/

    public static String format(Date date, String format) {
        var sdf =  new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+07:00"));
        return    sdf.format(date);
    }

    public static int compare(Date d1, Date d2) {
        return d1.compareTo(d2);
    }

    public static boolean isBeforeOther(Date date, Date other){
        return compare(date, other) < 0;
    }

    public static boolean isAfterOther(Date date, Date other){
        return compare(date, other) > 0;
    }

    public static boolean isEqualToOther(Date date, Date other){
        return compare(date, other) == 0;
    }

    public static boolean isBeforeOrEqualOther(Date date, Date other) {
        return isBeforeOther(date, other) || isEqualToOther(date, other);
    }

    public static Date addDate(Date date, int value, DatetimeUnit unit) {
        return switch (unit){
            case YEAR -> DateUtils.addYears(date, value);
            case MONTH -> DateUtils.addMonths(date, value);
            case DAY -> DateUtils.addDays(date, value);
            case HOUR -> DateUtils.addHours(date, value);
            case MINUTE -> DateUtils.addMinutes(date, value);
            case SECOND -> DateUtils.addSeconds(date, value);
            case MILLISECOND -> DateUtils.addMilliseconds(date, value);
        };
    }
}
