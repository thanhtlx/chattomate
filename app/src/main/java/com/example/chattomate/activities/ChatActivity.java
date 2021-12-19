package com.example.chattomate.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.example.chattomate.App;
import com.example.chattomate.R;
import com.example.chattomate.adapter.ChatRoomThreadAdapter;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.interfaces.OnNewMessageCallBack;
import com.example.chattomate.interfaces.ScrollChat;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.Message;
import com.example.chattomate.models.User;
import com.example.chattomate.service.API;
import com.example.chattomate.service.Call;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FileUtils;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements ScrollChat {
    ImageButton send, sendFile;
    EditText txtContent;
    RecyclerView recyclerView;
    Call callService;
    private MediaRecorder mediaRecorder;
    Uri file;
    private String audioPath;
    RecordView recordView;
    RecordButton recordButton;
    int STORAGE_REQUEST_CODE = 1000;
    int RECORDING_REQUEST_CODE = 3000;
    //permisstion
    private final int REQUEST_IMAGE_GALLERY = 201;
    private final int REQUEST_PERMISTION_OPEN_GLLERY = 199;

    private ArrayList<Message> listMess;
    private ArrayList<Friend> members; //cac thanh vien trong cuoc tro chuyen (ko có mình)
    private User user;
    private AppPreferenceManager manager;
    private ChatRoomThreadAdapter adapter;
    private String idConversation;
    private String nameConversation;
    String URL_MESSAGE = Config.HOST + Config.MESSAGE_URL;
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
        sendFile = findViewById(R.id.send_file);
        recordView = findViewById(R.id.recordView);
        recordButton = findViewById(R.id.recordButton);
        recordButton.setRecordView(recordView);
        recordButton.setListenForRecord(false);
        txtContent = findViewById(R.id.txt_message);
        recyclerView = findViewById(R.id.recycler_view_chat);

        Bundle extras = getIntent().getExtras();
        idConversation = extras.getString("idConversation");
        nameConversation = extras.getString("nameConversation");
        member_number = extras.getInt("member_number");
        if (idConversation == null) {
            onNewIntent(getIntent());
        }
        getSupportActionBar().setTitle(nameConversation);

        members = manager.getMembersInConversation(idConversation);

        listMess = manager.getMessage(idConversation);
        if (listMess != null && listMess.size() > 50)
            listMess = new ArrayList<>(listMess.subList(listMess.size() - 50, listMess.size()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(600);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ChatRoomThreadAdapter(this, listMess, (member_number > 2), user._id);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() > 1) {
            // scrolling to bottom of the recycler view
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
        }


        send.setOnClickListener(view -> sendMessage());

        sendFile.setOnClickListener(view -> {
            if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_IMAGE_GALLERY);
            } else
                ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISTION_OPEN_GLLERY);
        });

        if (members.size() == 1) {
            Friend x = members.get(0);
            if (x.nickName.length() > 0) getSupportActionBar().setTitle(x.nickName);
        }
        getSupportActionBar().setTitle(nameConversation);

        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "ChatToMate/Media/Recording");

                if (!file.exists()) file.mkdirs();
                audioPath = file.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".3gp";

                mediaRecorder.setOutputFile(audioPath);

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                send.setVisibility(View.GONE);
                recordView.setVisibility(View.VISIBLE);

                Log.d("RecordView", "onStart");
            }

            @Override
            public void onCancel() {
                Log.d("RecordView", "onCancel");

                mediaRecorder.reset();
                mediaRecorder.release();
                File file = new File(audioPath);
                if (file.exists())
                    file.delete();

                recordView.setVisibility(View.GONE);
                send.setVisibility(View.VISIBLE);
                Log.d("RecordView", "onCancel");
            }

            @Override
            public void onFinish(long recordTime) {
                try {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                recordView.setVisibility(View.GONE);
                send.setVisibility(View.VISIBLE);
//                send message  voice
                JSONObject sendM = new JSONObject();
                try {
                    sendM.put("conversation", idConversation);
                    sendM.put("type", "5");
                    sendM.put("content", "send to voice");
                    String b64o3gp = Cv3pgToString(audioPath);
                    if(b64o3gp.length() > 0) {
                        Log.d("DEBUG", String.valueOf(b64o3gp.length()));
                        sendM.put("file",Cv3pgToString(audioPath));
                    }else {
                        Log.d("DEBUG" ,"???");
                        return;
                    }
//                    push file
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("DEBUG" ,"call api");
                API api = new API(ChatActivity.this);
                api.Call(Request.Method.POST, URL_MESSAGE, sendM, token, new APICallBack() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            String status = result.getString("status");
                            if (status.equals("success")) {
                                JSONObject j = result.getJSONObject("data");

                                String id = j.getString("_id");
                                String content = j.getString("content");
                                String type = j.getString("type");
                                String contentUrl = j.getString("contentUrl");
                                String sendAt = j.getString("createdAt");
                                Friend sender = new Friend(manager.getUser()._id, manager.getUser().name, manager.getUser().avatarUrl);

                                Message sen = new Message(idConversation, id, content, contentUrl, sendAt, null, sender, false, type);
                                manager.addMessage(sen, idConversation);
                                Log.d("debugGGGG", manager.getMessage(idConversation).get(
                                        manager.getMessage(idConversation).size() - 1).content);

                                audioPath = null;
                                file = null;
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
                    }
                });
            }

            @Override
            public void onLessThanSecond() {
                mediaRecorder.reset();
                mediaRecorder.release();

                File file = new File(audioPath);
                if (file.exists()) file.delete();

                recordView.setVisibility(View.GONE);
                send.setVisibility(View.VISIBLE);

                Log.d("RecordView", "onLessThanSecond");
            }
        });

        //ListenForRecord must be false ,otherwise onClick will not be called
        recordButton.setOnRecordClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    recordButton.setListenForRecord(true);
                else
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_REQUEST_CODE);
            else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORDING_REQUEST_CODE);

        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                Log.d("RecordView", "Basket Animation Finished");
            }
        });

    }

    private String Cv3pgToString(String filePath){
        File file = new File(filePath);
        byte[] bytes = new byte[0];
        try {
            bytes = FileUtils.readFileToByteArray(file);
            return Base64.encodeToString(bytes, 0);

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("DEBUG",e.getMessage());
        }
        return  "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            JSONObject sendM = new JSONObject();
            try {
                sendM.put("conversation", idConversation);
                sendM.put("type", "4");
                sendM.put("content", "send to image");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            file = data.getData();

            Glide.with(this).asBitmap().load(file).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    try {
                        sendM.put("file",BitMapToString(resource));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("DEBUG","ERRR");
                    }

                    API api = new API(ChatActivity.this);
                    api.Call(Request.Method.POST, URL_MESSAGE, sendM, token, new APICallBack() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                String status = result.getString("status");
                                if (status.equals("success")) {
                                    JSONObject j = result.getJSONObject("data");

                                    String id = j.getString("_id");
                                    String content = j.getString("content");
                                    String type = j.getString("type");
                                    String contentUrl = j.getString("contentUrl");
                                    String sendAt = j.getString("createdAt");
                                    Friend sender = new Friend(manager.getUser()._id, manager.getUser().name, manager.getUser().avatarUrl);

                                    Message sen = new Message(idConversation, id, content, contentUrl, sendAt, null, sender, false, type);
                                    manager.addMessage(sen, idConversation);
                                    Log.d("debugGGGG", manager.getMessage(idConversation).get(
                                            manager.getMessage(idConversation).size() - 1).content);

                                    audioPath = null;
                                    file = null;
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
//                    Log.d("debug",result.toString());
                        }
                    });
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }
    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private Boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED;
    }

    //default send text. Others: 1.text, 2.video_call, 3.voice_call, 4.image, 5.voice, 6.file, 7.emoji
    public void sendMessage() {
        String content = txtContent.getText().toString().trim();

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
                        String type = j.getString("type");
                        String contentUrl = j.getString("contentUrl");
                        String sendAt = j.getString("createdAt");
                        Friend sender = new Friend(manager.getUser()._id, manager.getUser().name, manager.getUser().avatarUrl);

                        Message sen = new Message(idConversation, id, content, contentUrl, sendAt, null, sender, false, type);
                        manager.addMessage(sen, idConversation);
                        Log.d("debugGGGG", manager.getMessage(idConversation).get(
                                manager.getMessage(idConversation).size() - 1).content);

                        audioPath = null;
                        file = null;
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
                    Log.d("debug",result.toString());
            }
        });

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
        listMess = manager.getMessage(idConversation);
        adapter = new ChatRoomThreadAdapter(this, listMess, (member_number > 2), user._id);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() > 1) {
            // scrolling to bottom of the recycler view
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
        }
        App.getInstance().getSocket().setOnNewMessageCallBack(data -> {
            Log.d("debugChatNewMess", data.toString());
            try {
                String id_Conversation = data.getString("conversation");

                JSONObject object = data.getJSONObject("sendBy");
                String idSender = object.getString("_id");
                Friend friend = manager.getFriend(manager.getAllUsers(), idSender);
                if (friend == null) {
                    friend = new Friend(object.getString("_id"),
                            object.getString("name"), object.getString("avatarUrl"));
                    friend.idApi = object.getString("idApi");
                }

                Message message = new Message(id_Conversation, data.getString("_id"),
                        data.getString("content"), data.getString("contentUrl"),
                        data.getString("createdAt"), null, friend, false,
                        data.getString("type"));
                manager.addMessage(message, id_Conversation);

                if (id_Conversation.equals(idConversation)) {
                    addMessToList(message);
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("debugGXX", data.toString());
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
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.voicecall_icon:
                for (Friend f : members )
                callService.startCall(false, f.idApi, f._id);
                break;
            case R.id.videocall_icon:
                for (Friend f : members )
                    callService.startCall(true, f.idApi, f._id);
                break;
            case R.id.options:
                Intent intent;
                Bundle ext = new Bundle();
                ext.putInt("member_number", members.size());
                if (members.size() == 1) {
                    ext.putString("id", members.get(0)._id);
                    ext.putString("conversationID", idConversation);
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
    public void ScrollRecycleView() {
        recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
    }
}