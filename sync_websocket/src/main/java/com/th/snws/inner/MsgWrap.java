package com.th.snws.inner;

/**
 * 为传递的消息加上id
 * Created by york on 11/07/2017.
 */

public class MsgWrap {
    /**
     * 消息id
     */
    public int id;

    /**
     * 消息实际内容
     */
    public String msg;

    public MsgWrap(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

}
