package com.example.chattomate.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.chattomate.R;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.Friend;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {
    RecyclerView recyclerView;
    ListFriendAdapter adapter;
    ArrayList<Friend> friendsList;
    AppPreferenceManager manager;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        recyclerView = view.findViewById(R.id.recycleListFriend);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        manager = new AppPreferenceManager(getContext());
        friendsList = manager.getFriends();

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

        // Inflate the layout for this fragment
        return view;
    }

    public class ListFriendAdapter extends RecyclerView.Adapter {
        private ArrayList<Friend> friends = new ArrayList<>();
        private Context mContext;

        public ListFriendAdapter(ArrayList<Friend> _friend, Context mContext) {
            this.friends = _friend;
            this.mContext = mContext;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View list_friends = inflater.inflate(R.layout.item_list_friend, parent, false);
            return new ViewHolder(list_friends);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Friend friend = friends.get(position);

            if (friend.avatarUrl.length() > 0) {
                Uri imageUri = Uri.parse(friend.avatarUrl);
                ((ViewHolder) holder).avatar_friend.setImageURI(imageUri);
            }

            ((ListFriendAdapter.ViewHolder) holder).name_friend.setText(friend.name);
        }

        @Override
        public int getItemCount() {
            if(friends == null) return 0;
            return friends.size();
        }

        /**
         * Lớp nắm giữ cấu trúc view
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            public CircleImageView avatar_friend;
            public TextView name_friend;
            public ImageButton callVoice, callVideo;
            public ImageView state_friend;

            public ViewHolder(View view) {
                super(view);
                avatar_friend = view.findViewById(R.id.avatar_friend);
                state_friend = view.findViewById(R.id.state_active_friend);
                name_friend = view.findViewById(R.id.name_friend);
                callVoice = view.findViewById(R.id.call_voice);
                callVideo = view.findViewById(R.id.call_video);
//            state_friend.setVisibility(View.INVISIBLE);

                callVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                callVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        //
                        return true;
                    }
                });

            }
        }


    }
}