package com.example.chattomate.models;

import java.util.ArrayList;

public class Conversation {
    public String _id;
    public String name;
    public String backgroundUrl;
    public String emoji;
    public boolean isPrivate;
    public ArrayList<Friend> members;
    public Friend admin;
    public Message ghim;

    public Conversation(String _id, String name, String backgroundUrl, String emoji, boolean isPrivate, ArrayList<Friend> members) {
        this._id = _id;
        this.name = name;
        this.backgroundUrl = backgroundUrl;
        this.emoji = emoji;
        this.isPrivate = isPrivate;
        this.members = members;
    }

}
