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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.chattomate.R;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.Conversation;
import com.example.chattomate.models.Friend;

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

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
//                LinearLayoutManager.VERTICAL, false);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setHasFixedSize(true);
//
////        adapter = new ListConversationAdapter(conversations, getContext());
//        recyclerView.setAdapter(adapter);
//
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink2, R.color.pink3);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        adapter = new ListConversationAdapter(conversations, getContext());
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

            // tro chuyen 1v1
            if(c.members.size() == 1) {
                Friend friend = c.members.get(0);
                if(!friend.friend.avatarUrl.isEmpty()) h.avatar.setImageURI(Uri.parse(friend.friend.avatarUrl));
                h.name.setText(friend.friend.name);

            } else {
                h.name.setText(c.name);
            }

            ((View) h.name.getParent().getParent().getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ChatActivity.class);
                    intent.putExtra("idConversation", c._id);
                }
            });
        }

        @Override
        public int getItemCount() {
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
