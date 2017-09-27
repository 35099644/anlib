package com.th.wx_xposed.server.hooks;

import android.app.Application;
import android.util.Log;

import com.th.wx_xposed.MMApp;
import com.th.wx_xposed.base.Config;
import com.th.wx_xposed.server.net.Classes;
import com.th.wx_xposed.server.net.NetSceneQueue;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 初始化类
 * Created by th on 26/9/2017.
 */

public class WxXposedLib {

    /**
     * 初始化 微信服务端
     *
     * @param wxApplication 微信的Application
     * @param param 微信的LoadPackageparam，注意应该在Application的OnCreate执行之后调用。为了防止有些类还没有加载进来
     */
    public static void initialWx(Application wxApplication, XC_LoadPackage.LoadPackageParam param) {
        MMApp.setApp(wxApplication);
        Classes.init(param.classLoader);
        try {
            NetSceneQueue.Helper.xpHook.handle(param);
        } catch (ClassNotFoundException e) {
            Log.i(Config.TAG, e + "");
            e.printStackTrace();
        }
    }

}
