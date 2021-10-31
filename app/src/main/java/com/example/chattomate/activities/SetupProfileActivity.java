package com.example.chattomate.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.chattomate.R;

public class SetupProfileActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText name, phone;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        getSupportActionBar().hide();

        toolbar = findViewById(R.id.bar_setup);
        toolbar.setTitle("Cập nhật thông tin cá nhân");

        name = findViewById(R.id.inputName);
        phone = findViewById(R.id.phone);
        save = findViewById(R.id.btn_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}