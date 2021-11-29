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