package com.example.chattomate.interfaces;

import org.json.JSONObject;

public interface SocketCallBack {
    void onNewMessage(JSONObject data);
    void onNewFriendRequest(JSONObject data);
    void onNewConversation(JSONObject data);
    void onNewFriend(JSONObject data);
    void onConversationChange(JSONObject data);
    void onFriendActiveChange(JSONObject data);
    void onTyping(JSONObject data);
}
//mSocket.on(Config.NEW_MASSAGE,onNewMessage);
//        mSocket.on(Config.NEW_FRIEND_REQUEST,onNewFriendRequest);
//        mSocket.on(Config.NEW_CONVERSATION,onNewConversation);
//        mSocket.on(Config.NEW_FRIEND,onNewFriend);
//        mSocket.on(Config.CONVERSATION_CHANGE,onConversationChange);
//        mSocket.on(Config.FRIEND_CHANGE,onFriendActiveChange);
//        mSocket.on(Config.TYPING,onTyping);