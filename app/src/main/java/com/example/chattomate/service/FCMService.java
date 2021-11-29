package com.example.chattomate.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.example.chattomate.App;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    public static final String R_FB_T_URL = Config.HOST + Config.REGISTER_FB_TOKEN_URL;
    public static final String U_R_FB_T_URL = Config.HOST + Config.UN_REGISTER_FB_TOKEN_URL;
    private  static  Map<String, String> token = new HashMap<>();

    private AppPreferenceManager manager;
    private API api;
    public FCMService() {
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        Log.d("DEBUG","Receiver mesaage");
        NotificationService notificationService = new NotificationService(this);
        Intent intent = new Intent(this.getApplicationContext(), ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
        notificationService.pushNotification(Config.CHANNEL_NOTIFICATION_NEW_MESSAGE,Config.ID_NOTIFICATION_NEW_MESSAGE,"title","content",pendingIntent);
        App.getInstance();
    }

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        if(manager == null) {
            manager = new AppPreferenceManager(getApplicationContext());
        }
        if (api  == null) {
            api = new API(getApplicationContext());
        }
        Log.d("DEBUG-new token", s);
        manager.setFBToken(s);
        if(manager.getUser() == null) {
            return;
        }
        if(token.size() == 0){
            token.put("auth-token", manager.getToken(getApplicationContext()));
        }
        JSONObject data = new JSONObject();
        try {
            data.put("fcm_token", manager.getFBToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
         if (manager.getFBToken() != "") {
            api.Call(Request.Method.POST, U_R_FB_T_URL, data,token, new APICallBack() {
                @Override
                public void onSuccess(JSONObject result) {
                    Log.d("DEBUG","un registoken success");
                }
                @Override
                public void onError(JSONObject result) {
                }
            });
        }
        api.Call(Request.Method.POST, R_FB_T_URL, data,token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("DEBUG","registoken success");
            }
            @Override
            public void onError(JSONObject result) {
            }
        });

    }



}