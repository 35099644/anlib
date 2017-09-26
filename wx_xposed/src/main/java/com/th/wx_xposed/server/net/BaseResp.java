package com.th.wx_xposed.server.net;

/**
 * Created by york on 05/09/2017.
 */

public class BaseResp {
    public String err_msg;
    public int ret;

    public interface Rst {
        int NEED_CAPTCHA = 200008;
        int OK = 0;
    }

    @Override
    public String toString() {
        return "BaseResp{" +
                "err_msg='" + err_msg + '\'' +
                ", ret=" + ret +
                '}';
    }
}

