package com.th.wx_xposed.server.requesthandler;

import org.java_websocket.WebSocket;

/**
 * Created by th on 26/9/2017.
 */

public abstract class BaseRequestHandler {

    public abstract void handleRequest(WebSocket conn, String message);
}
