package com.th.wx_xposed.base.socket;

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
    public boolean isLast;

    public BaseResponse(BaseRequest request) {
        this.request = request;
    }

    public static class State {
        int err_code = 0;
        String err_msg = "";

        public State(int err_code, String err_msg) {
            this.err_code = err_code;
            this.err_msg = err_msg;
        }

        public static int ERR_NO_HANDLER = -1;
        public static String MSG_NO_HANDLER = "不能处理这种类型的请求";
    }

}
