package com.example.chattomate;

import android.app.Application;
import android.util.Log;

import com.example.chattomate.socket.SocketController;
import com.example.chattomate.socket.SocketService;

public class App  extends Application {
    private static App instance;
    private static SocketService socket;

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication();
        socket = new SocketService(instance);
        Log.d("DEBUG"," start application");
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
