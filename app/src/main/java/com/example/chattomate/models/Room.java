package com.example.chattomate.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {
    public ArrayList<String> members;
    public Map<String, String> groupInfo;

    public Room() {
        members = new ArrayList<>();
        groupInfo = new HashMap<>();
    }
}
