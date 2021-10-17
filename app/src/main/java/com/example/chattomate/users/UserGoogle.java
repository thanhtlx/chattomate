package com.example.chattomate.users;

import java.io.Serializable;

public class UserGoogle implements Serializable {
    private String userEmail;
    private String name;

    public UserGoogle(){}

    public UserGoogle(String userEmail, String name) {
        this.userEmail = userEmail;
        this.name = name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
