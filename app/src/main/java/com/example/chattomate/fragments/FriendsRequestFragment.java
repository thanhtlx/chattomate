package com.example.chattomate.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chattomate.R;
import com.example.chattomate.activities.ProfileFriend;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.Friend;
import com.example.chattomate.service.ServiceAPI;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsRequestFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Friend> friendsList = new ArrayList<>();
    AppPreferenceManager manager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ServiceAPI serviceAPI;
    ListFriendAdapter adapter;

    public FriendsRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_pending, container, false);

        manager = new AppPreferenceManager(getContext());
        friendsList = manager.getRequestFriends();
        if(friendsList == null) friendsList = new ArrayList<>();
        serviceAPI = new ServiceAPI(getContext(), manager);

        recyclerView = view.findViewById(R.id.recycler_pending);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_pending);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        adapter = new ListFriendAdapter(friendsList, getContext());
        recyclerView.setAdapter(adapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink3);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ListFriendAdapter(friendsList, getContext());
                        recyclerView.setAdapter(adapter);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
            }
        });

        return view;
    }

    public class ListFriendAdapter extends RecyclerView.Adapter {
        private List<Friend> friends = new ArrayList<>();
        private Context mContext;

        public ListFriendAdapter(List<Friend> _friend, Context mContext) {
            this.friends = _friend;
            this.mContext = mContext;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View list_friends = inflater.inflate(R.layout.item_friends_pending, parent, false);
            return new ViewHolder(list_friends);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Friend friend = manager.getFriend(manager.getAllUsers(), friends.get(position)._id);
            ViewHolder h = (ViewHolder) holder;

            if(friend.avatarUrl.length() > 0) h.avatar_pending.setImageURI(Uri.parse(friend.avatarUrl));
            h.name_pending.setText(friend.name);
            h.email_pending.setText(friend.email);
        }

        @Override
        public int getItemCount() {
            if (friends == null) return 0;
            return friends.size();
        }

        /**
         * Lớp nắm giữ cấu trúc view
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            CircleImageView avatar_pending;
            TextView name_pending, email_pending;
            ImageButton delete;
            String id;

            public ViewHolder(View view) {
                super(view);

                avatar_pending = view.findViewById(R.id.avatar_pending);
                name_pending = view.findViewById(R.id.name_pending);
                email_pending = view.findViewById(R.id.email_pending);
                delete = view.findViewById(R.id.del_pending);
            }
        }
    }
}