package com.example.chattomate.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chattomate.R;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.config.Config;

public class ReceiverInput extends BroadcastReceiver {
    private String KEY_TEXT_REPLY = "key_text_reply";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle =  RemoteInput.getResultsFromIntent(intent);

        if (bundle != null){
//            sernd messsage
            createNoti("",context);
        }
    }



    @SuppressLint("WrongConstant")
    private void createNoti(String chat,Context context ) {

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);


        Bundle bundle = new Bundle();
        bundle.putString("chat", chat);
        Intent intent = new Intent(context, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        intent.putExtra("bundle", bundle);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification build = new NotificationCompat.Builder(context, Config.CHANNEL_NOTIFICATION_NEW_MESSAGE)
            .setSmallIcon(R.drawable.logo)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        notificationManagerCompat.notify(Config.ID_NOTIFICATION_NEW_MESSAGE, build);

    }
}