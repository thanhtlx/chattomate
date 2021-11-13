package com.example.chattomate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chattomate.activities.LoginActivity;
import com.example.chattomate.database.AppPreferenceManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        getSupportActionBar().hide();

        System.out.println(getApplicationContext());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Check if user is already logged in or not
                AppPreferenceManager manager = new AppPreferenceManager(getApplicationContext());
                Intent intent;
                if (manager.isLoggedIn()) {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
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

}