package com.th.snws;

import com.google.gson.Gson;
import com.th.snws.inner.MsgWrap;
import com.th.snws.inner.Order;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by york on 11/07/2017.
 */

public class WebSocketClientWrap {
    private WebSocketClient     mClient;
    private boolean             isOpen   = false;
    private Map<Integer, Order> orderMap = new HashMap<>();

    public WebSocketClientWrap(String url) {
        mClient = new WebSocketClient(URI.create(url)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                isOpen = true;
            }

            @Override
            public void onMessage(String message) {
                MsgWrap rstMsg = new Gson().fromJson(message, MsgWrap.class);
                Order order = orderMap.get(rstMsg.id);
                if (order.callback != null) {
                    order.callback.onSuccess(rstMsg.msg);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                isOpen = false;
                failedAll();
            }

            @Override
            public void onError(Exception ex) {
                isOpen = false;
                failedAll();
            }

            private synchronized void failedAll() {
                for (Integer key : orderMap.keySet()) {
                    Order order = orderMap.get(key);
                    if (order != null && !order.hasFinish) {
                        order.callback.onFailed();
                    }
                }
                orderMap.clear();
            }
        };
        mClient.connect();
    }

    public interface WebSocketServerCallback {
        void onSuccess(String rst);
        void onFailed();
    }

    /*-------------------------------------------*/
    /*       Public
    /*-------------------------------------------*/

    /**
     * 发送消息
     * @param msg 如果是string。则直接发送，否则将msg使用gson转为json发送
     * @param callback 服务端给的回调
     * @param <T> 需要将服务端返回数据返json成什么类型
     */
    public <T>void send(Object msg, WebSocketServerCallback callback) {
        if (msg == null || !isOpen) {
            if (callback != null) {
                callback.onFailed();
            }
            return;
        }

        String toSend = new Gson().toJson(msg);
        MsgWrap msgWrap = obtainMsg(toSend);
        mClient.send(new Gson().toJson(msgWrap));
        orderMap.put(msgWrap.id, new Order(msgWrap, callback));
    }

    private int curMsgId = 1;

    private MsgWrap obtainMsg(String msg) {
        return new MsgWrap(curMsgId++ % Integer.MAX_VALUE, msg);
    }

}
