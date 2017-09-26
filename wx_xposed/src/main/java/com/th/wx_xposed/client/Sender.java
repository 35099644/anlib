package com.th.wx_xposed.client;

import com.google.gson.Gson;
import com.th.wx_xposed.base.socket.BaseRequest;
import com.th.wx_xposed.base.socket.BaseResponse;

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

    private boolean hasOpened = false;

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
            mClient = new WebSocketClient(new URI(String.format("ws://127.0.0.1:%d", port))) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    hasOpened = true;
                }

                @Override
                public void onMessage(String message) {
                    BaseResponse baseResponse = new Gson().fromJson(message, BaseResponse.class);
                    ResponseBundle responseBundle = mCache.get(baseResponse.request.extra.index);

                    responseBundle.emitter.onNext(new Gson().fromJson(message, responseBundle.responseClazz));

                    if (baseResponse.isLast) {
                        mCache.remove(baseResponse.request.extra.index);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    hasOpened = false;
                }

                @Override
                public void onError(Exception ex) {
                    hasOpened = false;
                }
            };
        } catch (URISyntaxException e) {
            // just ignore
        }
    }

    public <T> Observable<T> send(final BaseRequest request, final Class<? extends T> responseClass) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                mCache.put(request.extra.index, new ResponseBundle(e, responseClass));
                mClient.send(new Gson().toJson(request));
            }
        });
    }

}

