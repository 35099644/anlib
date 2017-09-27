package com.th.wx_xposed.client;

import android.util.Log;

import com.google.gson.Gson;
import com.th.wx_xposed.base.Config;
import com.th.wx_xposed.base.model.socket.BaseRequest;
import com.th.wx_xposed.base.model.socket.BaseResponse;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * 请求发送方，目前仅支持发送 - 收到请求模式
 *
 * 忽略：
 * 超时没有回应
 * Created by th on 25/9/2017.
 */

public class Sender {
    private WebSocketClient mClient;

    private boolean hasConnected = false;

    private Map<Integer, ResponseBundle> mCache = new HashMap<>();

    public static class ResponseBundle {
        public ObservableEmitter emitter;
        public Class responseClazz;

        public ResponseBundle(ObservableEmitter emitter, Class responseClazz) {
            this.emitter = emitter;
            this.responseClazz = responseClazz;
        }
    }

    public Sender(int port) {
        try {
            mClient = new WebSocketClient(new URI(String.format("ws://localhost:%d", port))) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.i(Config.TAG, "socket OnOpen");
                    hasConnected = true;
                }

                @Override
                public void onMessage(String message) {
                    Log.i(Config.TAG, "socket OnMessage: " + message);
                    BaseResponse baseResponse = new Gson().fromJson(message, BaseResponse.class);
                    ResponseBundle responseBundle = mCache.get(baseResponse.request.extra.index);

                    responseBundle.emitter.onNext(new Gson().fromJson(message, responseBundle.responseClazz));

                    if (baseResponse.isLast) {
                        mCache.remove(baseResponse.request.extra.index);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.i(Config.TAG, "socket onClose, reason: " + reason);
                    hasConnected = false;
                }

                @Override
                public void onError(Exception ex) {
                    Log.i(Config.TAG, "socket onError: " + ex);
                    hasConnected = false;
                }
            };
        } catch (URISyntaxException e) {
            Log.i(Config.TAG, "e: "  + e);
        }
    }

    public void connect() {
        if (mClient != null) {
            Log.i(Config.TAG, "begin connect");
            mClient.connect();
            hasConnected = true;
        }
    }

    public <T extends BaseResponse> Observable<T> send(final BaseRequest request, final Class<? extends T> responseClass) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                if (!hasConnected) {
                    T response = responseClass.newInstance();
                    response.state = new BaseResponse.State(BaseResponse.State.ERR_NOT_CONNECTED, BaseResponse.State.MSG_NOT_CONNECTED);
                    e.onNext(response);
                } else {
                    mCache.put(request.extra.index, new ResponseBundle(e, responseClass));
                    mClient.send(new Gson().toJson(request));
                }

            }
        });
    }

}

