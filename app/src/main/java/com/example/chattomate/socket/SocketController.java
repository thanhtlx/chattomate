package com.example.chattomate.socket;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chattomate.R;
import com.example.chattomate.activities.LoginActivity;
import com.example.chattomate.config.Config;
import com.example.chattomate.models.Message;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketController  extends AppCompatActivity {
    private static Socket socket;
    public static String SERVER_IP = "";
    public static final int SERVER_PORT = 8888;
    private EditText messageSend;
    private static String TAG = "DEBUG_SOCKET_";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        messageSend = findViewById(R.id.edtMessage);

        initSocket();
    }

    public void initSocket() {
        Log.d(TAG,"init socket");
        try {
            SERVER_IP = getLocalIpAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        URI uri = URI.create(Config.SOCKET_URL);

        IO.Options options = IO.Options.builder()
                .setPath("/socket.io/")
                .setQuery(LoginActivity.AUTH_TOKEN)
                .build();

        socket = IO.socket(uri, options);

        socket.on(Config.NEW_MASSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG,args.toString());

            }
        }).on(Config.NEW_FRIEND_REQUEST, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG,args.toString());
            }
        }).on(Config.NEW_FRIEND, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG,args.toString());
            }
        }).on(Config.NEW_CONVERSATION, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG,args.toString());
            }
        });

        socket.connect();

    }

    public void sendMessage() {
        String message = messageSend.getText().toString();
        if(TextUtils.isEmpty(message)) return;
        Message newMess = new Message();
        newMess.content = message;

        messageSend.setText("");
        socket.emit(Config.NEW_MASSAGE, message);
    }

    private String getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }

}
