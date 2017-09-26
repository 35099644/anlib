package com.th.wx_xposed;

import android.app.Application;

import java.lang.ref.WeakReference;

/**
 * 本源码只限于学习交流使用，其他用于与原作者无关
 * Created by york on 12/06/2017.
 */

public class MMApp {
    private static WeakReference<Application> app;

    public static Application app() {
        return app.get();
    }

    public static void setApp(Application app) {
        MMApp.app = new WeakReference<>(app);
    }

}
