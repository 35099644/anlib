package com.th.wx_xposed.server.net;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XposedHelpers;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * 获取A8key
 * 分成两次，第一次获取pass_ticket
 *
 * 第二次获取请求要的参数
 * Created by th on 21/9/2017.
 */

public class NetSceneGetA8Key extends NetSceneBase {
    /**
     * 原来的类需要一个时间，而且不知道存哪里去了，我们就存在wrapper里面
     */
    public int key;

    public NetSceneGetA8Key(Object real) {
        super(real);
    }


    private NetSceneGetA8Key(Object real, int key) {
        super(real);
        this.key = key;
    }

    public static NetSceneGetA8Key createFirstA8Key(String originalUrl) {
        int time = (int) System.currentTimeMillis();
        return new NetSceneGetA8Key(XposedHelpers.newInstance(Classes.NetSceneGetA8Key, originalUrl, 4, 19, 9, null, time), time);
    }


    public static NetSceneGetA8Key createSecondA8Key(String originalUrl, int preTime) {
        return new NetSceneGetA8Key(XposedHelpers.newInstance(Classes.NetSceneGetA8Key, originalUrl, null, 4, 0, 1, "WIFI", preTime), preTime);
    }

    public String getResultReqUr() {
        return (String) XposedHelpers.callMethod(real, "Ja");
    }

    public String getResultFullUrl() {
        return (String) XposedHelpers.callMethod(real, "IZ");
    }

    public Map<String, String> requestHeaders() {
        List<Object> list = (List<Object>) getObjectField(getObjectField(getObjectField(getObjectField(real, "cjO"), "cBt"), "cBA"), "mZa");
        Map<String, String> rst = new HashMap<>();
        for (Object o : list) {
            rst.put((String)getObjectField(o, "mKq"), (String) getObjectField(o, "iME"));
        }
        return rst;
    }

}
