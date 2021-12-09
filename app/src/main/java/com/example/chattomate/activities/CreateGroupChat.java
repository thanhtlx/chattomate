package com.example.chattomate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.Conversation;
import com.example.chattomate.models.Friend;
import com.example.chattomate.service.API;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupChat extends AppCompatActivity {
    CircleImageView avatar_group;
    EditText name_group;
    ArrayList<Friend> members = new ArrayList<>();
    AppPreferenceManager manager;
    RecyclerView recyclerView;
    FloatingActionButton button;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_chat);
        Toolbar toolbar = findViewById(R.id.toolbar_group);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tạo nhóm trò chuyện");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycler_group);
        avatar_group = findViewById(R.id.avatar_group);
        name_group = findViewById(R.id.name_group);
        button = findViewById(R.id.next_act);

        manager = new AppPreferenceManager(getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new ListAdapter(manager.getFriends(), this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(members == null || members.size() == 0) {
                    Toast.makeText(CreateGroupChat.this, "Hãy chọn thành viên!", Toast.LENGTH_SHORT).show();
                } else if(members.size() == 1) { // tro ve cuoc tro chuyen 1v1
                    String idConversation = manager.getIdConversation(members.get(0)._id);
                    Bundle extras = new Bundle();
                    extras.putString("idConversation", idConversation);
                    extras.putString("nameConversation", members.get(0).name);
                    extras.putInt("member_number", 2);

                    Intent intent = new Intent(CreateGroupChat.this, ChatActivity.class);
                    intent.putExtras(extras);
                    startActivity(intent);

                } else { //tro chuyen nhom
                    if(manager.checkHasConversation(members) != null) { //cuoc tro chuyen da ton tai
                        String idConversation = manager.checkHasConversation(members);
                        Bundle extras = new Bundle();
                        extras.putString("idConversation", idConversation);
                        extras.putString("nameConversation", name_group.getText().toString());
                        extras.putInt("member_number", members.size());

                        Intent intent = new Intent(CreateGroupChat.this, ChatActivity.class);
                        intent.putExtras(extras);
                        startActivity(intent);

                    } else { //tao cuoc tro chuyen moi
//                        if(name_group.getText().toString().isEmpty())
//                            Toast.makeText(CreateGroupChat.this, "Hãy đặt tên cho nhóm!", Toast.LENGTH_SHORT).show();

                        JSONArray jsonArray = new JSONArray();
                        for(Friend friend : members)
                            jsonArray.put(friend._id);

                        JSONObject member = new JSONObject();
                        try {
                            member.put("members", jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String URL_CONVERSATION = Config.HOST + Config.CONVERSATION_URL;
                        API api = new API(CreateGroupChat.this);
                        api.Call(Request.Method.POST, URL_CONVERSATION, member, manager.getMapToken(CreateGroupChat.this), new APICallBack() {
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

                                        Bundle extras = new Bundle();
                                        extras.putString("idConversation", newCvst._id);
                                        extras.putString("nameConversation", newCvst.name);
                                        extras.putInt("member_number", members.size());

                                        Intent intent = new Intent(CreateGroupChat.this, ChatActivity.class);
                                        intent.putExtras(extras);
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
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ListAdapter extends RecyclerView.Adapter {
        private ArrayList<Friend> friends;
        private Context mContext;

        public ListAdapter(ArrayList<Friend> _friend, Context mContext) {
            this.friends = _friend;
            this.mContext = mContext;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View list_friends = inflater.inflate(R.layout.item_friend_creategroup, parent, false);
            return new ViewHolder(list_friends);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
            Friend friend = manager.getFriend(manager.getAllUsers(), friends.get(position)._id);
            ViewHolder h = (ViewHolder) holder;
            h.id = friend._id;
            if(friend.avatarUrl.length() > 0) h._avatar_friend.setImageURI(Uri.parse(friend.avatarUrl));
            h._name_friend.setText(friend.name);
            h._email_friend.setText(friend.email);
        }

        @Override
        public int getItemCount() {
            if(friends == null) return 0;
            return friends.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CircleImageView _avatar_friend;
            public TextView _name_friend, _email_friend;
            public CheckBox checkBox;
            public String id;

            public ViewHolder(View view) {
                super(view);
                _avatar_friend = view.findViewById(R.id.item_avatar_friend_group);
                _name_friend = view.findViewById(R.id.item_name_friend_group);
                _email_friend = view.findViewById(R.id.item_email_friend_group);
                checkBox = view.findViewById(R.id.item_checkBox_group);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBox.setChecked(!checkBox.isChecked());
                        if (checkBox.isChecked()) members.add(manager.getFriend(friends, id));
                        else {
                            if (manager.getFriend(members, id) != null)
                                members.remove(manager.getFriend(members, id));
                        }

                        Log.d("debug___", members.toString());
                    }
                });

            }
        }


    }

}