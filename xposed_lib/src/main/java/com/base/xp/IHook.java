package com.base.xp;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by jdb on 16/03/2017.
 */

public interface IHook {
    void handle(XC_LoadPackage.LoadPackageParam param) throws ClassNotFoundException;
}
