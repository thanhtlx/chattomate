package com.example.chattomate.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.chattomate.R;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.Conversation;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.Message;
import com.example.chattomate.models.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Conversation> conversations = new ArrayList<>();
    AppPreferenceManager manager;
    ListConversationAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

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
        conversations = manager.getConversations();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new ListConversationAdapter(conversations, getContext());
        recyclerView.setAdapter(adapter);

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

    class ListConversationAdapter extends RecyclerView.Adapter {
        private ArrayList<Conversation> list;
        private Context mContext;
        String nameConversation;
        String idFriend;

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
                    if(!c.members.get(0)._id.equals(user._id)) idFriend = c.members.get(0)._id;
                    else idFriend = c.members.get(1)._id;

                    Friend friend = manager.getFriend(manager.getFriends(), idFriend);
                    if(friend != null) {
                        if (friend.avatarUrl.length() > 0)
                            h.avatar.setImageURI(Uri.parse(friend.avatarUrl));
                        nameConversation = friend.name;
                    } else nameConversation = c.name;
                } else { //tro chuyen nhom
                    idFriend = "";
                    nameConversation = c.name;
                }

                String content_message;

                Message message = manager.getMessage(c._id).get(manager.getMessage(c._id).size()-1);
                if(message.sendBy._id.equals(manager.getUser()._id)) {
                    if (message.content.length() > 0) h.message.setText("Bạn: " + message.content);
                    else h.message.setText("Bạn đã gửi file đính kèm");
                } else {
                    String name_friend = message.sendBy.name;
                    if (message.content.length() > 0) h.message.setText(name_friend + ": " + message.content);
                    else h.message.setText(name_friend + " đã gửi file đính kèm");

                }
                h.time.setText(message.sendAt);

                h.name.setText(nameConversation);
            }

            ((View) h.name.getParent().getParent().getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle extras = new Bundle();
                    extras.putString("idConversation", c._id);
                    extras.putString("idFriend", idFriend);
                    extras.putString("nameConversation", nameConversation);

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
            avatar = view.findViewById(R.id.icon_avatar);
            name = view.findViewById(R.id.txtName);
            message = view.findViewById(R.id.txtMessage);
            time = view.findViewById(R.id.txtTime);
        }
    }


}
