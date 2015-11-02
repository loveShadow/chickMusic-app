package com.zhaoli.chickmusic.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaoli on 2015/10/18.
 */
public class SearchSongRequest extends StringRequest {

    Map<String, String> map = null;

    public SearchSongRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener,
                             Map<String, String> map) {
        super(method, url, listener, errorListener);
        this.map = map;
    }

    public SearchSongRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public SearchSongRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return (map == null) ? super.getParams() : map;
    }
}
