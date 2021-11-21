package com.example.chattomate.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chattomate.App;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.config.Config;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class FCMService extends FirebaseMessagingService {
    public FCMService() {
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        Log.d("DEBUG","Receiver mesaage");
//        notify nay test thoi

        NotificationService notificationService = new NotificationService(this);
        Intent intent = new Intent(this.getApplicationContext(), ChatActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), 0, intent, 0);
        notificationService.pushNotification(Config.CHANNEL_NOTIFICATION_NEW_MESSAGE,Config.ID_NOTIFICATION_NEW_MESSAGE,"title","content",pendingIntent);
        App.getInstance();
    }

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
        Log.d("DEBUG-new token", s);
//        upload len server thoi :(
    }

}