package com.th.wx_xposed.server.net;

import de.robv.android.xposed.XposedHelpers;

/**
 * Created by th on 23/9/2017.
 */

public class CookieManager extends BaseMock {
    private static CookieManager instance;

    private CookieManager(Object real) {
        super(real);
    }

    public static CookieManager instance() {
        if (instance == null) {
            synchronized (CookieManager.class) {
                if (instance == null) {
                    Object real = XposedHelpers.callStaticMethod(Classes.CookieManager, "getInstance");
                    instance = new CookieManager(real);
                }
            }
        }
        return instance;
    }

    public String getCookie(String url) {
        return (String) XposedHelpers.callMethod(real, "getCookie", url);
    }
}
