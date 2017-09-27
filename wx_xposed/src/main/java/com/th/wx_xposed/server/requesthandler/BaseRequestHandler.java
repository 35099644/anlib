package com.th.wx_xposed.server.requesthandler;

import com.google.gson.Gson;
import com.th.wx_xposed.base.model.socket.BaseRequest;
import com.th.wx_xposed.base.model.socket.BaseResponse;

import org.java_websocket.WebSocket;

/**
 * Created by th on 26/9/2017.
 */

public abstract class BaseRequestHandler {

    public abstract void handleRequest(WebSocket conn, String message);

    protected void responseSuccess(WebSocket conn, BaseRequest request) {
        conn.send(new Gson().toJson(new BaseResponse(request)));
    }


    protected void responseFailed(WebSocket conn, BaseRequest request, int errCode, String errMsg) {
        BaseResponse baseResponse = new BaseResponse(request);
        baseResponse.state = new BaseResponse.State(errCode, errMsg);
        conn.send(new Gson().toJson(baseResponse));
    }

}
