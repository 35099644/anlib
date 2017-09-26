package com.th.wx_xposed.base.socket;

/**
 * Created by th on 21/9/2017.
 */

public class LoginQrCode extends BaseRequest {

    public static final String ACTION = "login_qrcode";

    public String url;

    public LoginQrCode() {
        super(ACTION);
    }

    public LoginQrCode(String url) {
        super(ACTION);
        this.url = url;
    }

}
