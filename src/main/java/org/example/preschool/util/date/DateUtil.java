package org.example.preschool.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateUtil {

    public static String formatDateTime(Date date) {
        return date.toInstant().toString();
    }

    public static String dateToString(Date date) {
        return date.toInstant().toString();
    }

    public static String longDateToString(Long date) {
        return new Date(date).toInstant().toString();
    }

    public static Date stringToDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getNowDateFolder() {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) == Calendar.DECEMBER ? 1 : calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%04d%02d%02d", year, month, day);
    }

    public static String convertStringToDate(String date) {
        Instant instant = Instant.parse(date);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return localDateTime.format(formatter);
    }

    public static String formatStringToDate(String date, String format) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        SimpleDateFormat mongoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date parsedDate = null;
        try {
            parsedDate = inputFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
        return mongoFormat.format(parsedDate);
    }

}