package com.search.suggestion.util;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date related functionality
 */
public class Logger {
    public static void log(String str)
    {
        try {
            if(Boolean.valueOf(ApplicationProperties.getProperty("application.debug"))) {
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
