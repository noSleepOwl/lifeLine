package com.github.vipulasri.timelineview.sample.Unit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class UTime {
    private UTime() {

    }

    public static LocalDateTime formatDateTimeToLocal(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static String formatDateTime(Date date) {
        return dateToLocal(date).format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    }

    public static LocalDateTime dateToLocal(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

}
