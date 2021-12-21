package com.example.chattomate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.chattomate.R;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.Conversation;

import java.util.ArrayList;

public class WaitingChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    AppPreferenceManager manager;
    TextView number_waiting_chats;
    ArrayList<Conversation> conversations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_chat);

        toolbar = findViewById(R.id.toolbar_waiting_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tin nhắn đang chờ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        manager = new AppPreferenceManager(this);
        number_waiting_chats = findViewById(R.id.number_conversations);
        if(conversations != null) if(conversations.size() > 0)
            number_waiting_chats.setText("Bạn đang có "+conversations.size()+" cuộc trò chuyện chờ.");

        recyclerView = findViewById(R.id.recycler_waiting_chat);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}