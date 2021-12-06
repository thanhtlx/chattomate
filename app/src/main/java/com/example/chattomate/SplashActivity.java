package com.example.chattomate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chattomate.activities.LoginActivity;
import com.example.chattomate.call.LoginService;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.models.User;
import com.quickblox.users.model.QBUser;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        System.out.println(getApplicationContext());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Check if user is already logged in or not
                AppPreferenceManager manager = new AppPreferenceManager(getApplicationContext());
                Intent intent;
                if (manager.isLoggedIn()) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                    loginQB(manager.getUser());
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(runnable,1500);
    }
    private void loginQB(User user) {
        QBUser qbUser = new QBUser(user.email, App.USER_DEFAULT_PASSWORD);
        qbUser.setId(Integer.parseInt(user.idApi));
        LoginService.start(this, qbUser);
    }

}