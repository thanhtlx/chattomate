package com.example.chattomate.models;

import java.util.ArrayList;

public class Friend extends User {
    public String nickName;
    public boolean accepted;

    public Friend() {}

    public Friend(String id) {
        this._id = id;
    }

    public Friend(String id, String nickName) {
        this._id = id;
        this.nickName = nickName;
    }

    public Friend(String id, String nickName, String photo) {
        this._id = id;
        this.avatarUrl = photo;
        this.nickName = nickName;
    }

    public Friend(String id, String nickName, String name, String photo) {
        this._id = id;
        this.avatarUrl = photo;
        this.name = name;
        this.nickName = nickName;
    }

    public Friend(String id, String nickName, String name, String photo, String idApi) {
        this._id = id;
        this.avatarUrl = photo;
        this.name = name;
        this.nickName = nickName;
        this.idApi = idApi;
    }
}
