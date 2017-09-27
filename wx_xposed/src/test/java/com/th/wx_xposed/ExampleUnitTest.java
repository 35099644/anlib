package com.th.wx_xposed;

import com.google.gson.Gson;
import com.th.wx_xposed.base.model.socket.BaseResponse;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {
        String json = "{\"isLast\":false,\"request\":{\"url\":\"http://mp.weixin.qq.com/wap/loginauthqrcode?action\\u003dscan\\u0026qrticket\\u003d132bc9778a2050f14b9a8f5da79e8ba6#wechat_redirect\",\"action\":\"login_qrcode\",\"extra\":{\"index\":0}}}";
        BaseResponse baseResponse = new Gson().fromJson(json, BaseResponse.class);
        System.out.println("baseResponse:" + baseResponse);
    }


}