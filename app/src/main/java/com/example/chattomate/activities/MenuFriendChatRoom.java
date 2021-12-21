package com.example.chattomate.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.Message;
import com.example.chattomate.service.API;
import com.example.chattomate.service.MapService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuFriendChatRoom extends AppCompatActivity {
    Toolbar toolbar;
    CircleImageView avatar;
    TextView name_view;
    Switch notification;
    AppPreferenceManager manager;
    Button btnMap;
    private String conversationID;
    private String nameConversation;
    private Friend friend;
    private String URL_MESSAGE      = Config.HOST + Config.MESSAGE_URL;
    private Map<String, String> token = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_friend_chat_room);
        manager = new AppPreferenceManager(getApplicationContext());
        token.put("auth-token", manager.getToken(this));

        toolbar = findViewById(R.id.menu_group_chat_room);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        avatar = findViewById(R.id.avatar_menu_chat);
        name_view = findViewById(R.id.name_menu_chat);
        notification = findViewById(R.id.turn_noti_group);
        btnMap = findViewById(R.id.btnMap);

        Bundle extras = getIntent().getExtras();
        String id = extras.getString("id");
        conversationID = extras.getString("conversationID");
        nameConversation = extras.getString("name");
        friend = manager.getFriend(manager.getFriends(), id);
        if(friend == null) friend = manager.getFriend(manager.getAllUsers(), id);

        name_view.setText(nameConversation);
//        if(friend.avatarUrl.length() > 0) avatar.setImageURI(Uri.parse(friend.avatarUrl));

        if(friend.nickName.length() > 0) getSupportActionBar().setTitle(friend.nickName);
        else getSupportActionBar().setTitle("");

        notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });

        btnMap.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},778);
                }
            }else {
                shareLocation();
            }
        });
    }

    private void shareLocation() {
        sendMessage();
    }
    public void sendMessage() {
        String content = "0,0";
        JSONObject sendM = new JSONObject();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        try {
            sendM.put("conversation", conversationID);
            sendM.put("type", "7");
            sendM.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        API api = new API(this);
        api.Call(Request.Method.POST, URL_MESSAGE, sendM, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        JSONObject j = result.getJSONObject("data");

                        String id = j.getString("_id");
                        String type = j.getString("type");
                        String content = j.getString("content");
                        String contentUrl = j.getString("contentUrl");
                        String sendAt = j.getString("createdAt");
                        Friend sender = new Friend(manager.getUser()._id, manager.getUser().name, manager.getUser().avatarUrl);
                        Message sen = new Message(conversationID, id, content, contentUrl, sendAt, null, sender, false, type);
                        manager.addMessage(sen, conversationID);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        Intent intent = new Intent(MenuFriendChatRoom.this, MapService.class);
                        Bundle ext = new Bundle();
                        ext.putString("messageID",id);
                        ext.putString("time",sendAt);
                        intent.putExtras(ext);
                        startService(intent);
                        onBackPressed();

                    } else { System.out.println("Error"); }
                } catch (JSONException e) { e.printStackTrace(); }
                Log.d("debug",result.toString());
            }

            @Override
            public void onError(JSONObject result) {
                Log.d("debug",result.toString());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 778) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},778);
                }
            }else{
                shareLocation();
            }
        }
    }
}