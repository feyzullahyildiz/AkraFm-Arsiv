package com.feyzullahefendi.akraarsiv;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * FileUtils.
 *
 * @author ccollins
 */
final class FileUtil {

    // Object for intrinsic lock (per docs 0 length array "lighter" than a normal Object
    public static final Object[] DATA_LOCK = new Object[0];

    private FileUtil() {
    }

    /**
     * Copy file.
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public static void copyFile(final File src, final File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    /**
     * Replace entire File with contents of String.
     *
     * @param fileContents
     * @param file
     * @return
     */
    public static boolean writeStringAsFile(final String fileContents, final File file) {
        boolean result = false;
        try {
            synchronized (DATA_LOCK) {
                if (file != null) {
                    file.createNewFile(); // ok if returns false, overwrite
                    Writer out = new BufferedWriter(new FileWriter(file), 1024);
                    out.write(fileContents);
                    out.close();
                    result = true;
                }
            }
        } catch (IOException e) {
            // Log.e(Constants.LOG_TAG, "Error writing string data to file " + e.getMessage(), e);
        }
        return result;
    }

    private static SimpleDateFormat sdf = null;
    private static File LOG_FILE = null;

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat getDefaultSdf() {
        if (sdf == null) {
            sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return sdf;
    }
    public static void setFile (File file) {
        LOG_FILE = file;
    }
    public static boolean appendStringToFile(final String appendContents) {
        boolean result = false;
        try {
            synchronized (DATA_LOCK) {
                if (LOG_FILE != null && LOG_FILE.canWrite()) {
                    LOG_FILE.createNewFile(); // ok if returns false, overwrite
                    Writer out = new BufferedWriter(new FileWriter(LOG_FILE, true), 1024);
                    String logTime = getDefaultSdf().format(new Date());
                    out.write(String.format("%s %s \n", logTime, appendContents));
                    out.close();
                    result = true;
                }
            }
        } catch (IOException e) {
            //   Log.e(Constants.LOG_TAG, "Error appending string data to file " + e.getMessage(), e);
        }
        return result;
    }
}
