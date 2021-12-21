package com.example.chattomate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.chattomate.R;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.Conversation;

public class MenuGroupChatRoom extends AppCompatActivity {
    Toolbar toolbar;
    AppPreferenceManager manager;
    Switch notification;
    TextView members, add_member;
    String idConversation;
    Conversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_group_chat_room);
        manager = new AppPreferenceManager(getApplicationContext());
        toolbar = findViewById(R.id.menu_group_chat_room);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        idConversation = bundle.getString("id");
        conversation = manager.getConversation(idConversation);
        getSupportActionBar().setTitle(bundle.getString("name"));

        members = findViewById(R.id.members_group);
        add_member = findViewById(R.id.add_members_to_group);
        notification = findViewById((R.id.turn_noti_group));

        members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        notification.setChecked(conversation.isSilence);
        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                conversation.isSilence = notification.isChecked();
                manager.updateConversation(idConversation, notification.isChecked() ? 1 : -1, null, null, null, 0, null);
            }
        });

        add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}