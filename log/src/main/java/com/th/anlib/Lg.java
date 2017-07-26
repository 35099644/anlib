package com.th.anlib;

import android.util.Log;

/**
 * Created by york on 07/07/2017.
 */

public class Lg {
    private static FileLog fileLog = FileLog.instance();

    private static String TAG = "test";

    private static boolean logToFile = false;

    /**
     * 设置tag
     */
    public static void setTag(String tag) {
        Lg.TAG = tag;
    }

    /**
     * 设置是否同时log到文件。
     * @param logToFile true为同时log到文件
     * @param logFolder 如果log到文件，设置log的文件夹
     */
    public static void logToFile(boolean logToFile, String logFolder) {
        Lg.logToFile = logToFile;
        fileLog.setLogFolder(logFolder);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    private static void iInner(String tag, String msg) {
        if (logToFile) {
            fileLog.write(Log.INFO, tag + ": " + msg);
        }
        Log.i(tag, msg);
    }

    private static void dInner(String tag, String msg) {
        if (logToFile) {
            fileLog.write(Log.DEBUG, tag + ": " + msg);
        }
        Log.d(tag, msg);
    }

    private static void wInner(String tag, String msg) {
        if (logToFile) {
            fileLog.write(Log.WARN, tag + ": " + msg);
        }
        Log.w(tag, msg);
    }

    private static void eInner(String tag, String msg) {
        if (logToFile) {
            fileLog.write(Log.ERROR, tag + ": " + msg);
        }
        Log.e(tag, msg);
    }
}
