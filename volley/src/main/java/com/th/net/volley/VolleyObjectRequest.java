package com.th.net.volley;


import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.th.anlib.Lg;
import com.th.net.Thyi;

import java.util.Map;

import rx.Subscriber;

import static com.sun.xml.internal.ws.api.message.Packet.Status.Request;
import static com.sun.xml.internal.ws.api.message.Packet.Status.Response;


/**
 * Created by yi on 2/22/16.
 */
public class VolleyObjectRequest<T> extends Request<T>{
    private Class<T> clazz;         
    private Map<String, String> params;
    private Subscriber<? super  T> subscriber;

    public VolleyObjectRequest(int method, String url, Class<T> clazz, Map<String, String> params, Subscriber<? super T> subscriber) {
        super(method, url, null);
        this.clazz = clazz;
        this.subscriber = subscriber;
        this.params = params;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.i(Thyi.TAG, "parseNetworkResponse(), for url: " + getUrl() + "\njson: " + json);
            return Response.success(new Gson().fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            Lg.e(Thyi.TAG, "parse json error: " + e);
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    protected void deliverResponse(T response) {
        subscriber.onNext(response);
        subscriber.onCompleted();
    }

    @Override
    public void deliverError(VolleyError error) {
        Lg.i(Thyi.TAG, "error: " + error.getMessage());
        Throwable t = error;
        if (t == null) {
            t = new Exception(error);
        }
        subscriber.onError(t);
    }

}
