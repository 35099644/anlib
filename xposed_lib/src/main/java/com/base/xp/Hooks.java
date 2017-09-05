package com.base.xp;


import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Hook管理类
 * Created by york on 28/03/2017.
 */

public class Hooks {
    private static Map<String, List<IHook>> mParamHandlers = new HashMap<>();

    /**
     * 增加hook
     * @param pkgName 要hook的包名
     * @param hook 要增加的hook
     */
    public static void put(String pkgName, IHook hook) {
        if (!mParamHandlers.containsKey(pkgName)) {
            List<IHook> hooks = new ArrayList<>();
            mParamHandlers.put(pkgName, hooks);
        }
        mParamHandlers.get(pkgName).add(hook);
    }

    /**
     * 是否需要hook LoadPackageParam对应的应用
     */
    public static boolean contains(XC_LoadPackage.LoadPackageParam param) {
        return mParamHandlers.containsKey(param.packageName);
    }

    /**
     * 处理hook
     */
    public static void handle(XC_LoadPackage.LoadPackageParam loadPackageParam) throws ClassNotFoundException {
        List<IHook> iHooks = mParamHandlers.get(loadPackageParam.packageName);
        if (iHooks != null) {
            for (IHook mHandler : iHooks) {
                Log.i("tonghu", "Hooks handle " + mHandler.getClass().getSimpleName() + " for pkg: " + loadPackageParam.packageName);
                try {
                    mHandler.handle(loadPackageParam);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
