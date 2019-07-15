package com.rafalczernecki.meteodata;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MeteoDataApplication {
    private static MeteoDataApplication instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private MeteoDataApplication(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized MeteoDataApplication getInstance(Context context) {
        if (instance == null) {
            instance = new MeteoDataApplication(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
