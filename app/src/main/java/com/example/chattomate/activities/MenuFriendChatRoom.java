package com.example.chattomate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.chattomate.R;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.Friend;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuFriendChatRoom extends AppCompatActivity {
    Toolbar toolbar;
    CircleImageView avatar;
    TextView name_view;
    Switch notification;
    AppPreferenceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_friend_chat_room);
        manager = new AppPreferenceManager(getApplicationContext());

        toolbar = findViewById(R.id.menu_group_chat_room);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        avatar = findViewById(R.id.avatar_menu_chat);
        name_view = findViewById(R.id.name_menu_chat);
        notification = findViewById(R.id.turn_noti_group);

        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        String name = extras.getString("name");
        Friend friend = manager.getFriend(manager.getFriends(), id);
        if(friend == null) friend = manager.getFriend(manager.getAllUsers(), id);

        name_view.setText(name);
        if(friend.avatarUrl.length() > 0) avatar.setImageURI(Uri.parse(friend.avatarUrl));

        if(friend.nickName.length() > 0) getSupportActionBar().setTitle(friend.nickName);
        else getSupportActionBar().setTitle("");

        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

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