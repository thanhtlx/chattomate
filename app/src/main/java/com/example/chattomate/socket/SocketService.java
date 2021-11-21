package com.example.chattomate.socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.SocketCallBack;
import com.example.chattomate.service.NotificationService;

import org.json.JSONObject;

import java.net.URI;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

public class SocketService extends Service {
    private static String TAG = "DEBUG_SOCKET_";
    private Socket mSocket;
    private Context context;
    private AppPreferenceManager manager;
    private NotificationService notificationService;
    private SocketCallBack socketCallBack = new SocketCallBack() {
        @Override
        public void onNewMessage(JSONObject data) {

        }

        @Override
        public void onNewFriendRequest(JSONObject data) {

        }

        @Override
        public void onNewConversation(JSONObject data) {

        }

        @Override
        public void onNewFriend(JSONObject data) {

        }

        @Override
        public void onConversationChange(JSONObject data) {

        }

        @Override
        public void onFriendActiveChange(JSONObject data) {

        }

        @Override
        public void onTyping(JSONObject data) {

        }
    };



    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
//            notification
            JSONObject data = (JSONObject) args[0];

            notificationService.pushNotification(
                    Config.CHANNEL_NOTIFICATION_NEW_MESSAGE,
                    Config.ID_NOTIFICATION_NEW_MESSAGE,
                    "new message");
            socketCallBack.onNewMessage(data);

        }
    };
    private Emitter.Listener onNewFriendRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onNewFriendRequest((JSONObject) args[0]);
        }
    };
    private Emitter.Listener onNewConversation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onNewConversation((JSONObject) args[0]);
        }
    };
    private Emitter.Listener onNewFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onNewFriend((JSONObject) args[0]);
        }
    };
    private Emitter.Listener onConversationChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onNewConversation((JSONObject) args[0]);
        }
    };
    private Emitter.Listener onFriendActiveChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onFriendActiveChange((JSONObject) args[0]);
        }
    };
    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onTyping((JSONObject) args[0]);
        }
    };

    public SocketService(Context context) {
        this.context = context;
        manager = new AppPreferenceManager(context);
        notificationService = new NotificationService(context);
        initSocket();
    }

    private void initSocket() {
        IO.Options opts = new IO.Options();
        opts.query = "token=" + manager.getToken(context);
        opts.transports = new String[]{WebSocket.NAME};
        mSocket = IO.socket(URI.create(Config.HOST), opts);
        Log.d(TAG,"init socket");
        listening();
        mSocket.connect();
    }

    private void listening() {
        mSocket.on(Config.NEW_MASSAGE,onNewMessage);
        mSocket.on(Config.NEW_FRIEND_REQUEST,onNewFriendRequest);
        mSocket.on(Config.NEW_CONVERSATION,onNewConversation);
        mSocket.on(Config.NEW_FRIEND,onNewFriend);
        mSocket.on(Config.CONVERSATION_CHANGE,onConversationChange);
        mSocket.on(Config.FRIEND_CHANGE,onFriendActiveChange);
        mSocket.on(Config.TYPING,onTyping);
    }

    public void setSocketCallBack(SocketCallBack socketCallBack) {
        this.socketCallBack = socketCallBack;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off(Config.NEW_MASSAGE,onNewMessage);
        mSocket.off(Config.NEW_FRIEND_REQUEST,onNewFriendRequest);
        mSocket.off(Config.NEW_CONVERSATION,onNewConversation);
        mSocket.off(Config.NEW_FRIEND,onNewFriend);
        mSocket.off(Config.CONVERSATION_CHANGE,onConversationChange);
        mSocket.off(Config.FRIEND_CHANGE,onFriendActiveChange);
        mSocket.off(Config.TYPING,onTyping);
    }
}
