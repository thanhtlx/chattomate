package com.example.chattomate.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.chattomate.App;
import com.example.chattomate.R;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.SocketCallBack;
import com.example.chattomate.models.Conversation;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.Message;
import com.example.chattomate.models.User;
import com.example.chattomate.service.ServiceAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Conversation> conversations = new ArrayList<>();
    AppPreferenceManager manager;
    ListConversationAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ServiceAPI serviceAPI;

    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycleListChatFriend);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout1);
        manager = new AppPreferenceManager(getContext());
        serviceAPI = new ServiceAPI(getContext(), manager);
        conversations = manager.getConversations();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new ListConversationAdapter(conversations, getContext());
        recyclerView.setAdapter(adapter);

        recyclerView.invalidate();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink2, R.color.pink3);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ListConversationAdapter(conversations, getContext());
                        recyclerView.setAdapter(adapter);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        App.getInstance().getSocket().setSocketCallBack(new SocketCallBack() {
            @Override
            public void onNewMessage(JSONObject data) {
                Log.d("debugChatFragment", data.toString());
                try {
                    JSONObject object = data.getJSONObject("sendBy");
                    String idConversation = data.getString("conversation");
                    String content = data.getString("content");
                    String contentUrl = data.getString("contentUrl");
                    String idSender = object.getString("_id");
                    Friend friend  = manager.getFriend(manager.getFriends(), idSender);
                    if(friend == null) {
                        friend = new Friend(object.getString("_id"),
                                object.getString("name"), object.getString("avatarUrl"));
                        friend.idApi = object.getString("idApi");
                    }
                    Message message = new Message(idConversation, data.getString("_id"),
                            content, contentUrl,
                            data.getString("createdAt"), null, friend, false,
                            data.getString("type"));
                    manager.addMessage(message, idConversation);

                    //TODO
                } catch (JSONException e) {
                    e.printStackTrace();
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

        adapter = new ListConversationAdapter(manager.getConversations(), getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        App.getInstance().getSocket().unSetSocketCallBack();
    }

    class ListConversationAdapter extends RecyclerView.Adapter {
        private ArrayList<Conversation> list;
        private Context mContext;
        String nameConversation;
        String idFriend;
        String idApiFriend;

        public ListConversationAdapter(ArrayList<Conversation> conversations, Context mContext) {
            this.list = conversations;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_list_chat_friend, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Conversation c = list.get(position);
            ViewHolder h = (ViewHolder) holder;
            User user = manager.getUser();

            if(c != null) {
                if (c.members.size() == 2) { //tro chuyen 1v1
                    if(!c.members.get(0)._id.equals(user._id)) {
                        idFriend = c.members.get(0)._id;
                    }
                    else {
                        idFriend = c.members.get(1)._id;
                    }

                    Friend friend = manager.getFriend(manager.getFriends(), idFriend);
                    idApiFriend = manager.getFriend(manager.getAllUsers(), idFriend).idApi;
                    if(friend != null) {
                        if (friend.avatarUrl.length() > 0)
                            h.avatar.setImageURI(Uri.parse(friend.avatarUrl));
                        nameConversation = friend.name;
                    } else nameConversation = c.name;
                } else { //tro chuyen nhom
                    idFriend = "";
                    idApiFriend = "";
                    nameConversation = c.name;
                }

                if(manager.getMessage(c._id) != null) {
                    if(manager.getMessage(c._id).size() > 0) {
                        Message message = manager.getMessage(c._id).get(manager.getMessage(c._id).size() - 1);
                        if (message.sendBy._id.equals(manager.getUser()._id)) {
                            if (message.content.length() > 0)
                                h.message.setText("Bạn: " + message.content);
                            else h.message.setText("Bạn đã gửi file đính kèm");
                        } else {
                            Friend friend = manager.getFriend(manager.getFriends(), message.sendBy._id);
                            String name_friend = "Someone";
                            if(friend != null) name_friend = friend.name;
                            if (message.content.length() > 0)
                                h.message.setText(name_friend + ": " + message.content);
                            else h.message.setText(name_friend + " đã gửi file đính kèm");

                        }
                        h.time.setText(App.getTimeStamp(message.sendAt));
                    }
                }
                h.name.setText(nameConversation);
            }

            ((View) h.name.getParent().getParent().getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle extras = new Bundle();
                    extras.putString("idConversation", c._id);
                    extras.putString("nameConversation", nameConversation);
                    extras.putInt("member_number", c.members.size());

                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if(list == null) return 0;
            return list.size();
        }

        public void newMessage(ArrayList<Conversation> conversations) {
            list = new ArrayList<>(conversations);
            notifyDataSetChanged();
        }

    }

    /**
     * Lớp nắm giữ cấu trúc view
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatar;
        public TextView name, message, time;
        public ImageView state_active;

        public ViewHolder(View view) {
            super(view);
            avatar = view.findViewById(R.id.avatar_search);
            name = view.findViewById(R.id.name_users);
            message = view.findViewById(R.id.email_users);
            time = view.findViewById(R.id.txtTime);
        }
    }


}
