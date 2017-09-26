package com.th.wx_xposed.base.socket;

/**
 * Created by th on 25/9/2017.
 */

public class RequestExtra {
    private static int GLOBAL_INDEX = 0;
    public int index;

    public RequestExtra(int index) {
        this.index = index;
    }

    public static RequestExtra createExtra () {
        return new RequestExtra(GLOBAL_INDEX++ % Integer.MAX_VALUE);
    }

}
