package com.example.chattomate.account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toolbar;

import com.example.chattomate.R;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPw, newPw, cfNewPw;
    private Button save_changePw;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().hide();

        toolbar = findViewById(R.id.bar_changePw);
        toolbar.setTitle("Thay đổi mật khẩu");
        oldPw = findViewById(R.id.old_password);
        newPw = findViewById(R.id.new_password);
        cfNewPw = findViewById(R.id.cf_new_password);
        save_changePw = findViewById(R.id.save_change_pwd);

        save_changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

    }

    private void changePassword() {

    }
}