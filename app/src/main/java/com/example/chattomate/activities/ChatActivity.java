package com.example.chattomate.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chattomate.R;

import java.net.Socket;

public class ChatActivity extends AppCompatActivity {
    private Socket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

    }
}