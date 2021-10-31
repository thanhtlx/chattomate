package com.example.chattomate.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.chattomate.models.User;

public class AppPreferenceManager {
    private static String TAG = AppPreferenceManager.class.getSimpleName();
    private SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private Context _context;

    int PRIVATE_MODE = 0;

    public static final String PREF_NAME = "chattomate_chat";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String PHONE = "phone";
    private static final String AVATAR_URL = "avatarUrl";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String KEY_NOTIFICATIONS = "notifications";

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

            User user = new User(phone, avatar, name, email, password);
            return user;

        } else return null;
    }

    public void addNotification(String notification) {
        // get old notifications
        String oldNotifications = getNotifications();
        if (oldNotifications != null) {
            oldNotifications += "|" + notification;
        } else {
            oldNotifications = notification;
        }
        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void clear() {
        editor.clear().commit();
    }

}
