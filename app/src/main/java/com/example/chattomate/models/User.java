package com.example.chattomate.models;

public class User {
    public String name;
    public String email;
    public String avatar;
    public Status status;
    public Message message;

    public User(){
        status = new Status();
        message = new Message();
        status.isOnline = false;
        status.timeStamp = 0;
        message.idReceive = "0";
        message.idSent = "0";
        message.text = "";
        message.timeStamp = 0;
    }

}
