package com.base.xp;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 本源码只限于学习交流使用，其用途于与原作者无关\nCreated by york on 16/03/2017.
 */

public interface IHook {
    void handle(XC_LoadPackage.LoadPackageParam param) throws ClassNotFoundException;
}
