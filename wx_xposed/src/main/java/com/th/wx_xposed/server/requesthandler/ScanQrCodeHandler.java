package com.th.wx_xposed.server.requesthandler;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.th.wx_xposed.base.Config;
import com.th.wx_xposed.base.socket.LoginQrCode;
import com.th.wx_xposed.server.datasource.DataSource;
import com.th.wx_xposed.server.net.ConfirmLoginParam;
import com.th.wx_xposed.server.net.NetSceneBase;
import com.th.wx_xposed.server.net.NetSceneGetA8Key;
import com.th.wx_xposed.server.net.NetSceneQueue;
import com.th.wx_xposed.server.net.WxResponse;

import org.java_websocket.WebSocket;

import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by th on 26/9/2017.
 */

public class ScanQrCodeHandler extends BaseRequestHandler {

    @Override
    public void handleRequest(WebSocket conn, String message) {
        LoginQrCode loginQrCode = new Gson().fromJson(message, LoginQrCode.class);
        final NetSceneGetA8Key firstA8Key = NetSceneGetA8Key.createFirstA8Key(loginQrCode.url);
        NetSceneQueue.instance().enqueueTask(firstA8Key, new NetSceneQueue.Callback() {
            @Override
            public void onFinish(int errType, int errCode, String errMsg, NetSceneBase netSceneBase) {
                NetSceneGetA8Key netSceneGetA8Key = ((NetSceneGetA8Key) netSceneBase);
                Log.i(Config.TAG, "errType: " + errType + ", errCode: " + errCode + ", errMsg: " + errMsg + ", resultReqUrl: " + netSceneGetA8Key.getResultReqUr()
                        + "\n\tfullUrl: " + netSceneGetA8Key.getResultFullUrl());

                final NetSceneGetA8Key secondA8KeyRequest = NetSceneGetA8Key.createSecondA8Key(netSceneGetA8Key.getResultFullUrl(), firstA8Key.key);
                NetSceneQueue.instance().enqueueTask(secondA8KeyRequest, new NetSceneQueue.Callback() {
                    @Override
                    public void onFinish(int errType, int errCode, String errMsg, NetSceneBase netSceneBase) {
                        NetSceneGetA8Key secondKey = (NetSceneGetA8Key) netSceneBase;
                        String secondUrl = secondKey.getResultReqUr();
                        Map<String, String> requestHeaders = secondKey.requestHeaders();

                        Log.i(Config.TAG, "secondUrl: " + secondUrl + ", requestHeaders: " + requestHeaders);
                        DataSource.sendLoginUr(secondUrl, requestHeaders)
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(@NonNull String content) throws Exception {
                                        Log.i(Config.TAG, "success: " + content);
                                        ConfirmLoginParam confirmLoginParam = new ConfirmLoginParam(content);

                                        if (!TextUtils.isEmpty(confirmLoginParam.pass_ticket)) {
                                            DataSource.allowLogin(confirmLoginParam).subscribe(new Consumer<WxResponse>() {
                                                @Override
                                                public void accept(@NonNull WxResponse wxResponse) throws Exception {
                                                    Log.i(Config.TAG, "confirm login success: " + wxResponse);
                                                }
                                            }, new Consumer<Throwable>() {
                                                @Override
                                                public void accept(@NonNull Throwable throwable) throws Exception {
                                                    Log.i(Config.TAG, "confirm failed: " + throwable);
                                                }
                                            });
                                        } else {
                                            Log.i(Config.TAG, "scanLogin faile");
                                        }
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                        Log.i(Config.TAG, "failed: " + throwable);
                                    }
                                });
                    }
                });
            }
        });
    }

}
