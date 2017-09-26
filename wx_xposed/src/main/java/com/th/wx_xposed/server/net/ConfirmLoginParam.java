package com.th.wx_xposed.server.net;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by th on 24/9/2017.
 */

public class ConfirmLoginParam {
    public String qrticket;
    public String pass_ticket;
    public String appmsg_token;

    public ConfirmLoginParam(String webContent) {
        qrticket = getValue(webContent, "qrticket");
        pass_ticket = getValue(webContent, "pass_ticket");
        appmsg_token = getValue(webContent, "appmsg_token");
    }

    private String getValue(String webContent, String key) {
        Pattern pattern = Pattern.compile(key + ": *'(.*)',");
        Matcher matcher = pattern.matcher(webContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    @Override
    public String toString() {
        return "ConfirmLoginParam{" +
                "qrticket='" + qrticket + '\'' +
                ", pass_ticket='" + pass_ticket + '\'' +
                ", appmsg_token='" + appmsg_token + '\'' +
                '}';
    }
}
