package com.example.chattomate;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.socket.SocketController;
import com.example.chattomate.socket.SocketService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class App  extends Application {
    private static App instance;
    private static SocketService socket;

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
        AppPreferenceManager manager = new AppPreferenceManager(instance.getApplicationContext());
        if (manager.getUser() != null)
            socket = new SocketService(instance);
        Log.d("DEBUG"," start application");
        initFCM();
    }

    public  static void  initSocket() {
        socket = new SocketService(instance);
    }

    private void initFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                    }
                });
    }

    private void initApplication() {
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    public  SocketService getSocket(){
        return  socket;
    }
}