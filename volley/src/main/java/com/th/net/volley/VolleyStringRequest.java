package com.th.net.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.th.net.Thyi;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;

/**
 * Created by yi on 2/22/16.
 */
public class VolleyStringRequest extends StringRequest {
    private Map<String, String> params;

    public VolleyStringRequest(int method, String url, Map<String, String> params, final Subscriber<? super String> subscriber) {
        super(method, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(Thyi.TAG, "success: " + response);
                    subscriber.onNext(response);
                    subscriber.onCompleted();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(Thyi.TAG, "error: " + error.getMessage());
                    Throwable t = error;
                    if (t == null) {
                        t = new Exception(t);
                    }
                    subscriber.onError(t);
                }
            }
        );
        this.params = params;
    }

    @Override
    public Map<String, String> getParams() {
        Log.i(Thyi.TAG, "params: " + params);
        return params;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Content-Type","application/x-www-form-urlencoded");
        return headers;
    }
}
