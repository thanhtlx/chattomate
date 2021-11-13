package com.example.chattomate.models;

import java.util.ArrayList;

public class Friend {
    public String nickName;
    public User friend;
    public boolean accepted;

    public Friend() {}

    public Friend(String id) {
        this.friend._id = id;
    }

    public Friend(String id, String nickName) {
        this.friend._id = id;
        this.nickName = nickName;
    }

    public Friend(String id, String nickName, String photo) {
        this.friend._id = id;
        this.friend.avatarUrl = photo;
        this.nickName = nickName;
    }

    public Friend(String id, String nickName, String name, String photo) {
        this.friend._id = id;
        this.friend.avatarUrl = photo;
        this.friend.name = name;
        this.nickName = nickName;
    }
}
