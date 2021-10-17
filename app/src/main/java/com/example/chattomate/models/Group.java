package com.example.chattomate.models;

public class Group extends Room {
    public String idGroup;
    public ListFriends listFriends;

    public Group() {
        listFriends = new ListFriends();
    }
}
