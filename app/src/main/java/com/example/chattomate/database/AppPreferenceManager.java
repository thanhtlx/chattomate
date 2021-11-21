package com.example.chattomate.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.chattomate.models.Conversation;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AppPreferenceManager {
    private static String TAG = AppPreferenceManager.class.getSimpleName();
    private SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME       = "chattomate_chat";
    private static final String IS_LOGGED_IN    = "isLoggedIn";
    private static final String PHONE           = "phone";
    private static final String AVATAR_URL      = "avatarUrl";
    private static final String NAME            = "name";
    private static final String EMAIL           = "email";
    private static final String PASSWORD        = "password";
    private static final String ALL_FRIEND      = "friends";
    private static final String REQUEST_FRIEND  = "request_friends";
    private static final String PENDING_FRIEND  = "pending_friends";
    private static final String ALL_CONVERSATION= "conversations";
    private static final String TOKEN= "token";
    private String TIME_TOKEN = "time token";

    public AppPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveToken(String token) {
        editor.putString(TOKEN, token);
        editor.commit();
    }
    public String getToken() {
        String token = pref.getString(TOKEN, "");
        return token;
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn).commit();
        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public void storeUser(User user) {
        editor.putString(PHONE, user.phone);
        editor.putString(AVATAR_URL, user.avatarUrl);
        editor.putString(NAME, user.name);
        editor.putString(EMAIL, user.email);
        editor.putString(PASSWORD, user.password);
        editor.commit();
    }

    public User getUser() {
        if (pref.getString(PHONE, null) != null) {
            String phone, avatar, name, password, email;
            phone = pref.getString(PHONE, null);
            avatar = pref.getString(AVATAR_URL, null);
            name = pref.getString(NAME, null);
            email = pref.getString(EMAIL, null);
            password = pref.getString(PASSWORD, null);

            return new User(name, avatar, phone, email, password);
        } else return null;
    }

    public void storeFriends(ArrayList<Friend> friends) {
        Gson gson = new Gson();
        String str = gson.toJson(friends);
        editor.putString(ALL_FRIEND, str).commit();
    }

    public ArrayList<Friend> getFriends() {
        String str = pref.getString(ALL_FRIEND, "");
        Gson gson = new Gson();
        Type friendList = new TypeToken<ArrayList<Friend>>(){}.getType();
        ArrayList<Friend> friends = gson.fromJson(str, friendList);
        return friends;
    }

    public Friend getFriend(ArrayList<Friend> friends, String id) {
        for(Friend friend : friends) {
            if(friend.friend._id.equals(id)) return friend;
        }
        return null;
    }

    public void addFriend(Friend friend) {
        ArrayList<Friend> friends = getFriends();
        friends.add(friend);
        storeFriends(friends);
    }

    public void deleteFriend(Friend friend) {
        ArrayList<Friend> friends = getFriends();
        friends.remove(friend);
        storeFriends(friends);
    }

    public void storeRequestFriend(ArrayList<Friend> friends) {
        Gson gson = new Gson();
        String str = gson.toJson(friends);
        editor.putString(REQUEST_FRIEND, str).commit();
    }

    public ArrayList<Friend> getRequestFriends() {
        String str = pref.getString(REQUEST_FRIEND, "");
        Gson gson = new Gson();
        Type friendList = new TypeToken<ArrayList<Friend>>(){}.getType();
        ArrayList<Friend> friends = gson.fromJson(str, friendList);
        return friends;
    }

    public void addRequestFriends(Friend friend) {
        ArrayList<Friend> friends = getRequestFriends();
        friends.add(friend);
        storeRequestFriend(friends);
    }

    public void deleteRequestFriends(Friend friend) {
        ArrayList<Friend> friends = getRequestFriends();
        friends.remove(friend);
        storeRequestFriend(friends);
    }

    public void storePendingFriends(ArrayList<Friend> friends) {
        Gson gson = new Gson();
        String str = gson.toJson(friends);
        editor.putString(PENDING_FRIEND, str).commit();
    }

    public ArrayList<Friend> getPendingFriends() {
        String str = pref.getString(PENDING_FRIEND, "");
        Gson gson = new Gson();
        Type friendList = new TypeToken<ArrayList<Friend>>(){}.getType();
        ArrayList<Friend> friends = gson.fromJson(str, friendList);
        return friends;
    }

    public void addPendingFriends(Friend friend) {
        ArrayList<Friend> friends = getPendingFriends();
        friends.add(friend);
        storePendingFriends(friends);
    }

    public void deletePendingFriends(Friend friend) {
        ArrayList<Friend> friends = getPendingFriends();
        friends.remove(friend);
        storePendingFriends(friends);
    }

    public void storeConversation(ArrayList<Conversation> conversations) {
        Gson gson = new Gson();
        String str = gson.toJson(conversations);
        editor.putString(ALL_CONVERSATION, str).commit();
    }

    public ArrayList<Conversation> getConversations() {
        String str = pref.getString(ALL_CONVERSATION, "");
        Gson gson = new Gson();
        Type list = new TypeToken<ArrayList<Conversation>>(){}.getType();
        ArrayList<Conversation> conversations = gson.fromJson(str, list);
        return conversations;
    }

    public Conversation getConversation(String id) {
        for(Conversation conversation : getConversations()) {
            if(conversation._id.equals(id)) return conversation;
        }
        return null;
    }

    public void addConversation(Conversation c) {
        ArrayList<Conversation> cv = getConversations();
        cv.add(c);
        storeConversation(cv);
    }

    // cập nhật cuộc trò chuyện: thêm xóa thành viên hoặc thay đổi tên, background...
    // a = 1 là thêm tv, -1 là xóa tv, khác là kp thêm hoặc xóa tv
    public void updateConversation(String id, String name, String background, String emoji, int a, ArrayList<Friend> friends) {
        ArrayList<Conversation> cv = getConversations();
        for (Conversation cvst : cv)
            if (cvst._id.equals(id)) {
                if (name != null) cvst.name = name;
                if (background != null) cvst.backgroundUrl = background;
                if (emoji != null) cvst.emoji = emoji;

                if (a == 1) for (Friend friend : friends)
                    cvst.members.add(friend);
                else if(a == -1) for (Friend friend : friends)
                    cvst.members.remove(friend);

                break;
            }

        storeConversation(cv);
    }

    public void deleteConversation(Conversation c) {
        ArrayList<Conversation> cv = getConversations();
        cv.remove(c);
        storeConversation(cv);
    }


    public void clear() {
        editor.clear().commit();
    }

    public void saveTimeToken(Calendar time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Log.d("DEBUG", String.valueOf(time.getTimeInMillis()));
        editor.putLong(TIME_TOKEN, time.getTimeInMillis());
        editor.commit();
    }

    public Boolean tokenVaild() {
        long time = pref.getLong(TIME_TOKEN, 0);
        Log.d("DEBUG", String.valueOf(time));
        if (time == 0) return false;
        long now = Calendar.getInstance().getTimeInMillis();
        return  time > now;
    }
}
