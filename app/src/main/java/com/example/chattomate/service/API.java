package com.example.chattomate.service;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chattomate.interfaces.APICallBack;

import org.json.JSONObject;

import java.util.Map;

public class API {
    private Context context = null;

    public API(Context context) {
        this.context = context;
    }

    public void Call(int method, String url, JSONObject params, Map<String, String> headers, APICallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, params,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response);
                callBack.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                VolleyLog.d("TAG", "Error response: " + error.getMessage());
                try {
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    callBack.onError(data);

                } catch (Exception e) {
                    callBack.onError(null);
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if(headers == null) {
                    return super.getHeaders();
                }
                else return  headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this.context);
        queue.add(jsonObjectRequest);
    }
}
