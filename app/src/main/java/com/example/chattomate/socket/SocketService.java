package com.example.chattomate.socket;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.chattomate.App;
import com.example.chattomate.MainActivity;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.activities.LoginActivity;
import com.example.chattomate.call.LoginService;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.SocketCallBack;
import com.example.chattomate.models.User;
import com.example.chattomate.service.NotificationService;
import com.quickblox.users.model.QBUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

public class SocketService extends Service {
    private static final String TAG = "DEBUG_SOCKET_";
    private Socket mSocket;
    private final Context context;
    private final AppPreferenceManager manager;
    private final NotificationService notificationService;
    private SocketCallBack socketCallBack = new SocketCallBack() {
        @Override
        public void onNewMessage(JSONObject data) {
            JSONObject jsonObject = data;
            try {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("conversationID", jsonObject.getString("conversation"));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationService.pushNotification(
                        Config.CHANNEL_NOTIFICATION_NEW_MESSAGE,
                        Config.ID_NOTIFICATION_NEW_MESSAGE,
                        jsonObject.getJSONObject("sendBy").getString("name"),
                        jsonObject.getString("content"),pendingIntent);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DEBUG","error paser json");
            }
        }

        @Override
        public void onNewFriendRequest(JSONObject data) {
            try {
                Intent intent = new Intent(context, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.putExtra("conversationID", jsonObject.getString("conversation"));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationService.pushNotification(
                        Config.CHANNEL_NOTIFICATION_NEW_FRIEND_REQUEST,
                        Config.ID_NOTIFICATION_NEW_FRIEND_REQUEST,
                        "new Friend Request",
                        data.getString( "message"),pendingIntent);
                socketCallBack.onNewMessage(data);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DEBUG","error paser json");
            }
        }

        @Override
        public void onNewConversation(JSONObject data) {
            try {
                Intent intent = new Intent(context, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.putExtra("conversationID", jsonObject.getString("conversation"));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationService.pushNotification(
                        Config.CHANNEL_NOTIFICATION_NEW_CONVERSATION,
                        Config.ID_NOTIFICATION_NEW_CONVERSATION,
                        "new Conversation",
                        data.getString( "message"),pendingIntent);
                socketCallBack.onNewMessage(data);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DEBUG","error paser json");
            }
        }

        @Override
        public void onNewFriend(JSONObject data) {
            try {
                Intent intent = new Intent(context, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.putExtra("conversationID", jsonObject.getString("conversation"));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationService.pushNotification(
                        Config.CHANNEL_NOTIFICATION_NEW_FRIEND,
                        Config.ID_NOTIFICATION_NEW_FRIEND,
                        "new Friend",
                        data.getString( "message"),pendingIntent);
                socketCallBack.onNewMessage(data);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DEBUG","error paser json");
            }
        }

        @Override
        public void onConversationChange(JSONObject data) {
            try {
                Intent intent = new Intent(context, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.putExtra("conversationID", jsonObject.getString("conversation"));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                notificationService.pushNotification(
                        Config.CHANNEL_NOTIFICATION_NEW_CONVERSATION,
                        Config.ID_NOTIFICATION_NEW_CONVERSATION,
                        "conversation change",
                        data.getString( "message"),pendingIntent);
                socketCallBack.onNewMessage(data);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DEBUG","error paser json");
            }
        }

        @Override
        public void onFriendActiveChange(JSONObject data) {
        }

        @Override
        public void onTyping(JSONObject data) {

        }
    };
    private SocketCallBack defauleSocketCallBack = socketCallBack;


    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
//            notification
            socketCallBack.onNewMessage((JSONObject) args[0]);

        }
    };
    private final Emitter.Listener onNewFriendRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onNewFriendRequest((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onNewConversation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onNewConversation((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onNewFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onNewFriend((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onConversationChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onNewConversation((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onFriendActiveChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onFriendActiveChange((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            socketCallBack.onTyping((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onInComingCall = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("DEBUG-CALL", Arrays.toString(args));
            User user = manager.getUser();
            QBUser qbUser = new QBUser(user.email, App.USER_DEFAULT_PASSWORD);
            qbUser.setId(Integer.parseInt(user.idApi));
            LoginService.start(getApplicationContext(), qbUser);
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
        mSocket.on(Config.IMCOMINGCALL,onInComingCall);
    }

    public void setSocketCallBack(SocketCallBack socketCallBack) {
        this.socketCallBack = socketCallBack;
    }
    public void unSetSocketCallBack(){
        this.socketCallBack = this.defauleSocketCallBack;
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