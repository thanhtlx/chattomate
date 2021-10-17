package com.example.chattomate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.chattomate.accounts.LoginActivity;
import com.example.chattomate.models.UserGoogle;
import com.example.chattomate.models.UserName;

public class MainActivity extends AppCompatActivity {
    private TextView hello;
    private UserGoogle userGoogle;
    private UserName userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hello = findViewById(R.id.mainview);
        Intent intent = getIntent();
        if(LoginActivity.LOGIN_CODE == 0) {
            userName = (UserName) intent.getSerializableExtra("dataUsername");
            hello.setText("hello "+userName.getLastName());
        } else {
            userGoogle = (UserGoogle) intent.getSerializableExtra("dataUserGG");
            hello.setText("hello "+userGoogle.getName());
        }
    }
}