package com.example.chattomate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.example.chattomate.App;
import com.example.chattomate.R;
import com.example.chattomate.adapter.ChatRoomThreadAdapter;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.interfaces.ScrollChat;
import com.example.chattomate.interfaces.SocketCallBack;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.Message;
import com.example.chattomate.models.User;
import com.example.chattomate.service.API;
import com.example.chattomate.service.Call;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements ScrollChat {
    ImageButton send;
    EditText txtContent;
    RecyclerView recyclerView;
    Call callService;

    private ArrayList<Message> listMess;
    private ArrayList<Friend> members; //cac thanh vien trong cuoc tro chuyen (ko có mình)
    private User user;
    private AppPreferenceManager manager;
    private ChatRoomThreadAdapter adapter;
    private String idConversation;
    private String nameConversation;
    String URL_MESSAGE      = Config.HOST + Config.MESSAGE_URL;
    Map<String, String> token = new HashMap<>();
    int member_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callService = new Call(this);
        manager = new AppPreferenceManager(getApplicationContext());
        user = manager.getUser();

        token.put("auth-token", manager.getToken(this));

        send = findViewById(R.id.btn_send);
        txtContent = findViewById(R.id.txt_message);
        recyclerView = findViewById(R.id.recycler_view_chat);

        Bundle extras = getIntent().getExtras();
        idConversation = extras.getString("idConversation");
        nameConversation = extras.getString("nameConversation");
        getSupportActionBar().setTitle(nameConversation);
        member_number = extras.getInt("member_number");

        members = manager.getMembersInConversation(idConversation);

        listMess = manager.getMessage(idConversation);
        if(listMess != null && listMess.size() > 50)
            listMess = new ArrayList<>(listMess.subList(listMess.size()-50,listMess.size()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
//        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(600);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatRoomThreadAdapter(this, listMess,(member_number > 2) , user._id);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        if(members.size() == 1) {
            Friend x = members.get(0);
            if(x.nickName.length() > 0) getSupportActionBar().setTitle(x.nickName);
        }
        getSupportActionBar().setTitle(nameConversation);

    }

    //default send text. Others: 1.text, 2.video_call, 3.voice_call, 4.image, 5.voice, 6.file, 7.emoji
    public void sendMessage() {
        String content = txtContent.getText().toString().trim();
        if (!content.equals("")) { //send text: type = 1
            JSONObject sendM = new JSONObject();
            try {
                sendM.put("conversation", idConversation);
                sendM.put("type", "1");
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
                            String content = j.getString("content");
                            String contentUrl = j.getString("contentUrl");
                            String sendAt = j.getString("createdAt");
                            Friend sender = new Friend(manager.getUser()._id, manager.getUser().name, manager.getUser().avatarUrl);

                            Message sen = new Message(idConversation, id, content, contentUrl, sendAt, null, sender, false, "1");
                            manager.addMessage(sen, idConversation);
                            Log.d("debugGGGG",manager.getMessage(idConversation).get(
                                    manager.getMessage(idConversation).size()-1).content);

                            addMessToList(sen);
                            adapter.notifyDataSetChanged();
                            if (adapter.getItemCount() > 1) {
                                // scrolling to bottom of the recycler view
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
                            }

                            txtContent.setText("");
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                        } else { System.out.println("Error"); }
                    } catch (JSONException e) { e.printStackTrace(); }
                    Log.d("debug",result.toString());
                }

                @Override
                public void onError(JSONObject result) {
//                    Log.d("debug",result.toString());
                }
            });

        } else {

        }

    }

    private void addMessToList(Message message) {
        if (listMess.size() > 100) {
            listMess.remove(0);
            Log.d("DEBUG", String.valueOf(listMess.size()));
        }
        listMess.add(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.getInstance().getSocket().setSocketCallBack(new SocketCallBack() {
            @Override
            public void onNewMessage(JSONObject data) {
                Log.d("debugChatNewMess", data.toString());
                try {
                    String id_Conversation = data.getString("conversation");
                    if(id_Conversation.equals(idConversation)) {
                        JSONObject object = data.getJSONObject("sendBy");
                        String idSender = object.getString("_id");
                        Friend friend = manager.getFriend(manager.getAllUsers(), idSender);
                        if (friend == null) {
                            friend = new Friend(object.getString("_id"),
                                    object.getString("name"), object.getString("avatarUrl"));
                            friend.idApi = object.getString("idApi");
                        }

                        Message message = new Message(idConversation, data.getString("_id"),
                                data.getString("content"), data.getString("contentUrl"),
                                data.getString("createdAt"), null, friend, false,
                                data.getString("type"));
                        manager.addMessage(message, idConversation);

                        addMessToList(message);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("debugGXX", data.toString());
                }
            }

            @Override
            public void onNewFriendRequest(JSONObject data) {

            }

            @Override
            public void onNewConversation(JSONObject data) {

            }

            @Override
            public void onNewFriend(JSONObject data) {

            }

            @Override
            public void onConversationChange(JSONObject data) {

            }

            @Override
            public void onFriendActiveChange(JSONObject data) {

            }

            @Override
            public void onTyping(JSONObject data) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.getInstance().getSocket().unSetSocketCallBack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.voicecall_icon:
                callService.startCall(false, "1","1");
                break;
            case R.id.videocall_icon:
                callService.startCall(true, "1", "1");
                break;
            case R.id.options:
                Intent intent;
                Bundle ext = new Bundle();
                ext.putInt("member_number", members.size());
                if(members.size() == 1) {
                    ext.putString("id", members.get(0)._id);
                    ext.putString("name", members.get(0).name);
                    intent = new Intent(ChatActivity.this, MenuFriendChatRoom.class);
                } else {
                    ext.putString("name", nameConversation);
                    ext.putString("id", idConversation);
                    intent = new Intent(ChatActivity.this, MenuGroupChatRoom.class);
                }

                intent.putExtras(ext);
                startActivity(intent);
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void ScrollRecycleView() {
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
    }
}