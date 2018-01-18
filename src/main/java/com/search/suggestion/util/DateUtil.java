package com.search.suggestion.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nikhil on 19/1/18.
 */
public class DateUtil {
    public static java.util.Date getDate(int days) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public static String getStringDate(String s, Date date) {
        DateFormat dateFormat = new SimpleDateFormat(s);
        String file = dateFormat.format(date);
        return file;
    }
}
