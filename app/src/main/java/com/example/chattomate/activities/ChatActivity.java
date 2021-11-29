package com.example.chattomate.activities;

import android.content.BroadcastReceiver;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.adapter.ChatRoomThreadAdapter;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.Message;
import com.example.chattomate.models.User;
import com.example.chattomate.service.API;
import com.example.chattomate.service.ServiceAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    ImageButton send;
    EditText txtContent;
    RecyclerView recyclerView;

    //permission
    private int REQUEST_PERMISSION_OPEN_CAMERA  = 198;
    private int REQUEST_PERMISSION_OPEN_GALLERY = 199;
    private int REQUEST_IMAGE_CAMERA            = 200;
    private int REQUEST_IMAGE_GALLERY           = 201;
    private int REQUEST_PERMISSION_MIC          = 202;

    //media
    private File file;
    private String fileName = "";
    private MediaRecorder recorder;
    private boolean isUp = false;

    private ArrayList<Message> listMess;
    private User user;
    private AppPreferenceManager manager;
    private ChatRoomThreadAdapter adapter;
    private ServiceAPI serviceAPI;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String idConversation;
    private String idFriend;
    private String nameConversation;
    String URL_MESSAGE      = Config.HOST + Config.MESSAGE_URL;
    Map<String, String> token = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        send = findViewById(R.id.btn_send);
        txtContent = findViewById(R.id.txt_message);
        recyclerView = findViewById(R.id.recycler_view_chat);

        Bundle extras = getIntent().getExtras();
        idConversation = extras.getString("idConversation");
        nameConversation = extras.getString("nameConversation");
        idFriend = extras.getString("idFriend");

        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(nameConversation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        manager = new AppPreferenceManager(getApplicationContext());
        user = manager.getUser();
        listMess = manager.getMessage(idConversation);
        serviceAPI = new ServiceAPI(this, manager);
        token.put("auth-token", manager.getToken(this));

        adapter = new ChatRoomThreadAdapter(this, listMess, user._id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals(Config.ID_NOTIFICATION_NEW_MESSAGE)) {
//                    // new push message is received
//                    handlePushNotification(intent);
//                }
//            }
//        };

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
                            listMess.add(sen);

                            adapter.notifyDataSetChanged();
                            if (adapter.getItemCount() > 1) {
                                // scrolling to bottom of the recycler view
                                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
                            }

                            txtContent.setText("");
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

        } else {

        }

    }

    public void fetchChat() {
        API api = new API(this);
        api.Call(Request.Method.GET, URL_MESSAGE+"/"+idConversation, null, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        JSONArray array = result.getJSONArray("data");
                        ArrayList<Message> messages = new ArrayList<>();
                        for(int i = 0; i < array.length(); i++) {
                            JSONObject j = array.getJSONObject(i);
                            String conversation = j.getString("conversation");
                            String id = j.getString("_id");
                            String content = j.getString("content");
                            String contentUrl = j.getString("contentUrl");
                            String sendAt = j.getJSONObject("sendBy").getString("createdAt");

                            JSONObject a = j.getJSONObject("sendBy");
                            Friend sender = new Friend(a.getString("_id"), a.getString("name"), a.getString("avatarUrl"));

                            String type = j.getString("type");
                            Boolean deleted = j.getJSONArray("deleteBy").length() > 0;

                            ArrayList<Friend> idSeenBy = new ArrayList<>();
                            if(j.getJSONArray("seenBy").length() > 0) {
                                for (int n = 0; n < j.getJSONArray("seenBy").length(); n++) {
                                    JSONObject b = j.getJSONArray("seenBy").getJSONObject(i);
                                    idSeenBy.add(new Friend(b.getString("_id"), b.getString("name"), b.getString("avatarUrl")));
                                }
                            }

                            messages.add(new Message(conversation, id, content, contentUrl, sendAt, idSeenBy, sender, deleted, type));
                        }

                        manager.storeMessage(messages, idConversation);
                        listMess.clear();;
                        listMess.addAll(messages);
                        adapter.notifyDataSetChanged();
                        if (adapter.getItemCount() > 1) {
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
                        }

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

    public void content() {
        fetchChat();
        refresh(0);
    }

    private void refresh (int milliseconds) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                content();
            }
        };
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
            case R.id.voicecall_icon:

            case R.id.videocall_icon:

            case R.id.options:

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        content();
        // registering the receiver for new notification
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                new IntentFilter(Config.PUSH_NOTIFICATION));
        //
    }

    @Override
    protected void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

//    /**
//     * Handling new push message, will add the message to
//     * recycler view and scroll it to bottom
//     * */
//    private void handlePushNotification(Intent intent) {
//        Message message = (Message) intent.getSerializableExtra("message");
//
//        if (message != null && idConversation != null) {
//            manager.addMessage(message, idConversation);
//            listMess.add(message);
//            adapter.notifyDataSetChanged();
//            if (adapter.getItemCount() > 1) {
//                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
//            }
//        }
//    }


}