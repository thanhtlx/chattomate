package com.example.chattomate.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chattomate.R;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment {
    private TextView nameUser;
    private CircleImageView avatarUser;
    private AppPreferenceManager manager;
    private User user;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new AppPreferenceManager(getActivity().getApplicationContext());
        user = manager.getUser();
//        avatarUser.setImageURI(Uri.parse(user.avatarUrl));
//        nameUser.setText(manager.getUser().name);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);



        return view;
    }}