package com.crisanto.kevin.picstant.helpers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyHandler {

    private static VolleyHandler mVolleyHandler;
    private static Context mContext;
    private RequestQueue mrequestQueue;

    private VolleyHandler(Context mContext) {
        this.mContext = mContext;
        this.mrequestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if(mrequestQueue == null){
            mrequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mrequestQueue;
    }

    public static synchronized VolleyHandler getInstance(Context mContext){
        if(mVolleyHandler == null) {
            mVolleyHandler = new VolleyHandler(mContext);
        }
        return mVolleyHandler;
    }

    public <T> void addRequestToQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
