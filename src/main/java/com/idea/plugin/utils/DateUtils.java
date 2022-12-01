package com.idea.plugin.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateUtils {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDDHHMMSSS = "yyyyMMddHHmmssS";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYMM = "yy.MM";
    public static List<String> dataFormats = Arrays.asList(YYYYMMDD, YYYY_MM_DD_HH_MM_SS, YYYYMMDDHHMMSSS, YYYY_MM_DD, YYMM);

    public static Boolean isDate(String text) {
        for (String dataFormat : dataFormats) {
            try {
                StrToLocalDateTime(text, dataFormat);
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public static String nowDate(String text) {
        for (String dataFormat : dataFormats) {
            try {
                new SimpleDateFormat(dataFormat).parse(text);
                return DateToStr(new Date(), dataFormat);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static Date LocalDateTimeToDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime DateToLocalDateTime(Date time) {
        return time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime StrToLocalDateTime(String time, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
        return LocalDateTime.parse(time, formatter);
    }

    public static String LocalDateTimeToStr(LocalDateTime time, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
        return formatter.format(time);
    }

    public static Date StrToDate(String time, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
        return LocalDateTimeToDate(LocalDateTime.parse(time, formatter));
    }

    public static String DateToStr(Date date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, Locale.US);
        return formatter.format(DateToLocalDateTime(date));
    }

    public static Calendar DateToCalendar(LocalDateTime time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()));
        return calendar;
    }

    public static Boolean isFriday(LocalDateTime time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()));
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
    }

    public static Boolean isMonthLastDay(LocalDateTime time) {
        int lastDay = time.getMonth().length(time.toLocalDate().isLeapYear());
        int day = time.getDayOfMonth();
        return lastDay == day;
    }
}
