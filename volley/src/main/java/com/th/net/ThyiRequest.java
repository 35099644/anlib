package com.th.net;

import rx.Observable;

/**
 * Created by qiliantao on 3/18/16.
 */
public class ThyiRequest<T> extends Observable<T>{
    private boolean showToast;
    private boolean showLoadingDialog;

    protected ThyiRequest(OnSubscribe<T> f) {
        super(f);
    }

}
