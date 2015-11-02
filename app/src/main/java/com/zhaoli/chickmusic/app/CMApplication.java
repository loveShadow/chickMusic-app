package com.zhaoli.chickmusic.app;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.zhaoli.chickmusic.base.cache.LruBitmapCache;

import de.greenrobot.event.EventBus;

/**
 * Created by zhaoli on 2015/10/16.
 */
public class CMApplication extends Application {

    public static final String TAG = CMApplication.class
            .getSimpleName();
    private static CMApplication mInstance = null;
    private ImageLoader imageLoader = null;
    private RequestQueue requestQueue = null;
    private EventBus eventBus = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized CMApplication getInstance() {
        return mInstance;
    }

    public EventBus getEventBus() {
        if (eventBus == null) {
            eventBus = new EventBus();
        }
        return eventBus;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null) {
            /**
             * 请求队列
             * 图片缓存
             */
            imageLoader = new ImageLoader(requestQueue, new LruBitmapCache());
        }
        return imageLoader;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
