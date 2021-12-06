package com.example.chattomate.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chattomate.R;
import com.example.chattomate.interfaces.ScrollChat;
import com.example.chattomate.models.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String id;
    private int SELF = -10;
    private static String today;
    private boolean group;

    private Context mContext;
    private ArrayList<Message> messageArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp, name_other;

        public ViewHolder(View view) {
            super(view);
            message = itemView.findViewById(R.id.cMessage);
            timestamp = itemView.findViewById(R.id.timeStamp);
            if(group) name_other = itemView.findViewById(R.id.name_other);
        }
    }

    public ChatRoomThreadAdapter(Context mContext, ArrayList<Message> messageArrayList, boolean group, String _id) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.id = _id;
        this.group = group;
        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == SELF) { // self message
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_self, parent, false);
        } else { // others message
            if(group) itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_other, parent, false);
            else itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_other, parent, false);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.sendBy._id.equals(id)) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if(position == messageArrayList.size()-1) {
            ((ScrollChat)mContext).ScrollRecycleView();
        }
        Message message = messageArrayList.get(position);

        ViewHolder h = (ViewHolder) holder;
        h.message.setText(message.content);
        h.timestamp.setText(message.sendAt);

        if (group)
            if (message.sendBy.nickName.length() > 0) h.name_other.setText(message.sendBy.nickName);
            else h.name_other.setText(message.sendBy.name);

//        String timestamp = getTimeStamp(message.sendAt);
//        h.timestamp.setText(timestamp);
    }

    @Override
    public int getItemCount() {
        if(messageArrayList == null) return 0;
        return messageArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    @Override
    public long getItemId(int position) {
        int idx =  messageArrayList.get(position)._id.hashCode();
        Log.d("DEBUG", String.valueOf(idx));
        return idx;
    }
}
