package com.epyco.matchup.helper;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.epyco.matchup.R;

public class AppController extends Application{

    private static AppController mInstance;
    private RequestQueue mRequestQueue;

    public static synchronized AppController getInstance(){
        return mInstance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag){
        if(mRequestQueue != null){
            mRequestQueue.cancelAll(tag);
        }
    }

    public String handleVolleyError(VolleyError error){
        if(error instanceof NetworkError){
            return getString(R.string.errorRequestNetwork);
        }else if(error instanceof ServerError){
            return getString(R.string.errorRequestServer);
        }else if(error instanceof AuthFailureError){
            return getString(R.string.errorRequestAuth);
        }else if(error instanceof ParseError){
            return getString(R.string.errorRequestParse);
        }else if(error instanceof TimeoutError){
            return getString(R.string.errorRequestTimeout);
        }else{
            return getString(R.string.errorRequestOther);
        }
    }
}
