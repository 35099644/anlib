package com.th.anlib;

import android.util.Log;

/**
 * Created by york on 07/07/2017.
 */

public class Lg {
    public static final String TAG = "Crawl";

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
}
