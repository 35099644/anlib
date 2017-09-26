package com.th.wx_xposed.server.requesthandler;

import com.google.gson.Gson;
import com.th.wx_xposed.base.socket.BaseRequest;
import com.th.wx_xposed.base.socket.BaseResponse;

import org.java_websocket.WebSocket;

/**
 * Created by th on 26/9/2017.
 */

public class EmptyHandler extends BaseRequestHandler {

    @Override
    public void handleRequest(WebSocket conn, String message) {
        BaseResponse baseResponse = new BaseResponse(new Gson().fromJson(message, BaseRequest.class));
        baseResponse.state = new BaseResponse.State(BaseResponse.State.ERR_NO_HANDLER, BaseResponse.State.MSG_NO_HANDLER);

        conn.send(new Gson().toJson(baseResponse));
    }

}
