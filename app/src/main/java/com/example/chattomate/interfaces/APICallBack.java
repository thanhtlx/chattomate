package com.example.chattomate.interfaces;

import org.json.JSONObject;

public interface APICallBack {
    void onSuccess(JSONObject result);
    void onError(JSONObject result);
}
