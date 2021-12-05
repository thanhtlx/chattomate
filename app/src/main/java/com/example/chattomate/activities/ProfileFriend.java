package com.example.chattomate.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.Conversation;
import com.example.chattomate.models.Friend;
import com.example.chattomate.service.API;
import com.example.chattomate.service.ServiceAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private String idApiFriend;
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
        idApiFriend = extras.getString("idApiFriend");
        friend = manager.getFriend(manager.getFriends(), idFriend);

        imageView = findViewById(R.id.profile_friend_avatar);
        textView = findViewById(R.id.profile_friend_name);
        ib = findViewById(R.id.message_friend);
        del = findViewById(R.id.del_friend);

        textView.setText(nameConversation);
        if(friend.avatarUrl.length() > 0) imageView.setImageURI(Uri.parse(friend.avatarUrl));

        API api = new API(this);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int member_number = extras.getInt("member_number");

                if(idConversation == null) {
                    ArrayList<Friend> members = new ArrayList<>();
                    members.add(friend);
                    members.add(manager.getUserAsFriend());

                    String URL_CONVERSATION = Config.HOST + Config.CONVERSATION_URL;

                    JSONArray jsonArray = new JSONArray();
                    for(Friend f : members)
                        jsonArray.put(f._id);

                    JSONObject m = new JSONObject();
                    try {
                        m.put("members", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    api.Call(Request.Method.POST, URL_CONVERSATION, m, token, new APICallBack() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                String status = result.getString("status");
                                if(status.equals("success")) {
                                    JSONObject json = result.getJSONObject("data");
                                    Conversation newCvst = new Conversation(json.getString("_id"),
                                            json.getString("name"), json.getString("backgroundURI"),
                                            json.getString("emoji"), members);
                                    manager.addConversation(newCvst);

                                    Bundle extr = new Bundle();

                                    extr.putString("idConversation", json.getString("_id"));
                                    extr.putString("idFriend", idFriend);
                                    extr.putString("idApiFriend", idApiFriend);
                                    extr.putString("nameConversation", nameConversation);
                                    extr.putInt("member_number", member_number);

                                    Intent intent = new Intent(ProfileFriend.this, ChatActivity.class);
                                    intent.putExtras(extr);
                                    startActivity(intent);

                                } else {
                                    System.out.println("Error");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("debug",result.toString());
                        }

                        @Override
                        public void onError(JSONObject result) {
                            Log.d("debug",result.toString());
                        }
                    });

                }

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
        getMenuInflater().inflate(R.menu.toolbar_friend, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            case R.id.voicecall_icon:

            case R.id.videocall_icon:

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
}