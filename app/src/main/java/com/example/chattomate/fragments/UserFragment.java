package com.example.chattomate.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.chattomate.R;
import com.example.chattomate.activities.ChangePasswordActivity;
import com.example.chattomate.activities.LoginActivity;
import com.example.chattomate.activities.SetupProfileActivity;
import com.example.chattomate.activities.WaitingChatActivity;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment {
    private TextView nameUser;
    private CircleImageView avatarUser;
    private AppPreferenceManager manager;
    private User user;
    Switch turnoff_active, disturb;
    ImageView active_point;
    TextView wait_message, update_profile, log_out, change_pass;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        manager = new AppPreferenceManager(getContext());
        user = manager.getUser();

        avatarUser = view.findViewById(R.id.avatar_request);
        nameUser = view.findViewById(R.id.User_name);
        turnoff_active = view.findViewById(R.id.turnoffactive_switch);
        disturb = view.findViewById(R.id.donotdisturb_switch);
        update_profile = view.findViewById(R.id.changing_infor_text);
        change_pass = view.findViewById(R.id.changing_pass_text);
        log_out = view.findViewById(R.id.logout_text);
        active_point = view.findViewById(R.id.active_point);
        wait_message = view.findViewById(R.id.chat_waiting_text);

//        if(user.avatarUrl.length() > 0) avatarUser.setImageURI(Uri.parse(user.avatarUrl));
        nameUser.setText(manager.getUser().name);
        Log.d("debug", user.avatarUrl);

        turnoff_active.setChecked(manager.getStateActive());
        if(manager.getStateActive()) active_point.setVisibility(View.VISIBLE);
        else active_point.setVisibility(View.INVISIBLE);

        turnoff_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(turnoff_active.isChecked()) {
                    active_point.setVisibility(View.VISIBLE);
                    manager.setStateActive(true);
                } else {
                    manager.setStateActive(false);
                    active_point.setVisibility(View.INVISIBLE);
                }
            }
        });

        disturb.setChecked(manager.getSilence());
        disturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                manager.setSilence(disturb.isChecked());
            }
        });

        wait_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), WaitingChatActivity.class));
            }
        });

        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
            }
        });

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SetupProfileActivity.class));
            }
        });

        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.clear();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

//        if(user.avatarUrl.length() > 0) avatarUser.setImageURI(Uri.parse(user.avatarUrl));
        nameUser.setText(manager.getUser().name);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

}