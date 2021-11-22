package com.example.chattomate.service;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.activities.ChatActivity;
import com.example.chattomate.activities.LoginActivity;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.Conversation;
import com.example.chattomate.models.Friend;
import com.example.chattomate.models.Message;
import com.example.chattomate.service.API;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceAPI {
    private Context context;
    private AppPreferenceManager manager;
    private Map<String, String> token;
    private final String URL_FRIEND       = Config.HOST + Config.FRIENDS_URL;
    private final String URL_CONVERSATION = Config.HOST + Config.CONVERSATION_URL;
    private final String URL_MESSAGE      = Config.HOST + Config.MESSAGE_URL;

    public ServiceAPI(Context c, AppPreferenceManager manager) {
        this.context = c;
        this.manager = manager;
        token = new HashMap<>();
        token.put("auth-token", manager.getToken(c));
    }

    /**
     * gui tin nhan
     * @param idConversation id conversation
     * @param type loai tin nhan
     */
    public void sendMessage(String idConversation, String type, String content) {
        JSONObject sendM = new JSONObject();
        try {
            sendM.put("conversation", idConversation);
            sendM.put("type", type);
            if(type.equals("1")) sendM.put("content", content);
            else if(type.equals("4") || type.equals("6")) sendM.put("contentUrl", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        API api = new API(context);
        api.Call(Request.Method.POST, URL_MESSAGE, sendM, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        JSONObject j = result.getJSONObject("data");

                        String id = j.getString("_id");
                        String content = j.getString("content");
                        String contentUrl = j.getString("contentUrl");
                        String sendAt = j.getString("createdAt");
                        Friend sender = new Friend(manager.getUser()._id, manager.getUser().name, manager.getUser().avatarUrl);

                        Message sen = new Message(idConversation, id, content, contentUrl, sendAt, null, sender, false, type);
                        manager.addMessage(sen, idConversation);
                        Log.d("debugGGGG",manager.getMessage(idConversation).get(
                                manager.getMessage(idConversation).size()-1).content);

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

    /**
     * lay tat ca tin nhan trong cuoc tro chuyen co id la id
     * @param idConversation id conversation
     */
    public void getAllMessage(String idConversation) {
        API api = new API(context);
        api.Call(Request.Method.GET, URL_MESSAGE+"/"+idConversation, null, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        JSONArray array = result.getJSONArray("data");
                        ArrayList<Message> messages = new ArrayList<>();
                        for(int i = 0; i < array.length(); i++) {
                            JSONObject j = array.getJSONObject(i);
                            String conversation = j.getString("conversation");
                            String id = j.getString("_id");
                            String content = j.getString("content");
                            String contentUrl = j.getString("contentUrl");
                            String sendAt = j.getJSONObject("sendBy").getString("createdAt");

                            JSONObject a = j.getJSONObject("sendBy");
                            Friend sender = new Friend(a.getString("_id"), a.getString("name"), a.getString("avatarUrl"));

                            String type = j.getString("type");
                            Boolean deleted = j.getJSONArray("deleteBy").length() > 0;

                            ArrayList<Friend> idSeenBy = new ArrayList<>();
                            if(j.getJSONArray("seenBy").length() > 0) {
                                for (int n = 0; n < j.getJSONArray("seenBy").length(); n++) {
                                    JSONObject b = j.getJSONArray("seenBy").getJSONObject(i);
                                    idSeenBy.add(new Friend(b.getString("_id"), b.getString("name"), b.getString("avatarUrl")));
                                }
                            }

                            messages.add(new Message(conversation, id, content, contentUrl, sendAt, idSeenBy, sender, deleted, type));
                        }

                        manager.storeMessage(messages, idConversation);
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

    /**
     * xoa 1 tin nhan co id la id
     * @param id id message
     */
    public void deleteAMessage(String id, String idConversation) {
        API api = new API(context);
        api.Call(Request.Method.PUT, URL_MESSAGE+"/"+id, null, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        manager.deleteAMessage(id, idConversation);
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

    /**
     * xoa cuoc tro chuyen co id la id
     * @param idConversation id conversation
     */
    public void deleteConversation(String idConversation) {
        API api = new API(context);
        api.Call(Request.Method.DELETE, URL_MESSAGE+"/"+idConversation, null, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        manager.deleteConversation(idConversation);
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

    /**
     * lấy tất cả danh sách bạn bè
     */
    public void getFriends() {
        API api = new API(context);

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
                                    tmp.getString("name"), tmp.getString("avatarUrl"));
                            list.add(friend);
                        }
                        manager.storeFriends(list);

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

    /**
     * gửi lời mời kết bạn tới bạn mới
     * @param id id friend
     */
    public void sendAddFriend(String id) {
        JSONObject newAddFriend = new JSONObject();
        try {
            newAddFriend.put("_id", id);
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
                        JSONObject jsonObject = result.getJSONObject("data");
                        Friend friend = new Friend(id, jsonObject.getString("nickName"));
                        manager.addRequestFriends(friend);

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

    /**
     * lấy tất cả lời mời kết bạn mình đã gửi
     */
    public void getAllSendAddFriend() {
        API api = new API(context);
        api.Call(Request.Method.GET, URL_FRIEND + "/request", null, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        ArrayList<Friend> list = new ArrayList<>();
                        JSONArray friends = result.getJSONArray("data");
                        for(int i = 0; i < friends.length(); i++) {
                            JSONObject temp = friends.getJSONObject(i);
                            JSONObject tmp = temp.getJSONObject("friend");
                            Friend friend = new Friend(temp.getString("_id"), temp.getString("nickName"),
                                    tmp.getString("name"), tmp.getString("avatarUrl"));

                            list.add(friend);
                        }
                        manager.storeRequestFriend(list);

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

    /**
     * lấy tất cả lời mời kết bạn gửi tới mình
     */
    public void getAllFriendSendAdd() {
        API api = new API(context);
        api.Call(Request.Method.GET, URL_FRIEND + "/pending", null, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        ArrayList<Friend> list = new ArrayList<>();
                        JSONArray friends = result.getJSONArray("data");
                        for(int i = 0; i < friends.length(); i++) {
                            JSONObject temp = friends.getJSONObject(i);
                            JSONObject tmp = temp.getJSONObject("friend");
                            Friend friend = new Friend(temp.getString("_id"), temp.getString("nickName"),
                                    tmp.getString("name"), tmp.getString("avatarUrl"));

                            list.add(friend);
                        }
                        manager.storePendingFriends(list);

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

    /**
     * chấp nhận lời mời kết bạn
     * @param id id friend
     */
    public void acceptFriend(String id) {
        String url = URL_FRIEND + "/:" + id + "/accept";
        JSONObject newAddFriend = new JSONObject();
        try {
            newAddFriend.put("_id", id);
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
                        Friend friend = manager.getFriend(manager.getPendingFriends(), id);
                        manager.deletePendingFriends(friend);
                        manager.addFriend(friend);
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

    /**
     * đặt nickname cho 1 bạn
     * @param id id friend
     * @param nickname nickname đặt
     */
    public void setNicknameFriend(String id, String nickname) {
        String url = URL_FRIEND + "/:" + id + "/change-nickname";
        JSONObject nickName = new JSONObject();
        try {
            nickName.put("_id", id);
            nickName.put("nickName", nickName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        API api = new API(context);
        api.Call(Request.Method.PUT, url, nickName, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        ArrayList<Friend> friends = manager.getFriends();
                        for(Friend friend : friends) {
                            if(friend._id.equals(id)) {
                                friend.nickName = nickname;
                                break;
                            }
                        }
                        manager.storeFriends(friends);

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

    /**
     * hủy lời mời kết bạn hoặc xóa bạn
     * @param id id friend
     */
    public void destroyFriend(String id) {
        String url = URL_FRIEND + "/:" + id;
        JSONObject friend = new JSONObject();
        try {
            friend.put("_id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        API api = new API(context);
        api.Call(Request.Method.DELETE, URL_FRIEND, friend, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        Friend friend1 = manager.getFriend(manager.getRequestFriends(),id);
                        Friend friend2 = manager.getFriend(manager.getFriends(),id);
                        Friend friend3 = manager.getFriend(manager.getPendingFriends(),id);

                        if(friend1 != null) manager.deleteRequestFriends(friend1);
                        else if(friend2 != null) manager.deleteFriend(friend2);
                        else if(friend3 != null) manager.deletePendingFriends(friend3);

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

    /**
     * tạo cuộc trò chuyện
     * @param friends member of conversation
     */
    public void newConversation(ArrayList<Friend> friends) {
        JSONArray jsonArray = new JSONArray();
        for(Friend friend : friends)
            jsonArray.put(friend._id);

        JSONObject members = new JSONObject();
        try {
            members.put("members", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        API api = new API(context);
        api.Call(Request.Method.POST, URL_CONVERSATION, members, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        JSONObject json = result.getJSONObject("data");
                        Conversation newCvst = new Conversation(json.getString("_id"),
                                json.getString("name"), json.getString("backgroundURI"),
                                json.getString("emoji"), friends);
                        manager.addConversation(newCvst);

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

    /**
     * lấy toàn bộ các cuộc trò chuyện
     */
    public void getAllConversation() {
        API api = new API(context);
        api.Call(Request.Method.GET, URL_CONVERSATION, null, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        JSONArray data = result.getJSONArray("data");
                        ArrayList<Conversation> conversations = new ArrayList<>();

                        for(int i = 0; i < data.length(); i++) {
                            JSONObject json = data.getJSONObject(i);
                            ArrayList<Friend> friends = new ArrayList<>();
                            JSONArray member = json.getJSONArray("members");
                            for(int j = 0; j < member.length(); j++) {
                                Friend friend = new Friend(member.get(j).toString());
                                friends.add(friend);
                            }

                            Conversation cv = new Conversation(json.getString("_id"),
                                    json.getString("name"), json.getString("backgroundURI"),
                                    json.getString("emoji"), friends);
                            conversations.add(cv);

                            getAllMessage(json.getString("_id"));
                        }
                        manager.storeConversation(conversations);

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

    /**
     * thêm thành viên vào cuộc trò chuyện
     * @param friends thành viên thêm vào
     * @param id id của cuộc trò chuyện
     */
    public void addMemberToConversation(String id, ArrayList<Friend> friends) {
        JSONArray jsonArray = new JSONArray();
        for(Friend friend : friends)
            jsonArray.put(friend._id);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", id);
            jsonObject.put("members", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        API api = new API(context);
        api.Call(Request.Method.PUT, URL_CONVERSATION + "/add-members", jsonObject, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        manager.updateConversation(id,null,null,null,1,friends);
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

    /**
     * xóa thành viên khỏi cuộc trò chuyện
     * @param friends thành viên xóa đi
     * @param id id của cuộc trò chuyện
     */
    public void removeMemberFromConversation(String id, ArrayList<Friend> friends) {
        JSONArray jsonArray = new JSONArray();
        for(Friend friend : friends)
            jsonArray.put(friend._id);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", id);
            jsonObject.put("members", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        API api = new API(context);
        api.Call(Request.Method.PUT, URL_CONVERSATION + "/remove-members", jsonObject, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        manager.updateConversation(id,null,null,null,-1,friends);
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

    /**
     * cập nhật cuộc trò chuyện
     * @param id id cuộc trò chuyện
     * @param name update
     * @param background update
     * @param emoji update
     */
    public void updateConversation(String id, String name, String background, String emoji) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("_id", id);
            if(name != null) jsonObject.put("name", name);
            if(background != null) jsonObject.put("backgroundURI", background);
            if(emoji != null) jsonObject.put("emoji", emoji);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        API api = new API(context);
        api.Call(Request.Method.PUT, URL_CONVERSATION, jsonObject, token, new APICallBack() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    String status = result.getString("status");
                    if(status.equals("success")) {
                        manager.updateConversation(id, name, background, emoji, 0, null);
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