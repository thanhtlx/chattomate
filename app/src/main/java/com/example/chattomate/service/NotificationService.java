package com.example.chattomate.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
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
        createNotificationChannel(Config.CHANNEL_NOTIFICATION_NEW_MESSAGE, Config.CHANNEL_NOTIFICATION_NEW_MESSAGE, Config.CHANNEL_NOTIFICATION_NEW_MESSAGE);
    }

    public void pushNotification(String CHANNEL_ID, int notificationID,String textContent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("My notification")
                .setContentText(textContent)
                .setSmallIcon(R.drawable.logo)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationID, builder.build());
    }

    private void createNotificationChannel(String CHANNEL_ID, String channel_name, String channel_description) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channel_name;
            String description = channel_description;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
