package com.feyzullahefendi.akraarsiv;


import android.annotation.SuppressLint;
import io.reactivex.subjects.BehaviorSubject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String TAG = "TAGTAG";

    public static String getSourceUrl(String url) {
        Pattern pattern = Pattern.compile("(https?:\\/\\/.*)\\/playlist.m3u8", Pattern.MULTILINE);
        String _url = url.endsWith("playlist.m3u8") ? url : url.split("playlist.m3u8")[0] + "playlist.m3u8";
        Matcher matcher = pattern.matcher(_url);
        if (matcher.find()) {
            return matcher.replaceAll("$1");
        }

        return url;
    }

    public static String getFileNameFromPlaylistM3U8(String body) {
        ArrayList<String> newLines = getFileNamesFromPlaylistM3U8(body);
        if (newLines.size() > 1 && BuildConfig.DEBUG) {
            return null;
        }
        return newLines.get(0);
    }

    public static ArrayList<String> getFileNamesFromPlaylistM3U8(String body) {
        String[] lines = body.split("\n");
        ArrayList<String> newLines = new ArrayList<String>();
        for (String line : lines) {
            if (line.length() > 0 && !line.startsWith("#")) {
                newLines.add(line);
            }
        }
        return newLines;
    }

    public static String[] getFFmpegCommandArray(String[] paths, String outputPath) {
        ArrayList<String> cmd = new ArrayList<String>();
//        "-y", "-i", chunkFilePaths[0], "-i", chunkFilePaths[1], "-i", chunkFilePaths[2], "-filter_complex", "[0:0][1:0][2:0]concat=n=3:v=0:a=1[out]", "-map", "[out]", mp3File.getPath()
        if (paths.length == 0) {
            return null;
        }
        cmd.add("-y");
        for (String path : paths) {
            cmd.add("-i");
            cmd.add(path);
        }
        cmd.add("-filter_complex");
        cmd.add(getFFmpegFilterComplexValue(paths.length));
        cmd.add("-map");
        cmd.add("[out]");
        cmd.add(outputPath);
        return cmd.toArray(new String[0]);
    }

    public static String getFFmpegFilterComplexValue(int count) {
//        [0:0][1:0][2:0]concat=n=3:v=0:a=1[out]
        StringBuilder value = new StringBuilder();
        for (int i = 0; i < count; i++) {
            value.append(String.format(Locale.ENGLISH, "[%d:0]", i));
        }
        value.append(String.format(Locale.ENGLISH, "concat=n=%d:v=0:a=1[out]", count));
        return value.toString();

    }

    public static String getTimeFromSeconds(int time) {
        return getTimeFromSeconds((long) time);
    }

    private static SimpleDateFormat sdf;

    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat getDefaultSdf() {
        if (sdf == null) {
            sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        return sdf;
    }


    public static String getTimeFromSeconds(Long time) {
        Date d = new Date();
        d.setTime(time);
        return getDefaultSdf().format(time);
    }

    public static final BehaviorSubject<CategoryModel> categoryModelSubject = BehaviorSubject.create();
    public static final BehaviorSubject<ChildProgram> childProgramSubject = BehaviorSubject.create();
    public static final BehaviorSubject<StreamModel> streamModelSubject = BehaviorSubject.create();


}
