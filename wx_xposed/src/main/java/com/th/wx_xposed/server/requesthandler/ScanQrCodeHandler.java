package com.th.wx_xposed.server.requesthandler;

import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import com.google.gson.Gson;
import com.th.wx_xposed.base.Config;
import com.th.wx_xposed.base.model.socket.BaseRequest;
import com.th.wx_xposed.base.model.socket.LoginQrCodeRequest;
import com.th.wx_xposed.server.datasource.WxXposedDs;
import com.th.wx_xposed.server.net.ConfirmLoginParam;
import com.th.wx_xposed.server.net.NetSceneBase;
import com.th.wx_xposed.server.net.NetSceneGetA8Key;
import com.th.wx_xposed.server.net.NetSceneQueue;
import com.th.wx_xposed.server.net.WxResponse;

import org.java_websocket.WebSocket;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by th on 26/9/2017.
 */

public class ScanQrCodeHandler extends BaseRequestHandler {

    @Override
    public void handleRequest(final WebSocket conn, String message) {
        Log.i(Config.TAG, "ScanQrCodeHandler receive message: " + message);

        final LoginQrCodeRequest loginQrCode = new Gson().fromJson(message, LoginQrCodeRequest.class);

        Log.i(Config.TAG, "ScanQrCodeHandler request: " + loginQrCode);

        if (TextUtils.isEmpty(loginQrCode.url)) {
            responseFailed(conn, loginQrCode, -6, "url为空");
        }
        Log.i(Config.TAG, "original url " + loginQrCode.url);

        final NetSceneGetA8Key firstA8Key = NetSceneGetA8Key.createFirstA8Key(loginQrCode.url);

        NetSceneQueue.instance().enqueueTask(firstA8Key, new NetSceneQueue.Callback() {
            @Override
            public void onFinish(int errType, int errCode, String errMsg, Object netSceneBaseReal) {
                NetSceneGetA8Key netSceneGetA8Key = new NetSceneGetA8Key(netSceneBaseReal);
                Log.i(Config.TAG, "real: " + netSceneBaseReal);
                Log.i(Config.TAG, "errType: " + errType + ", errCode: " + errCode + ", errMsg: " + errMsg + ", resultReqUrl: " + netSceneGetA8Key.getResultReqUr()
                        + "\n\tfullUrl: " + netSceneGetA8Key.getResultFullUrl());

                if (TextUtils.isEmpty(netSceneGetA8Key.getResultFullUrl())) {
                    responseFailed(conn, loginQrCode, -5, "获取fullUrl失败");
                    return;
                }

                final NetSceneGetA8Key secondA8KeyRequest = NetSceneGetA8Key.createSecondA8Key(netSceneGetA8Key.getResultFullUrl(), firstA8Key.key);
                NetSceneQueue.instance().enqueueTask(secondA8KeyRequest, new NetSceneQueue.Callback() {
                    @Override
                    public void onFinish(int errType, int errCode, String errMsg, Object netSceneBaseReal) {
                        NetSceneGetA8Key secondKey = new NetSceneGetA8Key(netSceneBaseReal);

                        String secondUrl = secondKey.getResultReqUr();
                        Map<String, String> requestHeaders = secondKey.requestHeaders();

                        Log.i(Config.TAG, "secondUrl: " + secondUrl + ", requestHeaders: " + requestHeaders);

                        WxXposedDs.sendLoginUr(secondUrl, requestHeaders)
                                .flatMap(new Function<String, ObservableSource<WxResponse>>() {
                                    @Override
                                    public ObservableSource<WxResponse> apply(@NonNull String content) throws Exception {
                                        ConfirmLoginParam confirmLoginParam = new ConfirmLoginParam(content);
                                        if (!TextUtils.isEmpty(confirmLoginParam.pass_ticket)) {
                                            return WxXposedDs.allowLogin(confirmLoginParam);
                                        } else {
                                            return Observable.error(new Throwable());
                                        }
                                    }
                                })
                                .map(new Function<WxResponse, Boolean>() {
                                    @Override
                                    public Boolean apply(@NonNull WxResponse wxResponse) throws Exception {
                                        Log.i(Config.TAG, "ScanQrCodeHandler allow login result: " + wxResponse);
                                        return wxResponse.base_resp.ret == 0;
                                    }
                                })
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                                        responseSuccess(conn, loginQrCode);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                        responseFailed(conn, loginQrCode, -3, "网络请求错误");
                                    }
                                });

                    }
                });
            }
        });
    }

}
