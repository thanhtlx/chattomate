package com.example.chattomate.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chattomate.R;
import com.example.chattomate.adapter.ChatRoomThreadAdapter;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.Message;
import com.example.chattomate.models.User;
import com.example.chattomate.service.ServiceAPI;

import java.io.File;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    Button send;
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
    private ServiceAPI api;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String idConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        send = findViewById(R.id.btn_send);
        txtContent = findViewById(R.id.txt_message);
        recyclerView = findViewById(R.id.recycler_view_chat);

        Intent intent = getIntent();
        idConversation = intent.getStringExtra("idConversation");
        String title = intent.getStringExtra("nameConversation");

        Toolbar toolbar = findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        manager = new AppPreferenceManager(getApplicationContext());
        user = manager.getUser();
        listMess = new ArrayList<>();
        api = new ServiceAPI(getApplicationContext(), manager);

        adapter = new ChatRoomThreadAdapter(this, listMess, user._id);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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
//                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
//                    // new push message is received
//                    handlePushNotification(intent);
//                }
//            }
//        };

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

    /**
     * Handling new push message, will add the message to
     * recycler view and scroll it to bottom
     * */
    private void handlePushNotification(Intent intent) {
        Message message = (Message) intent.getSerializableExtra("message");

        if (message != null && idConversation != null) {
            manager.addMessage(message);
            listMess.add(message);
            adapter.notifyDataSetChanged();
            if (adapter.getItemCount() > 1) {
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
            }
        }
    }

    //default send text. Others: 1.text, 2.video_call, 3.voice_call, 4.image, 5.voice, 6.file, 7.emoji
    public void sendMessage() {
        String content = txtContent.getText().toString().trim();
        if (!content.equals("")) {
            api.sendMessage(idConversation, 1, content);
            listMess = manager.getMessage(idConversation);

            adapter.notifyDataSetChanged();
            if (adapter.getItemCount() > 1) {
                // scrolling to bottom of the recycler view
                recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
            }

            txtContent.setText("");
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else {

        }

    }

    public void fetchChat() {
        api.getAllMessage(idConversation);
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1) {
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
        }
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

}