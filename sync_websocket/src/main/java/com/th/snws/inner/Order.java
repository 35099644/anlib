package com.th.snws.inner;

import com.th.snws.WebSocketClientWrap;

/**
 * 客户端向服务端发送的任务
 * Created by york on 12/07/2017.
 */

public class Order {
    public MsgWrap mMsgWrap;

    public boolean hasFinish = false;

    public WebSocketClientWrap.WebSocketServerCallback callback;

    public Order(MsgWrap msgWrap, WebSocketClientWrap.WebSocketServerCallback callback) {
        mMsgWrap = msgWrap;
        this.callback = callback;
    }
}

