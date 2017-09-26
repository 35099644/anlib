package com.th.wx_xposed.server.net;

import de.robv.android.xposed.XposedHelpers;

/**
 * 网络请求基础类
 * Created by th on 20/9/2017.
 */

public class NetSceneBase extends BaseMock {

    public NetSceneBase(Object real) {
        super(real);
    }

    public int getType() {
        return (int) XposedHelpers.callMethod(real, "getType");
    }

}
