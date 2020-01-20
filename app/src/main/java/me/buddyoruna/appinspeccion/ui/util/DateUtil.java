package me.buddyoruna.appinspeccion.ui.util;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    private static SimpleDateFormat smf;
    private static String formatDefault = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static String formatDefault2 = "yyyy-MM-dd'T'HH:mm:ss";
    private static String formatFecha = "dd/MM/yyyy";

    public static long getDateHour() {
        return new Date().getTime();
    }

    public static Date getDateNow() {
        return new Date();
    }

    public static String LongToFechaHora(String fechaLong, String format) {
        String newDate = "";
        try {
            long dateLong = Long.parseLong(fechaLong);
            Date date = new Date(dateLong);
            format = format.equalsIgnoreCase("") ? formatFecha : format;
            smf = new SimpleDateFormat(format, new Locale("es", "pe"));
            newDate = smf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static Date formatISOToDateTZ(String fecha) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat(formatDefault, new Locale("es", "pe"));
        df.setTimeZone(tz);
        try {
            return df.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatISOToDateT(Date date) {
        DateFormat df = new SimpleDateFormat(formatDefault2, new Locale("es", "pe"));
        try {
            return df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date formatISOToDateT(String fecha) {
        DateFormat df = new SimpleDateFormat(formatDefault2, new Locale("es", "pe"));
        try {
            return df.parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getNowFormatISO() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        // Quoted "Z" to indicate UTC, no timezone offset
        DateFormat df = new SimpleDateFormat(formatDefault, new Locale("es", "pe"));
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        return nowAsISO;
    }

    public static String LongToFecha(long dateLong) {
        String newDate = "";
        try {
            Date date = new Date(dateLong);
            smf = new SimpleDateFormat(formatFecha, new Locale("es", "pe"));
            newDate = smf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String getDateNowFormat(String format) {
        String newDate = "";
        try {
            Date date = new Date();
            format = format.equalsIgnoreCase("") ? formatFecha : format;
            smf = new SimpleDateFormat(format, new Locale("es", "pe"));
            newDate = smf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static Date stringToDate(String fecha, @Nullable String format) {
        Date newDate = null;
        try {
            format = TextUtils.isEmpty(format) ? formatFecha : format;
            smf = new SimpleDateFormat(format, new Locale("es", "pe"));
            newDate = smf.parse(fecha);
        } catch (Exception e) {
            newDate = new Date();
        }
        return newDate;
    }

    public static String dateToString(Date date, String format) {
        String newDate = "";
        if (date == null) return newDate;
        try {
            format = format.equalsIgnoreCase("") ? formatFecha : format;
            smf = new SimpleDateFormat(format, new Locale("es", "pe"));
            newDate = smf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String longToHourMinute(long diff) {
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        String newDate = diffHours + "'h' " + diffMinutes + "'min'";
        return newDate;
    }

}
