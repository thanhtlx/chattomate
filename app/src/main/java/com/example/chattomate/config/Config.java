package com.example.chattomate.config;

public class Config {
    public static String HOST = "https://chattomate.cf";
    public static String LOGIN_URL = "/api/auth/login";
    public static String REGISTER_URL = "/api/auth/register";
    public static String UPDATE_PROFILE_URL = "/api/user";
    public static String FRIENDS_URL = "/api/friends";
    public static String CONVERSATION_URL = " /api/conversations";


    public static String SOCKET_URL = "https://chattomate.cf/socket.io/socket.io.js";

    public static String NEW_MASSAGE = "new message";
    public static String NEW_FRIEND_REQUEST = "new friend request";
    public static String NEW_CONVERSATION = "new conversation";
    public static String NEW_FRIEND = "new friend";
    public static String CONVERSATION_CHANGE = "conversation change";
    public static String FRIEND_CHANGE = "friend active change";
    public static String TYPING = "typing";
    public static String DELETE_FRIEND = "delete friend";


    //    notification
    public static String CHANNEL_NOTIFICATION_NEW_MESSAGE = "NEW MESSAGE";
    public static int ID_NOTIFICATION_NEW_MESSAGE = 111;

}
