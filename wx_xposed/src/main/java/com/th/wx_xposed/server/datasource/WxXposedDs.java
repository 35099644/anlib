package com.th.wx_xposed.server.datasource;

import android.util.Log;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.th.net.Thyi;
import com.th.wx_xposed.MMApp;
import com.th.wx_xposed.base.Config;
import com.th.wx_xposed.server.net.ConfirmLoginParam;
import com.th.wx_xposed.server.net.CookieManager;
import com.th.wx_xposed.server.net.WxResponse;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * DataSource 请求类
 * Created by th on 22/9/2017.
 */

public class WxXposedDs {
    private Thyi thyi;

    private static WxXposedDs instance;

    private WxXposedDs() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(MMApp.app())))
                .build();
        thyi = new Thyi(okHttpClient);
    }

    public static WxXposedDs instance() {
        if (instance == null) {
            synchronized (WxXposedDs.class) {
                if (instance == null) {
                    instance = new WxXposedDs();
                }
            }
        }
        return instance;
    }

    public static Observable<String> sendLoginUr(String url, Map<String, String> httpHeaders) {
        Request.Builder rb = new Request.Builder()
                .url(url).get();

        for (String key : httpHeaders.keySet()) {
            rb.header(key, httpHeaders.get(key));
        }

        addCommon2Request(rb, url);

        Log.i(Config.TAG, "send, url: " + url + ", headers: " + httpHeaders);
        return instance().thyi.request(rb.build(), String.class);

    }

    public static Observable<WxResponse> allowLogin(ConfirmLoginParam param) {
        Log.i(Config.TAG, "send allow login, param: " + param);
        FormBody.Builder postBuilder = new FormBody.Builder();
        postBuilder.add("param", "qrticket'");
        postBuilder.add("qrticket", param.qrticket);
        postBuilder.add("uin", "777");
        postBuilder.add("key", "777");
        postBuilder.add("pass_ticket", param.pass_ticket);
        postBuilder.add("appmsg_token", param.appmsg_token);
        postBuilder.add("f", "json");

        String url = "https://mp.weixin.qq.com/wap/loginauthqrcode?action=confirm";
        Request.Builder rb = new Request.Builder();
        rb.url(url).post(postBuilder.build());

        addCommon2Request(rb, url);

        return instance().thyi.request(rb.build(), WxResponse.class);
    }

    private static Request.Builder addCommon2Request(Request.Builder rb, String url) {
        rb.header("User-Agent", "Mozilla/5.0 (Linux; Android 6.0.1; ZUK Z2131 Build/MMB29M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/48.0.2564.106 Mobile Safari/537.36 MicroMessenger/6.5.4.1000 NetType/WIFI Language/zh_CN");
        rb.header("x-requested-with", "com.tencent.mm");
        String cookie = CookieManager.instance().getCookie(url);
        rb.header("Cookie", cookie);

        return rb;
    }

}
