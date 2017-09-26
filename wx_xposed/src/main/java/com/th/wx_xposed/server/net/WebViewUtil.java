package com.th.wx_xposed.server.net;

import com.th.wx_xposed.MMApp;

import de.robv.android.xposed.XposedHelpers;

/**
 * Created by th on 22/9/2017.
 */

public class WebViewUtil extends BaseMock {

    public WebViewUtil(Object real) {
        super(real);
    }

    public static String getUserAgent() {
        return (String) XposedHelpers.callStaticMethod(Classes.WebViewUtil, "aX", MMApp.app(), "");
    }

}
