package com.github.vipulasri.timelineview.sample.Unit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Util {
    public static final DateTimeFormatter PATTERN_DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter PATTERN_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter PATTERN_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Util() {

    }

    public static LocalDateTime formatDateTimeToLocal(String dateTime) {
        return LocalDateTime.parse(dateTime, PATTERN_DATE_TIME);
    }

    public static LocalTime formatTime(String time) {
        return LocalTime.parse(time, PATTERN_TIME);
    }

    public static String formatDateTime(Date date) {
        return dateToLocal(date).format(PATTERN_DATE_TIME);
    }

    public static String nowDateTime() {
        return LocalDateTime.now().format(PATTERN_DATE_TIME);
    }

    public static String nowDate() {
        return LocalDateTime.now().toLocalDate().format(PATTERN_DATE);
    }

    public static LocalDateTime dateToLocal(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static String formatDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate().format(PATTERN_DATE);
    }

    public static String nowTime() {
        return LocalDateTime.now().toLocalTime().format(PATTERN_TIME);
    }

    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
