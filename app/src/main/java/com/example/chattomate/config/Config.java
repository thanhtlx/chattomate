package com.example.chattomate.config;

public class Config {
    public static String HOST           = "https://chattomate.cf";
    public static String LOGIN_URL      = "/api/auth/login";
    public static String REGISTER_URL   = "/api/auth/register";
    public static String UPDATE_PROFILE_URL = "/api/users";
    public static String FRIENDS_URL    = "/api/friends";
    public static String CONVERSATION_URL = "/api/conversations";
    public static String MESSAGE_URL    = "/api/messages";
    public static String REGISTER_FB_TOKEN_URL    = "/api/users/register-fcm";
    public static String UN_REGISTER_FB_TOKEN_URL    = "/api/users/un-register-fcm";


    public static String SOCKET_URL     = "https://chattomate.cf/socket.io/socket.io.js";

    public static String NEW_MASSAGE    = "new message";
    public static String NEW_FRIEND_REQUEST = "new friend request";
    public static String NEW_CONVERSATION = "new conversation";
    public static String NEW_FRIEND     = "new friend";
    public static String CONVERSATION_CHANGE = "conversation change";
    public static String FRIEND_CHANGE  = "friend active change";
    public static String TYPING         = "typing";
    public static String DELETE_FRIEND  = "delete friend";

    public static long TIME_TO_REFRESH = 10 * 1000;
    public static long TIME_TO_OFFLINE = 2 * 60 * 1000;

    //    notification
    public static String CHANNEL_NOTIFICATION_NEW_MESSAGE = "NEW MESSAGE";
    public static int ID_NOTIFICATION_NEW_MESSAGE = 111;


}
