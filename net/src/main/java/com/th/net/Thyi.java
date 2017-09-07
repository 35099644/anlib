package com.th.net;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yi on 2/22/16.
 */
public class Thyi {
    public static final String TAG = "Thyi";

    public static final int GET = 0;
    public static final int POST = 1;
    private OkHttpClient okClient;


    public void init(CookieJar cookieJar) {
        if (cookieJar == null) {
            cookieJar = CookieJar.NO_COOKIES;
        }
        okClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
    }

    public <T>Observable<T> request(String url, Map<String, String> params, Class<T> clazz) {
        return requestInternal(POST, url, params, clazz);
    }

    public <T>Observable<T> request(int method, String url, Map<String, String> params, Class<T> clazz) {
        return requestInternal(method, url, params, clazz);
    }

    public Observable<Bitmap> requestImage(String url, Map<String, String> param) {
        ObservableOnSubscribe<Bitmap> onSubsribe = new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                FormBody.Builder builder = new FormBody.Builder();
                if (param != null) {
                    for (String key : param.keySet()) {
                        builder.add(key, param.get(key));
                    }
                }

                String finalUrl = url;
                if (method == GET) {
                    finalUrl = packageGetParam(url, param);
                }


                Request.Builder rb = new Request.Builder()
                        .url(finalUrl)
                        .post(builder.build());

                Request request = rb.build();
                Log.i(TAG, "send " + finalUrl + ", param: " + param);

                try {
                    Response response = okClient.newCall(request).execute();
                    String rst = response.body().string();
                    T bean;
                    if (clazz == String.class) {
                        bean = (T) rst;
                    } else {
                        bean = new Gson().fromJson(rst, clazz);
                    }
                    e.onNext(bean);
                    e.onComplete();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    e.onError(ex);
                    e.onComplete();
                }
            }
        };
        return Observable.create(onSubsribe).observeOn(AndroidSchedulers.mainThread());
    }

    private <T>Observable<T> requestInternal(final int method, final String url,
                                                    final Map<String, String> param, final Class<T> clazz) {
        ObservableOnSubscribe<T> onSubsribe = new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                FormBody.Builder builder = new FormBody.Builder();

                String finalUrl = url;
                if (method == GET) {
                    finalUrl = packageGetParam(url, param);
                } else {
                    if (param != null) {
                        for (String key : param.keySet()) {
                            builder.add(key, param.get(key));
                        }
                    }
                }


                Request.Builder rb = new Request.Builder()
                        .url(finalUrl)
                        .post(builder.build());

                Request request = rb.build();
                Log.i(TAG, "send " + finalUrl + ", param: " + param);

                try {
                    Response response = okClient.newCall(request).execute();
                    String rst = response.body().string();
                    T bean;
                    if (clazz == String.class) {
                        bean = (T) rst;
                    } else {
                        bean = new Gson().fromJson(rst, clazz);
                    }
                    e.onNext(bean);
                    e.onComplete();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    e.onError(ex);
                    e.onComplete();
                }
            }
        };
        return Observable.create(onSubsribe).observeOn(AndroidSchedulers.mainThread());
    }

    private String packageGetParam(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) return url;

        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (!url.contains("?")) {
            sb.append("?");
        }

        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        url = sb.toString();
        url = url.substring(0, url.length() - 1);
        return url;
    }

}
