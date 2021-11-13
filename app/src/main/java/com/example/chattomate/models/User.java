package com.example.chattomate.models;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    public String idApi;
    public String _id;
    public String name;
    public String avatarUrl = "";
    public String phone = "";
    public String email;
    public String password;

    public User() { }

    public User(String name, String avatarUrl, String phone, String email, String password) {
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public User(String name, String phone, String email) {
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.phone = phone;
        this.email = email;
    }

    public User(String name, String avatarUrl, String phone, String email) {
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.phone = phone;
        this.email = email;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
