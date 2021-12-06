package com.example.chattomate.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.example.chattomate.config.Config;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.Conversation;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.Message;
import com.example.chattomate.models.User;
import com.example.chattomate.service.API;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class AppPreferenceManager {
    private static String TAG = AppPreferenceManager.class.getSimpleName();
    private SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME       = "chattomate_chat";
    private static final String IS_LOGGED_IN    = "isLoggedIn";
    private static final String STATE_ACTIVE    = "state_active";
    private static final String ID              = "_id";
    private static final String IdApi              = "_idApi";
    private static final String PHONE           = "phone";
    private static final String AVATAR_URL      = "avatarUrl";
    private static final String NAME            = "name";
    private static final String EMAIL           = "email";
    private static final String PASSWORD        = "password";
    private static final String ALL_FRIEND      = "friends";
    private static final String REQUEST_FRIEND  = "request_friends";
    private static final String PENDING_FRIEND  = "pending_friends";
    private static final String ALL_CONVERSATION= "conversations";
    private static final String ALL_MESSAGE     = "messages";
    private static final String ALL_USER        = "all_user";
    private static final String TOKEN           = "token";
    private static final String FB_TOKEN        = "fb_token";
    private static final String TIME_TOKEN      = "time token";
    private static final String IS_SILENCE      = "isSilence";

    public AppPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn).commit();
        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGGED_IN, false);
    }

    public void setStateActive(boolean b) {
        editor.putBoolean(STATE_ACTIVE, b).commit();
        Log.d(TAG, "User state active session modified!");
    }

    public boolean getStateActive(){
        return pref.getBoolean(STATE_ACTIVE, false);
    }

    public void setSilence(boolean b) {
        editor.putBoolean(IS_SILENCE, b).commit();
        Log.d(TAG, "User state silence session modified!");
    }

    public boolean getSilence(){
        return pref.getBoolean(IS_SILENCE, false);
    }

    public void storeUser(User user) {
        editor.putString(ID, user._id);
        editor.putString(IdApi, user.idApi);
        editor.putString(PHONE, user.phone);
        editor.putString(AVATAR_URL, user.avatarUrl);
        editor.putString(NAME, user.name);
        editor.putString(EMAIL, user.email);
        editor.putString(PASSWORD, user.password);
        editor.commit();
    }

    public Friend getUserAsFriend() {
        Friend friend = new Friend(getUser()._id);
        friend.name = getUser().name;
        friend.avatarUrl = getUser().avatarUrl;
        return friend;
    }

    public User getUser() {
        if (pref.getString(ID, null) != null) {
            String id, phone, avatar, name, password, email, idApi;
            id = pref.getString(ID, null);
            phone = pref.getString(PHONE, null);
            avatar = pref.getString(AVATAR_URL, null);
            name = pref.getString(NAME, null);
            email = pref.getString(EMAIL, null);
            password = pref.getString(PASSWORD, null);
            idApi = pref.getString(IdApi,null);
            return new User(id, name, avatar, phone, email, password,idApi);
        } else return null;
    }

    public String getNameFromIdApi(Integer id) {
        List<Friend> friends = getAllUsers();
        for (Friend friend:friends) {
            if (Integer.parseInt(friend.idApi) == id) {
                return  friend.name;
            }
        }
        return  "";
    }

    public void storeMessage(ArrayList<Message> m, String idConversation) {
        Gson gson = new Gson();
        String str = gson.toJson(m);
        editor.putString(ALL_MESSAGE+idConversation, str).commit();
    }

    public ArrayList<Message> getMessage(String idConversation) {
        String str = pref.getString(ALL_MESSAGE+idConversation, "");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Message>>() {}.getType();
        ArrayList<Message> messages = gson.fromJson(str, type);

        return messages;
    }

    public void addMessage(Message message, String idConversation) {
        ArrayList<Message> m = getMessage(idConversation);
        boolean check = false;
        for(Message message1 : m)
            if(message1._id.equals(message._id)) {
                check = true;
                break;
            }

        if(!check) {
            m.add(message);
            storeMessage(m, idConversation);
        }

    }

    public void deleteAMessage(String id, String idConversation) {
        ArrayList<Message> m = getMessage(idConversation);
        for(Message message : m)
            if(message._id.equals(id)) {
                m.remove(message);
                break;
            }
        storeMessage(m, idConversation);
    }

    public void deleteConversation(String idConversation) {
        editor.remove(ALL_MESSAGE+idConversation).commit();

        ArrayList<Conversation> conversations = getConversations();
        Conversation c = getConversation(idConversation);
        conversations.remove(c);

        storeConversation(conversations);
    }

    public void storeAllUsers(ArrayList<Friend> friends) {
        Gson gson = new Gson();
        String str = gson.toJson(friends);
        editor.putString(ALL_USER, str).commit();
    }

    public ArrayList<Friend> getAllUsers() {
        String str = pref.getString(ALL_USER, "");
        Gson gson = new Gson();
        Type friendList = new TypeToken<ArrayList<Friend>>(){}.getType();
        ArrayList<Friend> friends = gson.fromJson(str, friendList);
        return friends;
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
        if(friends != null)
            for (Friend friend : friends) {
                if (friend._id.equals(id)) return friend;
            }
        return null;
    }

    public void addFriend(Friend friend) {
        ArrayList<Friend> friends = getFriends();
        Friend f = getFriend(friends, friend._id);

        if(f == null) {
            friends.add(friend);
            storeFriends(friends);
        }
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

    // lấy id cuộc trò chuyện của người bạn có _id = idFriend
    public String getIdConversation(String idFriend) {
        ArrayList<Conversation> conversations = getConversations();
        if (conversations != null)
            for (Conversation c : conversations) {
                if (c.members.size() == 2) {
                    Friend friend = getFriend(c.members, idFriend);
                    if (friend != null) return c._id;
                }
            }

        return null;
    }

    public ArrayList<Friend> getMembersInConversation(String idConversation) {
        ArrayList<Friend> members = new ArrayList<>();
        members = getConversation(idConversation).members;
        members.remove(getFriend(members, getUser()._id)); //remove self
        for (Friend f : members) {
            Friend friend = getFriend(getAllUsers(), f._id);
            f.idApi = friend.idApi;
            f.avatarUrl = friend.avatarUrl;
            f.email = friend.email;
            f.name = friend.name;
        }

        return members;
    }

    public void addConversation(Conversation c) {
        ArrayList<Conversation> cv = getConversations();
        Conversation conversation = getConversation(c._id);

        if(conversation == null) {
            cv.add(c);
            storeConversation(cv);
        }
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

    public void saveToken(String token) {
        editor.putString(TOKEN, token);
        editor.commit();

    }

    public HashMap<String, String> getMapToken(Context c) {
        HashMap<String, String> h = new HashMap<>();
        h.put("auth-token", getToken(c));
        return h;
    }

    public String getToken(Context c) {
        if(!tokenValid()) {
            String LOGIN_URL = Config.HOST + Config.LOGIN_URL;
            JSONObject loginData = new JSONObject();
            try {
                loginData.put("email", getUser().email);
                loginData.put("password", getUser().password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            API api = new API(c);
            api.Call(Request.Method.POST, LOGIN_URL, loginData, null, new APICallBack() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        String status = result.getString("status");
                        if(status.equals("success")) {
                            String AUTH_TOKEN = result.getJSONObject("data").getString("token");
                            saveToken(AUTH_TOKEN);

                            Calendar now = Calendar.getInstance();
                            now.add(Calendar.DATE,1);
                            saveTimeToken(now);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("debug",result.toString());
                }

                @Override
                public void onError(JSONObject result) {
                    Log.d("debug",result.toString());
                }
            });
        }

        String token = pref.getString(TOKEN, "");
        return token;
    }

    public void saveTimeToken(Calendar time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Log.d("DEBUG", String.valueOf(time.getTimeInMillis()));
        editor.putLong(TIME_TOKEN, time.getTimeInMillis());
        editor.commit();
    }

    public Boolean tokenValid() {
        long time = pref.getLong(TIME_TOKEN, 0);
        Log.d("DEBUG", String.valueOf(time));
        if (time == 0) return false;
        long now = Calendar.getInstance().getTimeInMillis();
        return  time > now;
    }

    public String getFBToken(){
        String token = pref.getString(FB_TOKEN, "");
        return  token;
    }

    public void setFBToken(String token)  {
        editor.putString(FB_TOKEN, token);
        editor.commit();
    }

    public void clear() {
        editor.clear().commit();
    }


    public List<Friend> getUserInConversation(String idConversation) {
        List<Friend> res = new ArrayList<>();

        return res;
    }
}
