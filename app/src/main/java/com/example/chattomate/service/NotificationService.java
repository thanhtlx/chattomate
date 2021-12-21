package com.example.chattomate.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.chattomate.R;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.config.Config;
import com.example.chattomate.models.Message;


public class NotificationService {
    private static final String KEY_TEXT_REPLY = "reply";
    private Context context;

    public NotificationService(Context context) {
        this.context = context;
        initChanel();
    }

    private void initChanel() {
        createNotificationChannel(Config.CHANNEL_NOTIFICATION_NEW_FRIEND,
                Config.CHANNEL_NOTIFICATION_NEW_FRIEND,
                Config.CHANNEL_NOTIFICATION_NEW_FRIEND);
        createNotificationChannel(Config.CHANNEL_NOTIFICATION_NEW_FRIEND_REQUEST,
                Config.CHANNEL_NOTIFICATION_NEW_FRIEND_REQUEST,
                Config.CHANNEL_NOTIFICATION_NEW_FRIEND_REQUEST);
        createNotificationChannel(Config.CHANNEL_NOTIFICATION_NEW_CONVERSATION,
                Config.CHANNEL_NOTIFICATION_NEW_CONVERSATION,
                Config.CHANNEL_NOTIFICATION_NEW_CONVERSATION);
    }

    public void pushNotification(String CHANNEL_ID,
                                 int notificationID,
                                 String title, String bigText,
                                 PendingIntent intent) {
        if (bigText.equals("0,0"))
            bigText = "sharing location";
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(title)
                        .setSmallIcon(R.drawable.logo)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(bigText))
                        .setContentIntent(intent)
                        .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" +context.getPackageName()+"/"+R.raw.sound_noti))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(notificationID, builder.build());
    }

    private void createNotificationChannel(String CHANNEL_ID, String channel_name, String channel_description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.sound_noti);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channel_name;
            String description = channel_description;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(sound, attributes);
            channel.enableVibration(true);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @SuppressLint({"NewApi", "LocalSuppress", "WrongConstant"})
    private void createNotification_mes(Message messager ) {

        String content = "";
        switch (messager.type) {
            case  "1" :
                content = messager.content;
            break;
            case  "7" :
                content = "share location";
                break;
        }
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        Intent isend = new Intent("actionsenddata");
        LocalBroadcastManager.getInstance(context).sendBroadcast(isend);
        Bundle bundle = new Bundle();
        bundle.putString("conversationID", messager.conversation);
        Intent intent1 = new Intent(context,ChatActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent1.putExtra("bundle", bundle);
        Uri uri =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.sound_noti);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            int important = NotificationManagerCompat.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Config.CHANNEL_NOTIFICATION_NEW_MESSAGE, Config.CHANNEL_NOTIFICATION_NEW_MESSAGE, important);
            channel.setDescription(Config.CHANNEL_NOTIFICATION_NEW_MESSAGE);
            channel.setSound(uri, attributes);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        Intent iReply = new Intent(context, ReceiverInput.class);
        iReply.putExtra("id", 2);

        PendingIntent pendingIntentReply = PendingIntent.getBroadcast(context, 0, iReply, 0);


        RemoteInput remoteInput2 = new RemoteInput.Builder(KEY_TEXT_REPLY).setLabel("Reply").build();
        Notification.Action action2 = new Notification.Action(
                R.drawable.ic_baseline_send_24,
                "Reply",
                pendingIntentReply
        );
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
             Notification build2 = new Notification.Builder(context, Config.CHANNEL_NOTIFICATION_NEW_MESSAGE)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(messager.sendBy.name)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addAction(action2)
                     .build();
            
            notificationManager.notify(Config.ID_NOTIFICATION_NEW_MESSAGE,build2);
        }
    }

}
