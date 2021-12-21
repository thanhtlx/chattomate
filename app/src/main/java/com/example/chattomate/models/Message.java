package com.example.chattomate.models;

import java.util.ArrayList;

public class Message {
    public String conversation;
    public String _id;
    public String content;
    public String contentUrl;
    public String sendAt;
    public ArrayList<Friend> seenBy;
    public Friend sendBy;
    public boolean delete;
    public String type;

    public Message(String content) {
        this.content = content;
    }

    public Message(String conversation, String _id, String content, String contentUrl, String sendAt, ArrayList<Friend> seenBy, Friend sendBy, boolean delete, String type) {
        this.conversation = conversation;
        this._id = _id;
        this.content = content;
        this.contentUrl = contentUrl;
        this.sendAt = sendAt;
        this.seenBy = seenBy;
        this.sendBy = sendBy;
        this.delete = delete;
        this.type = type;
    }

    public Message() {}
}
