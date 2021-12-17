package com.example.chattomate.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.Friend;
import com.example.chattomate.service.API;
import com.example.chattomate.service.ServiceAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsPendingFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Friend> friendsList = new ArrayList<>();
    AppPreferenceManager manager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ServiceAPI serviceAPI;
    ListFriendAdapter adapter;

    public FriendsPendingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_request, container, false);

        manager = new AppPreferenceManager(getContext());
        friendsList = manager.getPendingFriends();
        if(friendsList == null) friendsList = new ArrayList<>();
        serviceAPI = new ServiceAPI(getContext(), manager);

        recyclerView = view.findViewById(R.id.recycler_request);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_request);

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

            View list_friends = inflater.inflate(R.layout.item_friends_request, parent, false);
            return new ViewHolder(list_friends);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Friend friend = manager.getFriend(manager.getAllUsers(), friends.get(position)._id);
            ViewHolder h = (ViewHolder) holder;
            if(friend == null) return;
            if(friend.avatarUrl.length() > 0) h.avatar_request.setImageURI(Uri.parse(friend.avatarUrl));
            h.name_request.setText(friend.name);
            h.email_request.setText(friend.email);

            h.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = Config.HOST + Config.FRIENDS_URL + "/" + friend._id + "/accept";
                    JSONObject newFriend = new JSONObject();
                    try {
                        newFriend.put("id", friend._id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    API api = new API(mContext);
                    api.Call(Request.Method.PUT, url, newFriend, manager.getMapToken(mContext), new APICallBack() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                String status = result.getString("status");
                                if(status.equals("success")) {
                                    Friend f = manager.getFriend(manager.getPendingFriends(), friend._id);
                                    manager.deletePendingFriends(f);
                                    manager.addFriend(f);

                                    friends.remove(f);
                                    adapter = new ListFriendAdapter(friends, mContext);
                                    recyclerView.setAdapter(adapter);
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
            });
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
            CircleImageView avatar_request;
            TextView name_request, email_request;
            ImageButton accept, reject;
            String id;

            public ViewHolder(View view) {
                super(view);

                avatar_request = view.findViewById(R.id.avatar_request);
                name_request = view.findViewById(R.id.name_request);
                email_request = view.findViewById(R.id.email_request);
                accept = view.findViewById(R.id.accept_request);
                reject = view.findViewById(R.id.reject_request);
            }
        }
    }
}