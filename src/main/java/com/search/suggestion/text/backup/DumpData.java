package com.search.suggestion.text.backup;

import com.search.suggestion.data.RawSearchUpdateRequest;
import com.search.suggestion.data.SuggestPayload;
import com.search.suggestion.engine.SearchEngine;
import com.search.suggestion.util.ApplicationProperties;
import com.search.suggestion.util.DateUtil;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nikhil on 10/1/18.
 */
public class DumpData {

    private String directory;
    private DateFormat dateFormat;
    private static String dateStr ="";

    public static void save(String data) {
        try {
                dumpToFile(data);
            }
        catch (Exception ex) {
                ex.printStackTrace();
        }
    }

    private static void dumpToFile(String data) throws IOException {
        if(ApplicationProperties.getProperty("recover.method").equals("file")) {
            String directory = System.getProperties().get("user.dir") + ApplicationProperties.getProperty("recover.method.file.backup.path");
            String file = DateUtil.getStringDate("yyyy-MM-dd", new Date());
            String path = directory + file + ".txt";
            BufferedWriter bw = null;
            FileWriter fw = null;


            try {

                if (path.equals(dateStr)) {
                    File f = new File(path);
                    if (!f.exists()) {
                        dateStr = path;
                        f.createNewFile();
                    }
                }

                fw = new FileWriter(path, true);
                bw = new BufferedWriter(fw);
                bw.write(data);
                bw.newLine();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bw != null)
                        bw.close();

                    if (fw != null)
                        fw.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }

        }
    }

}
