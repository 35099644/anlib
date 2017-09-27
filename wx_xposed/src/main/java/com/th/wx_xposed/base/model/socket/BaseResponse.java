package com.th.wx_xposed.base.model.socket;

/**
 * Created by th on 25/9/2017.
 */

public class BaseResponse {
    public State state;

    public BaseRequest request;

    /**
     * 是否是最后一次回调
     *
     * 现在的逻辑是，如果是最后一次。则将之前注册的回调清掉
     */
    public boolean isLast = true;

    public BaseResponse() {
    }

    public BaseResponse(BaseRequest request) {
        this.request = request;
        this.state = new State(0, "成功！");
    }

    public static class State {
        public int err_code = 0;
        public String err_msg = "";

        public State(int err_code, String err_msg) {
            this.err_code = err_code;
            this.err_msg = err_msg;
        }

        public static int ERR_NO_HANDLER = -1;
        public static String MSG_NO_HANDLER = "不能处理这种类型的请求";

        public static int ERR_NOT_CONNECTED = -2;
        public static String MSG_NOT_CONNECTED = "Socket没有连接上";

        @Override
        public String toString() {
            return "State{" +
                    "err_code=" + err_code +
                    ", err_msg='" + err_msg + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "state=" + state +
                "request=" + request +
                '}';
    }
}
