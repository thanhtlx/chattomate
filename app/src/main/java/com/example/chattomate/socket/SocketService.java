package com.example.chattomate.socket;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.chattomate.MainActivity;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.OnConversationChangeCallBack;
import com.example.chattomate.interfaces.OnFriendActiveChangeCallBack;
import com.example.chattomate.interfaces.OnMapChangeCallBack;
import com.example.chattomate.interfaces.OnNewConversationCallBack;
import com.example.chattomate.interfaces.OnNewFriendCallBack;
import com.example.chattomate.interfaces.OnNewMessageCallBack;
import com.example.chattomate.interfaces.OnTypingCallBack;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.Message;
import com.example.chattomate.service.NotificationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
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

    public void setOnConversationChangeCallBack(OnConversationChangeCallBack onConversationChangeCallBack) {
        this.onConversationChangeCallBack = onConversationChangeCallBack;
    }

    public void setOnFriendActiveChangeCallBack(OnFriendActiveChangeCallBack onFriendActiveChangeCallBack) {
        this.onFriendActiveChangeCallBack = onFriendActiveChangeCallBack;
    }

    public void setOnNewConversationCallBack(OnNewConversationCallBack onNewConversationCallBack) {
        this.onNewConversationCallBack = onNewConversationCallBack;
    }

    public void setOnNewFriendCallBack(OnNewFriendCallBack onNewFriendCallBack) {
        this.onNewFriendCallBack = onNewFriendCallBack;
    }

    public void setOnNewMessageCallBack(OnNewMessageCallBack onNewMessageCallBack) {
        this.onNewMessageCallBack = onNewMessageCallBack;
    }

    public void setOnTypingCallBack(OnTypingCallBack onTypingCallBack) {
        this.onTypingCallBack = onTypingCallBack;
    }

    public void setOnMapChangeCallBack(OnMapChangeCallBack onMapChangeCallBack) {
        this.onMapChangeCallBack = onMapChangeCallBack;
    }

    private OnConversationChangeCallBack onConversationChangeCallBack = new OnConversationChangeCallBack() {
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
                        data.getString("message"), pendingIntent);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DEBUG", "error paser json");
            }
        }
    };
    private OnFriendActiveChangeCallBack onFriendActiveChangeCallBack = new OnFriendActiveChangeCallBack() {
        @Override
        public void onFriendActiveChange(JSONObject data) {

        }
    };
    private OnNewConversationCallBack onNewConversationCallBack = new OnNewConversationCallBack() {
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
                        data.getString("message"), pendingIntent);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DEBUG", "error paser json");
            }
        }
    };
    private OnNewFriendCallBack onNewFriendCallBack = new OnNewFriendCallBack() {
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
                        data.getString("message"), pendingIntent);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DEBUG", "error paser json");
            }
        }
    };
    private OnNewMessageCallBack onNewMessageCallBack = new OnNewMessageCallBack() {
        @Override
        public void onNewMessage(JSONObject data) {
            JSONObject jsonObject = data;
            try {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("idConversation", jsonObject.getString("conversation"));
                intent.putExtra("nameConversation", jsonObject.getString("conversation"));
                intent.putExtra("idFriend", jsonObject.getJSONObject("sendBy").getString("_id"));
                intent.putExtra("member_number", jsonObject.getString("conversation"));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationService.pushNotification(
                        Config.CHANNEL_NOTIFICATION_NEW_MESSAGE,
                        Config.ID_NOTIFICATION_NEW_MESSAGE,
                        jsonObject.getJSONObject("sendBy").getString("name"),
                        jsonObject.getString("content"), pendingIntent);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("DEBUG", "error paser json");
            }
        }
    };
    private OnTypingCallBack onTypingCallBack = new OnTypingCallBack() {
        @Override
        public void onTyping(JSONObject data) {

        }
    };
    private OnMapChangeCallBack onMapChangeCallBack = new OnMapChangeCallBack() {
        @Override
        public void onMapChange(JSONObject data) {

        }
    };


    private final Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            onNewMessageCallBack.onNewMessage((JSONObject) args[0]);
            try {
                JSONObject data = (JSONObject) args[0];
                JSONObject object = data.getJSONObject("sendBy");
                String idConversation = data.getString("conversation");
                String content = data.getString("content");
                String contentUrl = data.getString("contentUrl");
                String idSender = object.getString("_id");
                Friend friend = manager.getFriend(manager.getFriends(), idSender);
                if (friend == null) {
                    friend = new Friend(object.getString("_id"),
                            object.getString("name"), object.getString("avatarUrl"));
                    friend.idApi = object.getString("idApi");
                }
                Message message = new Message(idConversation, data.getString("_id"),
                        content, contentUrl,
                        data.getString("createdAt"), null, friend, false,
                        data.getString("type"));
                manager.addMessage(message, idConversation);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    private final Emitter.Listener onNewFriendRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            onNewFriendCallBack.onNewFriendRequest((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onNewConversation = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            onNewConversationCallBack.onNewConversation((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onNewFriend = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            onNewFriendCallBack.onNewFriendRequest((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onConversationChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            onConversationChangeCallBack.onConversationChange((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onFriendActiveChange = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            onFriendActiveChangeCallBack.onFriendActiveChange((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, Arrays.toString(args));
            onTypingCallBack.onTyping((JSONObject) args[0]);
//            socketCallBack.onTyping((JSONObject) args[0]);
        }
    };
    private final Emitter.Listener onMapChange = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            onMapChangeCallBack.onMapChange((JSONObject) args[0]);
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
        Log.d(TAG, "init socket");
        listening();
        mSocket.connect();
    }

    private void listening() {
        mSocket.on(Config.NEW_MASSAGE, onNewMessage);
        mSocket.on(Config.NEW_FRIEND_REQUEST, onNewFriendRequest);
        mSocket.on(Config.NEW_CONVERSATION, onNewConversation);
        mSocket.on(Config.NEW_FRIEND, onNewFriend);
        mSocket.on(Config.CONVERSATION_CHANGE, onConversationChange);
        mSocket.on(Config.FRIEND_CHANGE, onFriendActiveChange);
        mSocket.on(Config.TYPING, onTyping);
        mSocket.on(Config.MAP_CHANGE, onMapChange);
    }


    public void unSetSocketCallBack() {
        initCallBack();
    }

    private void initCallBack() {
        onConversationChangeCallBack = new OnConversationChangeCallBack() {
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
                            data.getString("message"), pendingIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DEBUG", "error paser json");
                }
            }
        };
        onFriendActiveChangeCallBack = new OnFriendActiveChangeCallBack() {
            @Override
            public void onFriendActiveChange(JSONObject data) {

            }
        };
        onNewConversationCallBack = new OnNewConversationCallBack() {
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
                            data.getString("message"), pendingIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DEBUG", "error paser json");
                }
            }
        };
        onNewFriendCallBack = new OnNewFriendCallBack() {
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
                            data.getString("message"), pendingIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DEBUG", "error paser json");
                }
            }
        };
        onNewMessageCallBack = new OnNewMessageCallBack() {
            @Override
            public void onNewMessage(JSONObject data) {
                JSONObject jsonObject = data;
                try {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("idConversation", jsonObject.getString("conversation"));
                    intent.putExtra("nameConversation", jsonObject.getString("conversation"));
                    intent.putExtra("idFriend", jsonObject.getJSONObject("sendBy").getString("_id"));
                    intent.putExtra("member_number", jsonObject.getString("conversation"));
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationService.pushNotification(
                            Config.CHANNEL_NOTIFICATION_NEW_MESSAGE,
                            Config.ID_NOTIFICATION_NEW_MESSAGE,
                            jsonObject.getJSONObject("sendBy").getString("name"),
                            jsonObject.getString("content"), pendingIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DEBUG", "error paser json");
                }
            }
        };
        onTypingCallBack = new OnTypingCallBack() {
            @Override
            public void onTyping(JSONObject data) {

            }
        };
        onMapChangeCallBack = new OnMapChangeCallBack() {
            @Override
            public void onMapChange(JSONObject data) {

            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void emitNewCall(JSONObject jsonData) {
        Log.d("DEBUG-CALL", "emit new call");
        Log.d("DEBUG-CALL", String.valueOf(jsonData));
        mSocket.emit("new-call", jsonData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off(Config.NEW_MASSAGE, onNewMessage);
        mSocket.off(Config.NEW_FRIEND_REQUEST, onNewFriendRequest);
        mSocket.off(Config.NEW_CONVERSATION, onNewConversation);
        mSocket.off(Config.NEW_FRIEND, onNewFriend);
        mSocket.off(Config.CONVERSATION_CHANGE, onConversationChange);
        mSocket.off(Config.FRIEND_CHANGE, onFriendActiveChange);
        mSocket.off(Config.TYPING, onTyping);
    }
}