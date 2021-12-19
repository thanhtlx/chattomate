package com.example.chattomate.adapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chattomate.App;
import com.example.chattomate.R;
import com.example.chattomate.activities.MapsActivity;
import com.example.chattomate.interfaces.ScrollChat;
import com.example.chattomate.models.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String id;
    private int type_message = 0; //other_content=6
    private int SELF = -10; //1
    private int SELF_RECORD = -11; //2
    private int SELF_IMAGE = -13; //3
    private int OTHER_RECORD = -12; //4
    private int OTHER_IMAGE = -14; //5
    private boolean group;
    private Context mContext;
    private ArrayList<Message> messageArrayList;
    private boolean isMe = false;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp, name_other;
        CircleImageView avatar_friend, imgplay, imgpause;
        ImageView imgContent;
        TextView txtTime_mic;

        public ViewHolder(View view) {
            super(view);
            avatar_friend = itemView.findViewById(R.id.avatar_other);
            message = itemView.findViewById(R.id.cMessage);
            timestamp = itemView.findViewById(R.id.timeStamp);
            if(group) name_other = itemView.findViewById(R.id.name_other);
            imgplay = view.findViewById(R.id.imgplay);
            imgpause = view.findViewById(R.id.imgpause);
            txtTime_mic = view.findViewById(R.id.txtTime_mic);
            imgContent = view.findViewById(R.id.imgContent);
        }
    }

    public ChatRoomThreadAdapter(Context mContext, ArrayList<Message> messageArrayList, boolean group, String _id) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.id = _id;
        this.group = group;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == SELF) { // self message
            isMe = true;
            type_message = 1;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_self, parent, false);
        } else if(viewType == SELF_IMAGE) {
            isMe = true;
            type_message = 3;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_self_image, parent, false);
        } else if(viewType == SELF_RECORD) {
            isMe = true;
            type_message = 2;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_self_record, parent, false);
        } else if(viewType == OTHER_RECORD) {
            isMe = false;
            type_message = 4;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_record, parent, false);
        } else if(viewType == OTHER_IMAGE) {
            isMe = false;
            type_message = 5;
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_image, parent, false);
        } else { // others message
            isMe = false;
            type_message = 6;
            if(group) itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_item_other, parent, false);
            else itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_other, parent, false);
        }

        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        if (message.sendBy._id.equals(id) && message.type.equals("1")) {
            return SELF;
        }
        if (message.sendBy._id.equals(id) && message.type.equals("4")) {
            return SELF_IMAGE;
        }
        if (message.sendBy._id.equals(id) && message.type.equals("5")) {
            return SELF_RECORD;
        }
        if (!message.sendBy._id.equals(id) && message.type.equals("4")) {
            return OTHER_IMAGE;
        }
        if (!message.sendBy._id.equals(id) && message.type.equals("5")) {
            return OTHER_RECORD;
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

        if(message.type.equals("1")) h.message.setText(message.content);
        if(message.type.equals("5") && false) {
            Uri uri = Uri.parse(message.contentUrl);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//            mmr.setDataSource(mContext, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int millSecond = Math.round(Integer.parseInt(durationStr)/1000);
            int minus = Math.round(millSecond/60);
            h.txtTime_mic.setText(String.format("%02d",minus)+":"+ String.format("%02d",millSecond - minus * 60));
            h.imgpause.setVisibility(View.INVISIBLE);

            h.imgplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    h.imgpause.setVisibility(View.VISIBLE);
                }
            });

        }

        if(type_message == 4 && false) {
            h.imgContent.setImageURI(Uri.parse(message.contentUrl));
            Log.d("debug", message.contentUrl);
        }

        if(message.sendBy.avatarUrl.length() > 0 && !isMe) {
//            h.avatar_friend.setImageURI(Uri.parse(message.sendBy.avatarUrl));
        }

        if (group)
            if (message.sendBy.nickName.length() > 0)
                h.name_other.setText(message.sendBy.nickName);
            else h.name_other.setText(message.sendBy.name);

        String timestamp = App.getTimeStamp(message.sendAt);
        h.timestamp.setText(timestamp);
    }

    private boolean checkTimeShareLocation(String time) {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(format.parse(time));
            cal.add(Calendar.MINUTE, 30);
            Calendar ins = Calendar.getInstance();
            return cal.getTimeInMillis() > ins.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("DEBUG","error parse");
        }
        return false;
    }

    public void saveImage(Bitmap bitmap) throws FileNotFoundException {
        String filename = "IMAGE_"+System.currentTimeMillis()+".jpg";
        OutputStream outputStream;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = mContext.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,filename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
            Uri uri =resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
            outputStream = (OutputStream) resolver.openOutputStream(uri);
        } else {
            String path = Environment.getExternalStorageDirectory().toString();
            File folder = new File(path+"/"+"image");
            if (!folder.exists()) folder.mkdirs();
            File file = new File(folder,filename);
            if (file.exists()) file.delete();
            outputStream = new FileOutputStream(file);
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        }

        boolean isSave= bitmap.compress(Bitmap.CompressFormat.JPEG,50,outputStream);
        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isSave) {
            Toast.makeText(mContext,"Save Success!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext,"save fail!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        if(messageArrayList == null) return 0;
        return messageArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        int idx =  messageArrayList.get(position)._id.hashCode();
        Log.d("DEBUG", String.valueOf(idx));
        return idx;
    }
}
