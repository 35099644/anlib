package com.th.snws;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.th.anlib.Lg;
import com.th.snws.inner.MsgWrap;
import com.th.snws.inner.Order;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by york on 11/07/2017.
 */

public class WebSocketClientWrap {
    private WebSocketClient     mClient;
    private boolean             hasConnected = false;
    private Map<Integer, Order> orderMap     = new HashMap<>();
    private Handler mHandler;

    private List<Runnable> cachedTasks = new ArrayList<>();

    private boolean isConnecting = false;

    private final Object lock = new Object();

    public WebSocketClientWrap(String url) {
        mHandler = new Handler(Looper.getMainLooper());

        mClient = new WebSocketClient(URI.create(url)) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                hasConnected = true;
                isConnecting = false;
                synchronized (lock) {
                    for (Runnable cachedTask : cachedTasks) {
                        cachedTask.run();
                    }
                    cachedTasks.clear();
                }
            }

            @Override
            public void onMessage(String message) {
                Lg.i("WebSocket Client onMessage: " + message);
                final MsgWrap rstMsg = new Gson().fromJson(message, MsgWrap.class);
                final Order order = orderMap.get(rstMsg.id);

                if (order.callback != null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            order.callback.onSuccess(new Gson().fromJson(rstMsg.msg, order.responseClass));
                        }
                    });
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                hasConnected = false;
                isConnecting = false;
                failedAll();
            }

            @Override
            public void onError(Exception ex) {
                hasConnected = false;
                isConnecting = false;
                failedAll();
            }

            private synchronized void failedAll() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (Integer key : orderMap.keySet()) {
                            Order order = orderMap.get(key);
                            if (order != null && !order.hasFinish) {
                                order.callback.onFailed(WebSocketServerCallback.ErrCode.ERR_CANT_CONNECT, WebSocketServerCallback.ErrCode.MSG_CANT_CONNECT);
                            }
                        }
                        orderMap.clear();
                    }
                });
            }
        };
        connect();
    }

    private void connect() {
        synchronized (lock) {
            if (!isConnecting) {
               mClient.connect();
                isConnecting = true;
            }
        }
    }

    /**
     * 服务器回应
     */
    public interface WebSocketServerCallback<T> {
        void onSuccess(T rst);
        void onFailed(int errCode, String errMsg);

        interface ErrCode {
            int ERR_CANT_CONNECT = 1;
            String MSG_CANT_CONNECT = "socket连接失败";
        }
    }

    /*-------------------------------------------*/
    /*       Public
    /*-------------------------------------------*/

    /**
     * 发送消息
     * @param msg 如果是string。则直接发送，否则将msg使用gson转为json发送
     * @param callback 服务端给的回调
     * @param clazz 服务器返回的bean
     * @param <T> 需要将服务端返回数据返json成什么类型
     */
    public <T>void send(final Object msg, final Class<T> clazz, final WebSocketServerCallback<T> callback) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                String toSend = new Gson().toJson(msg);
                MsgWrap msgWrap = obtainMsg(toSend);
                mClient.send(new Gson().toJson(msgWrap));
                orderMap.put(msgWrap.id, new Order(msgWrap, clazz, callback));
            }
        };
        if (hasConnected) {
            task.run();
        } else  {
            synchronized (lock) {
                cachedTasks.add(task);
            }
            if (!isConnecting) {
                connect();
            }
        }

//        if (msg == null || !hasConnected) {
//            if (callback != null) {
//                callback.onFailed(WebSocketServerCallback.ErrCode.ERR_CANT_CONNECT, WebSocketServerCallback.ErrCode.MSG_CANT_CONNECT);
//            }
//            return;
//        }

    }

    public void send(Object msg, WebSocketServerCallback<String> callback) {
        send(msg, String.class, callback);
    }

    private int curMsgId = 1;

    private MsgWrap obtainMsg(String msg) {
        return new MsgWrap(curMsgId++ % Integer.MAX_VALUE, msg);
    }

}
