package com.th.wx_xposed.base.socket;

/**
 * Created by th on 25/9/2017.
 */

public class BaseRequest {

    public String action;
    public RequestExtra extra;

    public BaseRequest(String action) {
        this.action = action;
        extra = RequestExtra.createExtra();
    }


}
