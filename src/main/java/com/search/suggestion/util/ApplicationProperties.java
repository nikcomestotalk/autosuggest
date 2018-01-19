package com.search.suggestion.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by nikhil on 29/7/17.
 */
public class ApplicationProperties {
    public static Properties properties;
    public static void configureFirst() throws IOException {
        // create and load default properties
        properties = new Properties();

        InputStream inputStream = new FileInputStream(System.getProperties().get("user.dir")+"/config/config.properties");
        properties.load(inputStream);
        inputStream.close();
    }
    public static String getProperty(String key) throws IOException {
        if(properties == null) {
            ApplicationProperties.configureFirst();
        }
        return properties.getProperty(key);
    }
}
