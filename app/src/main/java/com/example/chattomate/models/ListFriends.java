package com.example.chattomate.models;

import java.util.ArrayList;

public class ListFriends {
    private ArrayList<Friend> listFriends;

    public ArrayList<Friend> getListFriend() {
        return listFriends;
    }

    public ListFriends(){
        listFriends = new ArrayList<>();
    }

    public String getAvatarById(String id){
        for(Friend friend: listFriends){
            if(id.equals(friend.idFriend)){
                return friend.avatar;
            }
        }
        return "";
    }

    public void setListFriend(ArrayList<Friend> listFriend) {
        this.listFriends = listFriend;
    }

}
