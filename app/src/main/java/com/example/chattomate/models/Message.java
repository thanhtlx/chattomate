package com.example.chattomate.models;

import java.util.ArrayList;

public class Message {
    public String content;
    public String contentUrl;
    public int type;
    public ArrayList<User> seenBy;
    public User sendBy;
    public boolean delete;
}
