package com.search.suggestion.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date related functionality
 */
public class DateUtil {
    /**
     * Get date +- days
     * @param days
     * @return
     */
    public static java.util.Date getDate(int days) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * Get String format of Date
     * @param s
     * @param date
     * @return
     */
    public static String getStringDate(String s, Date date) {
        DateFormat dateFormat = new SimpleDateFormat(s);
        String file = dateFormat.format(date);
        return file;
    }
}
