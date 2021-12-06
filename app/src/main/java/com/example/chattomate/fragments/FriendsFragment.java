package com.example.chattomate.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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

import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.activities.ProfileFriend;
import com.example.chattomate.call.CallActivity;
import com.example.chattomate.call.CallService;
import com.example.chattomate.call.utils.PushNotificationSender;
import com.example.chattomate.call.utils.WebRtcSessionManager;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.Friend;
import com.example.chattomate.service.API;
import com.example.chattomate.service.ServiceAPI;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {
    RecyclerView recyclerView;
    ListFriendAdapter adapter;
    ArrayList<Friend> friendsList = new ArrayList<>();
    AppPreferenceManager manager;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ServiceAPI api;
    Map<String, String> token = new HashMap<>();

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
        api = new ServiceAPI(getContext(), manager);
        token.put("auth-token", manager.getToken(getContext()));

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

    @Override
    public void onResume() {
        super.onResume();
        API api = new API(getContext());
        String URL_FRIEND       = Config.HOST + Config.FRIENDS_URL;

        api.Call(Request.Method.GET, URL_FRIEND, null, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        JSONArray friends = result.getJSONArray("data");
                        ArrayList<Friend> list = new ArrayList<>();

                        for(int i = 0; i < friends.length(); i++) {
                            JSONObject temp = friends.getJSONObject(i);
                            JSONObject tmp = temp.getJSONObject("friend");
                            Friend friend = new Friend(tmp.getString("_id"), temp.getString("nickName"),
                                    tmp.getString("name"), tmp.getString("avatarUrl"), tmp.getString("idApi"));
                            list.add(friend);
                        }
                        manager.storeFriends(list);
                        if (friendsList != null) {
                            friendsList.clear();
                            friendsList.addAll(list);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        System.out.println("Lỗi lấy danh sách bạn bè");
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

    public class ListFriendAdapter extends RecyclerView.Adapter {
        private List<Friend> friends = new ArrayList<>();
        private Context mContext;

        public ListFriendAdapter(List<Friend> _friend, Context mContext) {
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
            ViewHolder h = (ViewHolder) holder;

            if (friend.avatarUrl.length() > 0) {
                Uri imageUri = Uri.parse(friend.avatarUrl);
                ((ViewHolder) holder).avatar_friend.setImageURI(imageUri);
            }
            ((ViewHolder) holder).callVideo.setOnClickListener(v -> {
                startCall(true,friends.get(position).idApi);
            });
            ((ViewHolder) holder).callVoice.setOnClickListener(v -> {
                startCall(false,friends.get(position).idApi);
            });

            ((ListFriendAdapter.ViewHolder) holder).name_friend.setText(friend.name);

            String idConversation = manager.getIdConversation(friend._id);

            ((View) h.name_friend.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle extras = new Bundle();
                    extras.putString("idConversation", idConversation);
                    extras.putString("idFriend", friend._id);
                    extras.putString("idApiFriend", friend.idApi);
                    extras.putString("nameConversation", friend.name);
                    extras.putInt("member_number", 2);

                    Intent intent = new Intent(mContext, ProfileFriend.class);
                    intent.putExtras(extras);
                    startActivity(intent);
                }
            });

            holder.getAdapterPosition();
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

    private void startCall(boolean isVideoCall, String id) {
//        if(CallService.)
        ArrayList<Integer> opponentsList = new ArrayList<>();
        opponentsList.add(Integer.valueOf(id));
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;

        QBRTCClient qbrtcClient = QBRTCClient.getInstance(getContext());
        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
        WebRtcSessionManager.getInstance(getContext()).setCurrentSession(newQbRtcSession);
        // Make Users FullName Strings and ID's list for iOS VOIP push
        String newSessionID = newQbRtcSession.getSessionID();
        ArrayList<String> opponentsIDsList = new ArrayList<>();
        ArrayList<String> opponentsNamesList = new ArrayList<>();
        List<QBUser> usersInCall = new ArrayList<>();

        // the Caller in exactly first position is needed regarding to iOS 13 functionality
        opponentsIDsList.add(id);
        opponentsNamesList.add(manager.getUser().name);

        String opponentsIDsString = TextUtils.join(",", opponentsIDsList);
        String opponentNamesString = TextUtils.join(",", opponentsNamesList);

        PushNotificationSender.sendPushMessage(opponentsList, manager.getUser().name, newSessionID, opponentsIDsString, opponentNamesString, isVideoCall);
        CallActivity.start(getContext(), false);
    }
}