package com.th.wx_xposed.server;

import android.util.Log;

import com.google.gson.Gson;
import com.th.wx_xposed.base.Config;
import com.th.wx_xposed.base.model.socket.BaseRequest;
import com.th.wx_xposed.base.model.socket.LoginQrCodeRequest;
import com.th.wx_xposed.server.requesthandler.BaseRequestHandler;
import com.th.wx_xposed.server.requesthandler.EmptyHandler;
import com.th.wx_xposed.server.requesthandler.ScanQrCodeHandler;

import org.java_websocket.WebSocket;

/**
 * Created by th on 26/9/2017.
 */

public class WxSocketServer {

    /**
     * 处理消息
     */
    public void handleMessage(WebSocket conn, String message) {

        String action = new Gson().fromJson(message, BaseRequest.class).action;
        BaseRequestHandler handler = null;

        if (LoginQrCodeRequest.ACTION.equals(action)) {
            handler = new ScanQrCodeHandler();
        }

        if (handler == null) {
            handler = new EmptyHandler();
        }

        handler.handleRequest(conn, message);
    }

}
