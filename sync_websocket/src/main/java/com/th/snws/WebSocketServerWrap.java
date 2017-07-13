package com.th.snws;

import com.google.gson.Gson;
import com.th.anlib.Lg;
import com.th.snws.inner.MsgWrap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * Created by york on 11/07/2017.
 */

public class WebSocketServerWrap {
    static {
        Lg.TAG = "WebSocket";
    }

    private WebSocketServer mServer;
    private MsgHandler      mHandler;

    public interface MsgHandler {
        void handle(String request, Callback callback);
    }

    public interface Callback {
        void onFinish(Object rst);
    }

    public WebSocketServerWrap(int port, MsgHandler handler) {
        mHandler = handler;
        mServer = new WebSocketServer(new InetSocketAddress(port)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                Lg.i("onOpen");
            }

            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                Lg.i("onClose");
            }

            @Override
            public void onMessage(final WebSocket conn, String message) {
                Lg.i("onMessage: " + message);
                final MsgWrap msgWrap = new Gson().fromJson(message, MsgWrap.class);
                if (mHandler != null) {
                    mHandler.handle(msgWrap.msg, new Callback() {
                        @Override
                        public void onFinish(Object rst) {
                            msgWrap.msg = new Gson().toJson(rst);
                            conn.send(new Gson().toJson(msgWrap));
                        }
                    });
                }
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                Lg.i("onError: ");
            }
        };
    }

    public void start() {
        mServer.start();
    }

}