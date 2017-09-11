package com.th.net;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static io.reactivex.Observable.create;

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
        return request(POST, url, params, clazz);
    }

    public <T>Observable<T> request(int method, String url, Map<String, String> params, Class<T> clazz) {
        return requestInternal(method, url, params, clazz);
    }

    public Observable<Bitmap> requestImage(final String url, final Map<String, String> param) {
        return request(GET, url, param, Bitmap.class);
    }

    private <T>Observable<T> requestInternal(final int method, final String url,
                                                    final Map<String, String> param, final Class<T> clazz) {
        FormBody.Builder postBuilder = new FormBody.Builder();

        String finalUrl = url;

        if (method == GET) {
            finalUrl = packageGetParam(url, param);
        } else {
            if (param != null) {
                for (String key : param.keySet()) {
                    postBuilder.add(key, param.get(key));
                }
            }
        }

        String refer = "";

        try {
            URL aURL = new URL(url);
            refer = aURL.getProtocol() + "://" + aURL.getHost();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Request.Builder rb = new Request.Builder()
                .url(finalUrl)
                .header("referer", refer)
                .header("user-agent", "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Mobile Safari/537.36");

        if (method == GET) {
            rb.get();
        } else {
            rb.post(postBuilder.build());
        }


        Request request = rb.build();

        return request(request, clazz);

    }

    public <T>Observable<T> request(final Request request, final Class<T> clazz) {
        Log.i(TAG, "send: " + request.url().toString());
        ObservableOnSubscribe<T> onSubsribe = new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<T> e) throws Exception {
                try {
                    Response response = okClient.newCall(request).execute();

                    if (clazz == Response.class) {
                        e.onNext((T) response);
                        e.onComplete();
                    } else if (clazz == Bitmap.class) {
                        InputStream inputStream = response.body().byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if (bitmap == null) {
                            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565);
                        }
                        e.onNext((T) bitmap);
                        e.onComplete();
                    } else {
                        String rst = response.body().string();
                        Log.i(TAG, "rst: " + rst + "\n\t for url: " + request.url() + "\n\t cookie: " + request.header("cookie") + "\n\t Cookie: " + request.header("Cookie"));
                        T bean;

                        if (clazz == String.class) {
                            bean = (T) rst;
                        } else if (clazz == JSONObject.class) {
                            bean = (T) new JSONObject(rst);
                        }

                        else {
                            bean = new Gson().fromJson(rst, clazz);
                        }

                        e.onNext(bean);
                        e.onComplete();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    e.onError(ex);
                    e.onComplete();
                }
            }
        };
        return create(onSubsribe).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
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
