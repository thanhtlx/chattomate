package com.example.chattomate;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.activities.ProfileFriend;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.fragments.ChatFragment;
import com.example.chattomate.fragments.FriendsFragment;
import com.example.chattomate.fragments.UserFragment;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.interfaces.SocketCallBack;
import com.example.chattomate.models.Conversation;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.Message;
import com.example.chattomate.service.API;
import com.example.chattomate.service.ServiceAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.chattomate.config.Config.REQUEST_PERMISTION_MIC;
import static com.example.chattomate.config.Config.REQUEST_PERMISTION_OPEN_CAMERA;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigationView bnt;
    Toolbar toolbar;
    SearchView searchView;
    SearchManager searchManager;
    AppPreferenceManager manager;
    ServiceAPI serviceAPI;
    ArrayList<Friend> allUsers = new ArrayList<>();
    private String URL = Config.HOST + Config.UPDATE_PROFILE_URL;
    private String URL_FRIEND       = Config.HOST + Config.FRIENDS_URL;
    HashMap<String, String> token;
    MatrixCursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        manager = new AppPreferenceManager(getApplicationContext());
        token = manager.getMapToken(this);
        allUsers = manager.getAllUsers();

//        serviceAPI = new ServiceAPI(this, manager);
//        serviceAPI.getAllFriendSendAdd();
//        serviceAPI.getAllSendAddFriend();

        viewPager = findViewById(R.id.view_pager);
        bnt = findViewById(R.id.bottom_navigation);

        // Cursor
        String[] columns = new String[] { "_id", "email" };
        Object[] temp = new Object[] { 0, "default" };

        cursor = new MatrixCursor(columns);

        if(allUsers != null) for(int i = 0; i < allUsers.size(); i++) {
            temp[0] = i;
            temp[1] = allUsers.get(i);
            cursor.addRow(temp);
        }

        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = findViewById(R.id.search_view);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Tìm kiếm");
        searchView.setSuggestionsAdapter(new SearchAdapter(this, cursor, allUsers));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bnt.getMenu().findItem(R.id.chat).setChecked(true);
                        break;
                    case 2:
                        bnt.getMenu().findItem(R.id.friend).setChecked(true);
                        break;
                    case 3:
                        bnt.getMenu().findItem(R.id.more).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bnt.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.chat:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.friend:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.more:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

        requirePermission();
    }

    private void requirePermission() {
        if (Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                requestPermissions( new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO},REQUEST_PERMISTION_OPEN_CAMERA);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppPreferenceManager manager = new AppPreferenceManager(getApplicationContext());
        Log.d("DEBUG", String.valueOf(manager.tokenValid()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        API api = new API(this);
        api.Call(Request.Method.GET, URL, null, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        JSONArray array = result.getJSONArray("data");
                        ArrayList<Friend> usersList = new ArrayList<>();
                        for(int i = 0; i < array.length(); i++) {
                            JSONObject j = array.getJSONObject(i);

                            Friend f = new Friend(j.getString("_id"));
                            f.name = j.getString("name");
                            f.avatarUrl = j.getString("avatarUrl");
                            f.email = j.getString("email");
                            f.idApi = j.getString("idApi");

                            usersList.add(f);
                        }
                        manager.storeAllUsers(usersList);
                        if(allUsers != null) allUsers.clear();
                        allUsers = usersList;
                        searchView.setSuggestionsAdapter(new SearchAdapter(MainActivity.this, cursor, allUsers));

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.add_new_message:
                //Tạo tin nhắn mới

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1:
                    return new FriendsFragment();
                case 2:
                    return new UserFragment();
                case 0:
                default:
                    return new ChatFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    class SearchAdapter extends CursorAdapter {
        private ArrayList<Friend> friendArrayList;
        private TextView name_search, email_search;
        Button button;
        CircleImageView imageView;

        public SearchAdapter(Context context, Cursor cursor, ArrayList<Friend> friendArrayList) {
            super(context, cursor, false);
            this.friendArrayList = friendArrayList;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Friend friend = friendArrayList.get(cursor.getPosition());
            name_search.setText(friend.name);
            email_search.setText(friend.email);

            if (friend.avatarUrl.length() > 0) imageView.setImageURI(Uri.parse(friend.avatarUrl));

            if (manager.getFriend(manager.getFriends(), friend._id) != null) {
                button.setText("Nhắn tin");
                button.setBackgroundColor(Color.parseColor("#E6BBF8"));
            } else if (manager.getFriend(manager.getRequestFriends(), friend._id) != null) {
                button.setText("Đã gửi lời\nkết bạn");
                button.setBackgroundColor(Color.parseColor("#E6BBF8"));
            } else if(manager.getFriend(manager.getPendingFriends(), friend._id) != null) {
                button.setText("Đồng ý\nkết bạn");
                button.setBackgroundColor(Color.parseColor("#EF1FF6"));
            } else if(friend._id.equals(manager.getUser()._id)) {
                button.setText("Me");
                button.setBackgroundColor(Color.parseColor("#E6BBF8"));
            } else {
                button.setText("Kết bạn");
                button.setBackgroundColor(Color.parseColor("#EF1FF6"));
            }

            String btn = button.getText().toString();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(button.getText().toString().equals("Nhắn tin")) {
                        Bundle extr = new Bundle();
                        String idConversation = manager.getIdConversation(friend._id);

                        if(idConversation == null) {
                            ArrayList<Friend> members = new ArrayList<>();
                            members.add(friend);
                            members.add(manager.getUserAsFriend());

                            String URL_CONVERSATION = Config.HOST + Config.CONVERSATION_URL;

                            JSONArray jsonArray = new JSONArray();
                            for(Friend f : members)
                                jsonArray.put(f._id);

                            JSONObject m = new JSONObject();
                            try {
                                m.put("members", jsonArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            API api = new API(context);
                            api.Call(Request.Method.POST, URL_CONVERSATION, m, token, new APICallBack() {
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

                                            extr.putString("idConversation", json.getString("_id"));
                                            extr.putString("idFriend", friend._id);
                                            extr.putString("idApiFriend", friend.idApi);
                                            extr.putString("nameConversation", friend.name);
                                            extr.putInt("member_number", 2);

                                            Intent intent = new Intent(context, ChatActivity.class);
                                            intent.putExtras(extr);
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

                    } else if(button.getText().toString().equals("Kết bạn")) {
                        JSONObject newAddFriend = new JSONObject();
                        try {
                            newAddFriend.put("userId", friend._id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        API api = new API(context);
                        api.Call(Request.Method.POST, URL_FRIEND, newAddFriend, token, new APICallBack() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    String status = result.getString("status");
                                    if(status.equals("success")) {
                                        Friend friendx = new Friend(friend._id);
                                        manager.addRequestFriends(friendx);
                                        button.setText("Đã gửi lời\nkết bạn");
                                        button.setBackgroundColor(Color.parseColor("#E6BBF8"));
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
                    } else if(button.getText().toString().equals("Đồng ý kết bạn")) {
                        String url = URL_FRIEND + "/:" + friend._id + "/accept";
                        JSONObject newAddFriend = new JSONObject();
                        try {
                            newAddFriend.put("id", friend._id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        API api = new API(context);
                        api.Call(Request.Method.PUT, url, newAddFriend, token, new APICallBack() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    String status = result.getString("status");
                                    if(status.equals("success")) {
                                        Friend f = manager.getFriend(manager.getPendingFriends(), friend._id);
                                        manager.deletePendingFriends(f);
                                        manager.addFriend(f);
                                        button.setText("Nhắn tin");
                                        button.setBackgroundColor(Color.parseColor("#E6BBF8"));
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
            });
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_user_search, parent, false);

            name_search = view.findViewById(R.id.name_users);
            email_search = view.findViewById(R.id.email_users);
            imageView = view.findViewById(R.id.avatar_search);
            button = view.findViewById(R.id.add_friend_search);

            return view;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PERMISTION_OPEN_CAMERA || requestCode == REQUEST_PERMISTION_MIC) {
            requirePermission();
        }
    }
}