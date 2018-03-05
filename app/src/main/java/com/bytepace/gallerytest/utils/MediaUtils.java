package com.bytepace.gallerytest.utils;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Viktor on 02.03.2018.
 */

public class MediaUtils {
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    public static boolean isAudioFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("audio");
    }

    public static List<File> getMediaFilesList(String directory) {
        List<File> result = new ArrayList<>();
        List<File> files = getAllDirectoryFiles(directory);
        for (File f : files) {
            if (f.isDirectory()) {
                result.addAll(getMediaFilesList(f.getAbsolutePath()));
            } else {
                if (isImageFile(f.getAbsolutePath())
                        || isVideoFile(f.getAbsolutePath())
                        || isAudioFile(f.getAbsolutePath())) {
                    result.add(f);
                }
            }
        }
        return result;
    }

    private static List<File> getAllDirectoryFiles(String path) {
        if (!path.equals("/sys") && !path.equals("/proc")) {
            File directory = new File(path);
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                return new ArrayList<>(Arrays.asList(files));
            }
        }
        return new ArrayList<>();
    }

    public static String getFileSize(File file) {
        String value;
        long Filesize = getFolderSize(file) / 1024;//call function and convert bytes into Kb
        if (Filesize >= 1024)
            value = Filesize / 1024 + " Mb";
        else
            value = Filesize + " Kb";

        return value;
    }

    private static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size = f.length();
        }
        return size;
    }
}
