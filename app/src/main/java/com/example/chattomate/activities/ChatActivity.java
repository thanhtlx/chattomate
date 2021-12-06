package com.example.chattomate.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.example.chattomate.App;
import com.example.chattomate.R;
import com.example.chattomate.adapter.ChatRoomThreadAdapter;
import com.example.chattomate.call.CallActivity;
import com.example.chattomate.call.utils.PushNotificationSender;
import com.example.chattomate.call.utils.WebRtcSessionManager;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.interfaces.ScrollChat;
import com.example.chattomate.interfaces.SocketCallBack;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.Message;
import com.example.chattomate.models.User;
import com.example.chattomate.service.API;
import com.example.chattomate.service.ServiceAPI;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements ScrollChat {
    ImageButton send;
    EditText txtContent;
    RecyclerView recyclerView;

    private ArrayList<Message> listMess;
    private User user;
    private AppPreferenceManager manager;
    private ChatRoomThreadAdapter adapter;
    private ServiceAPI serviceAPI;
    private String idConversation;
    private String idFriend;
    private String nameConversation;
    String URL_MESSAGE = Config.HOST + Config.MESSAGE_URL;
    Map<String, String> token = new HashMap<>();
    int member_number;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = getIntent().getExtras();
        idConversation = extras.getString("idConversation");
        nameConversation = extras.getString("nameConversation");
        idFriend = extras.getString("idFriend");
        member_number = extras.getInt("member_number");
    }

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
        member_number = extras.getInt("member_number");
        if (idConversation == null) {
            onNewIntent(getIntent());
        }
        Log.d("DEBUGX", String.valueOf(idConversation));

        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(nameConversation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        manager = new AppPreferenceManager(getApplicationContext());
        user = manager.getUser();
        listMess = manager.getMessage(idConversation);
        if (listMess != null)
            listMess = new ArrayList<>(listMess.subList(listMess.size() - 50, listMess.size() - 1));
        serviceAPI = new ServiceAPI(this, manager);
        token.put("auth-token", manager.getToken(this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(600);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatRoomThreadAdapter(this, listMess, (member_number > 2), user._id);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() > 1) {
//            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
        }


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

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
                        if (status.equals("success")) {
                            JSONObject j = result.getJSONObject("data");

                            String id = j.getString("_id");
                            String content = j.getString("content");
                            String contentUrl = j.getString("contentUrl");
                            String sendAt = j.getString("createdAt");
                            Friend sender = new Friend(manager.getUser()._id, manager.getUser().name, manager.getUser().avatarUrl);

                            Message sen = new Message(idConversation, id, content, contentUrl, sendAt, null, sender, false, "1");
                            manager.addMessage(sen, idConversation);
                            Log.d("debugGGGG", manager.getMessage(idConversation).get(
                                    manager.getMessage(idConversation).size() - 1).content);
//                            listMess.add(sen);
                            addMessToList(sen);
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
                    Log.d("debug", result.toString());
                }

                @Override
                public void onError(JSONObject result) {
                    Log.d("debug", result.toString());
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
                Log.d("debugG", data.toString());
                try {
                    JSONObject object = data.getJSONObject("sendBy");
                    String idSender = object.getString("_id");
                    Friend friend = manager.getFriend(manager.getFriends(), idSender);
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

//                    listMess.add(message);
                    addMessToList(message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
                        }
                    });

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

    public synchronized void fetchChat() {
        API api = new API(this);
        api.Call(Request.Method.GET, URL_MESSAGE + "/" + idConversation, null, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if (status.equals("success")) {
                        JSONArray array = result.getJSONArray("data");
                        ArrayList<Message> messages = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
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
                            if (j.getJSONArray("seenBy").length() > 0) {
                                for (int n = 0; n < j.getJSONArray("seenBy").length(); n++) {
                                    JSONObject b = j.getJSONArray("seenBy").getJSONObject(i);
                                    idSeenBy.add(new Friend(b.getString("_id"), b.getString("name"), b.getString("avatarUrl")));
                                }
                            }

                            messages.add(new Message(conversation, id, content, contentUrl, sendAt, idSeenBy, sender, deleted, type));
                        }

                        manager.storeMessage(messages, idConversation);
                        listMess.clear();
                        listMess.addAll(messages);

                    } else {
                        System.out.println("Error");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("debug", result.toString());
            }

            @Override
            public void onError(JSONObject result) {
                Log.d("debug", result.toString());
            }
        });

        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1) {

            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.voicecall_icon:
                List<Friend> friends = manager.getUserInConversation(idConversation);
                startCall(false, "1", "1");
                break;
            case R.id.videocall_icon:
                startCall(true, "1", "1");
                break;
            case R.id.options:

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void ScrollRecylerview() {
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
    }

    private void startCall(boolean isVideoCall, String idApi, String _id) {
//        if(CallService.)
        ArrayList<Integer> opponentsList = new ArrayList<>();
        opponentsList.add(Integer.valueOf(idApi));
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

        QBRTCClient qbrtcClient = QBRTCClient.getInstance(this);
        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
        WebRtcSessionManager.getInstance(this).setCurrentSession(newQbRtcSession);
        // Make Users FullName Strings and ID's list for iOS VOIP push
        String newSessionID = newQbRtcSession.getSessionID();
        ArrayList<String> opponentsIDsList = new ArrayList<>();
        ArrayList<String> opponentsNamesList = new ArrayList<>();
        List<QBUser> usersInCall = new ArrayList<>();
        Log.d("DEBUG-CALL", idApi);
        // the Caller in exactly first position is needed regarding to iOS 13 functionality
        opponentsIDsList.add(idApi);
        opponentsNamesList.add(manager.getUser().name);

        String opponentsIDsString = TextUtils.join(",", opponentsIDsList);
        String opponentNamesString = TextUtils.join(",", opponentsNamesList);

        PushNotificationSender.sendPushMessage(opponentsList, manager.getUser().name, newSessionID, opponentsIDsString, opponentNamesString, isVideoCall, _id);
        CallActivity.start(this, false);
    }
}