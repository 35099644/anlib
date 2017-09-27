package com.th.wx_xposed.base.model.socket;

/**
 * Created by th on 21/9/2017.
 */

public class LoginQrCodeRequest extends BaseRequest {

    public static final String ACTION = "login_qrcode";

    public String url;

    public LoginQrCodeRequest() {
        super(ACTION);
    }

    public LoginQrCodeRequest(String url) {
        super(ACTION);
        this.url = url;
    }

    @Override
    public String toString() {
        return "LoginQrCodeRequest{" +
                "url='" + url + '\'' +
                '}';
    }
}
