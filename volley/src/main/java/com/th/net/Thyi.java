package com.th.net;

import android.content.Context;

import com.th.anlib.Lg;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import thnet.thanlib.com.thyi.volley.VolleyObjectRequest;
import thnet.thanlib.com.thyi.volley.VolleyStringRequest;

/**
 * Created by yi on 2/22/16.
 */
public class Thyi {
    public static final String TAG = "Thyi";

    public static final int GET = 0;
    public static final int POST = 1;
    private static RequestQueue requestQueue;

    public static void init(Context context) {
        if (context == null) {
            throw new RuntimeException("Context can't be null");
        }
        initRequestQueue(context.getApplicationContext());
    }

    public static Observable<String> request(String url) {
        return request(GET, url, null, String.class);
    }

    public static <T>Observable<T> request(String url, Class<T> clazz) {
        return request(POST, url, null, clazz);
    }

    public static <T>Observable<T> request(String url, Map<String, String> params, Class<T> clazz) {
        return request(POST, url, params, clazz);
    }

    public static <T>Observable<T> request(int method, String url, Map<String, String> params, Class<T> clazz) {
        return requestInternal(method, url, params, clazz);
    }

    // MARK: private
    private static <T>Observable<T> requestInternal(final int method, final String url,
                                                    final Map<String, String> params, final Class<T> clazz) {
        Lg.i(TAG, "begin " + (method == GET? "Get" : "Post") + " request, url: " + url + ", params: " + params);

        if (requestQueue == null) {
            throw new RuntimeException("Must call init(context) before use");
        }

        Observable.OnSubscribe<T> onSubsribe = new Observable.OnSubscribe<T>() {
            @SuppressWarnings("unchecked")
            @Override
            public void call(final Subscriber<? super T> subscriber) {
                Request<T> request;
                String tmpUrl = url;
                if (method == GET) {
                    tmpUrl = packageGetParam(url, params);
                }

                if (clazz == String.class) {
                    request = (Request<T>) new VolleyStringRequest(getRequestMethod(method), tmpUrl, params, (Subscriber<? super String>) subscriber);
                } else {
                    request = new VolleyObjectRequest<>(getRequestMethod(method), tmpUrl, clazz, params, subscriber);
                }

                request.setRetryPolicy(new DefaultRetryPolicy(5000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                requestQueue.add(request);
            }
        };
        return Observable.create(onSubsribe).observeOn(AndroidSchedulers.mainThread());
    }

    private static String packageGetParam(String url, Map<String, String> params) {
        if (params != null && params.size() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(url).append("?");
            for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
                sb.append(stringStringEntry.getKey()).append("=").append(stringStringEntry.getValue()).append("&");
            }
            url = sb.toString();
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    private static void initRequestQueue(Context context) {
        if (requestQueue == null) {
            synchronized (Thyi.class) {
                if (requestQueue == null) {
                    requestQueue = Volley.newRequestQueue(context);
                }
            }
        }
    }

    private static int getRequestMethod(int method) {
        switch (method) {
            case Thyi.GET:
                return Request.Method.GET;
            case Thyi.POST:
                return Request.Method.POST;
            default:
                return Request.Method.POST;
        }
    }

}
