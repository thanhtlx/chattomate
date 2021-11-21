package com.example.chattomate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chattomate.R;
import com.example.chattomate.models.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String id;
    private int SELF = 100;
    private static String today;

    private Context mContext;
    private ArrayList<Message> messageArrayList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;

        public ViewHolder(View view) {
            super(view);
            message = itemView.findViewById(R.id.cMessage);
            timestamp = itemView.findViewById(R.id.timeStamp);
        }
    }


    public ChatRoomThreadAdapter(Context mContext, ArrayList<Message> messageArrayList, String _id) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.id = _id;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the chat message
        // left or right
        if (viewType == SELF) { // self message
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_self, parent, false);
        } else { // others message
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_other, parent, false);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        String e = message.sendBy.friend._id;
        if (e.equals(id)) {
            return SELF;
        }

        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        ((ViewHolder) holder).message.setText(message.content);

        String timestamp = getTimeStamp(message.sendAt);

        ((ViewHolder) holder).timestamp.setText(timestamp);
    }

    @Override
    public int getItemCount() {
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
}
