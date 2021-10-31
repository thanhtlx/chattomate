package com.example.chattomate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chattomate.activities.LoginActivity;
import com.example.chattomate.activities.SetupProfileActivity;
import com.example.chattomate.helper.AppPreferenceManager;
import com.example.chattomate.models.User;

public class MainActivity extends AppCompatActivity {
    private AppPreferenceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = new AppPreferenceManager(getApplicationContext());

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.clear();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });

        Button btnn = (Button) findViewById(R.id.btnn);
        btnn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SetupProfileActivity.class));
            }
        });
    }
}