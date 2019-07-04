package com.feyzullahefendi.akraarsiv;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Environment;
import android.util.Log;
import androidx.core.content.ContextCompat;
//import com.coremedia.iso.boxes.Container;
//import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
//import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import nl.bravobit.ffmpeg.ExecuteBinaryResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class M3U8Manager {

    IM3u8StatusChangeListener listener;
    Activity activity;
    String guid;

    M3U8Manager(Activity activity) {
        this.activity = activity;
        File cacheDir = activity.getCacheDir();
        File[] externalCacheDir = ContextCompat.getExternalCacheDirs(activity);
        File storageDir = Environment.getExternalStorageDirectory();
    }

    public void download(String url, String guid, IM3u8StatusChangeListener listener) {
        this.listener = listener;
        this.guid = guid;
        new Thread(() -> {
            try {
                start(url);
            } catch (IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> listener.onError(e));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void start(String url) throws Exception {
        String sourceUrl = Utils.getSourceUrl(url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            String body = new String(response.body().bytes(), StandardCharsets.UTF_8);
            throw new Exception("playlist.m3u8 response not valid");
        }
        String body = new String(response.body().bytes(), StandardCharsets.UTF_8);
        String fileName = Utils.getFileNameFromPlaylistM3U8(body);
        if (fileName == null) {
            throw new Exception("fileName could not found for this path");
        }

        String chunkListUrl = sourceUrl + "/" + fileName;

        Request chunkListRequest = new Request.Builder().url(chunkListUrl).build();
        Response chunkListResponse = client.newCall(chunkListRequest).execute();

        if (!chunkListResponse.isSuccessful()) {
            throw new Exception("chunkList response not valid");
        }
        String chunkListBody = new String(chunkListResponse.body().bytes(), StandardCharsets.UTF_8);
        File tempFolder = new File(this.activity.getCacheDir() + File.separator + this.guid);
        if (tempFolder.exists()) {
            deleteRecursive(tempFolder);
        }
        boolean isFileCreated = tempFolder.mkdirs();
        if (!isFileCreated && !tempFolder.exists()) {
            throw new Exception("Dosya izinlerinde hata oluştu");
        }
        ArrayList<String> chunkFiles = Utils.getFileNamesFromPlaylistM3U8(chunkListBody);
        for (int i = 0; i < chunkFiles.size(); i++) {
            String chunk = chunkFiles.get(i);
            String fileUrl = sourceUrl + "/" + chunk;
            Request chunkFileRequest = new Request.Builder().url(fileUrl).build();
            Response chunkFileResponse = client.newCall(chunkFileRequest).execute();
            if (!chunkFileResponse.isSuccessful()) {
                throw new Exception("İndirme sırasında hata oluştu");

            }
            File chunkFile = new File(tempFolder + File.separator + i + ".acc");
            chunkFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(chunkFile, true);
            fileOutputStream.write(chunkFileResponse.body().bytes());
            fileOutputStream.close();
            int finalI = i;

            this.activity.runOnUiThread(() -> listener.onChunkFileDownloaded(finalI + 1, chunkFiles.size()));
        }
//        File storageDir = Environment.getExternalStorageDirectory();
        File mp3File = new File(ContextCompat.getExternalCacheDirs(activity)[1] + File.separator + guid + ".mp3");
        mp3File.createNewFile();
//        FileOutputStream mp3OutputStream = new FileOutputStream(mp3File, true);
//        for (int i = 0; i < chunkFiles.size(); i++) {
//            File chunkFile = new File(tempFolder + File.separator + i + ".temp");
//            byte[] bytes = FileUtils.readFileToByteArray(chunkFile);
//            mp3OutputStream.write(bytes);
//        }
//        mp3OutputStream.close();
//        FileUtils.copyFileToDirectory(mp3File, ContextCompat.getExternalCacheDirs(context)[1]);
//        String output = Environment.getExternalStorageDirectory().getAbsolutePath() + "output.mp3";

        String[] chunkFilePaths = new String[chunkFiles.size()];
        for (int i = 0; i < chunkFiles.size(); i++) {
            chunkFilePaths[i] = tempFolder + File.separator + i + ".acc";
        }
        String[] cmd = Utils.getFFmpegCommandArray(chunkFilePaths, mp3File.getPath());
        FFmpeg ffmpeg = FFmpeg.getInstance(activity);
        String TAG = "ffmpeg.execute";
        if (ffmpeg.isSupported()) {
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onProgress(String message) {
                    super.onProgress(message);
                    Log.i(TAG, "onProgress: " + message);
                }

                @Override
                public void onStart() {
                    super.onStart();
                    Log.i(TAG, "onStart");
                }

                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);
                    Log.i(TAG, "onSuccess");
                }

                @Override
                public void onFailure(String message) {
                    super.onFailure(message);
                    Log.i(TAG, "onFailure");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    Log.i(TAG, "onFinish");
                }
            });
        } else {
            throw new Exception("FFmpeg not supported");
        }

//        deleteRecursive(tempFolder);
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        fileOrDirectory.delete();
    }

}
