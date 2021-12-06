package com.example.chattomate.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.chattomate.R;
import com.example.chattomate.config.Config;

public class NotificationService {
    private Context context;

    public NotificationService(Context context) {
        this.context = context;
        initChanel();
    }

    private void initChanel() {
        createNotificationChannel(Config.CHANNEL_NOTIFICATION_NEW_MESSAGE,
                Config.CHANNEL_NOTIFICATION_NEW_MESSAGE,
                Config.CHANNEL_NOTIFICATION_NEW_MESSAGE);
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
}
