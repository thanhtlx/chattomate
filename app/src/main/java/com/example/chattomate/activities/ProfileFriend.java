package com.example.chattomate.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.chattomate.R;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.Friend;
import com.example.chattomate.service.ServiceAPI;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFriend extends AppCompatActivity {
    AppPreferenceManager manager;
    CircleImageView imageView;
    TextView textView;
    Button ib, del;
    ServiceAPI serviceAPI;
    HashMap<String, String> token;
    private String idConversation;
    private String idFriend;
    private String nameConversation;
    Friend friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_friend);

        Toolbar toolbar = findViewById(R.id.toolbar_profile_friend);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        manager = new AppPreferenceManager(getApplicationContext());
        serviceAPI = new ServiceAPI(this, manager);
        token = manager.getMapToken(this);

        Bundle extras = getIntent().getExtras();
        idConversation = extras.getString("idConversation");
        nameConversation = extras.getString("nameConversation");
        idFriend = extras.getString("idFriend");
        friend = manager.getFriend(manager.getFriends(), idFriend);

        imageView = findViewById(R.id.avatarChangepwd);
        textView = findViewById(R.id.profile_friend_name);
        ib = findViewById(R.id.message_friend);
        del = findViewById(R.id.del_friend);

        textView.setText(nameConversation);
        if(friend.avatarUrl.length() > 0) imageView.setImageURI(Uri.parse(friend.avatarUrl));

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = new Bundle();
                extras.putString("idConversation", idConversation);
                extras.putString("nameConversation", nameConversation);
                extras.putInt("member_number", 2);

                Intent intent = new Intent(ProfileFriend.this, ChatActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
}