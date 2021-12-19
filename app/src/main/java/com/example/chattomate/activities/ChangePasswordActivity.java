package com.example.chattomate.activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.User;
import com.example.chattomate.service.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPw, newPw, cfNewPw;
    private Button save_changePw;
    private CircleImageView avatar;
    private TextView name;
    private Toolbar toolbar;
    ProgressDialog progressDialog;
    AppPreferenceManager manager;
    User user;
    private final String URL = Config.HOST + Config.UPDATE_PROFILE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        toolbar = findViewById(R.id.toolbar_changePwd);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Thay đổi mật khẩu");
        bar.setDisplayHomeAsUpEnabled(true);

        manager = new AppPreferenceManager(getApplicationContext());
        user = manager.getUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đổi mật khẩu...");
        progressDialog.setCanceledOnTouchOutside(false);

        oldPw = findViewById(R.id.old_password);
        newPw = findViewById(R.id.new_password);
        cfNewPw = findViewById(R.id.cf_new_password);
        save_changePw = findViewById(R.id.save_change_pwd);
        avatar = findViewById(R.id.avatarChangepwd);
        name = findViewById(R.id.nameChangepwd);

        if(!user.avatarUrl.isEmpty()) {
            Uri imageUri = Uri.parse(Config.HOST +user.avatarUrl);
            avatar.setImageURI(imageUri);
        }
        name.setText(user.name);

        save_changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void changePassword() {
        String old_pw = oldPw.getText().toString();
        String new_pw = newPw.getText().toString();
        String cf_new_pw = cfNewPw.getText().toString();

        if(checkPwd(old_pw)) oldPw.setError("Mật khẩu dài ít nhất 8 ký tự");
        else if(checkPwd(new_pw)) newPw.setError("Mật khẩu dài ít nhất 8 ký tự");
        else if(!cf_new_pw.equals(new_pw)) cfNewPw.setError("Không khớp với mật khẩu trên");
        else {
            if(!old_pw.equals(user.password)) {
                oldPw.setError("Mật khẩu sai");
                Toast.makeText(this,"Bạn nhập sai mật khẩu hiện tại",Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show();
                JSONObject changePwdData = new JSONObject();
                try {
                    changePwdData.put("password", new_pw);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                API api = new API(this);
                Map<String, String> token = new HashMap<>();
                token.put("auth-token", manager.getToken(this));
                api.Call(Request.Method.PUT, URL, changePwdData, token, new APICallBack() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    String status = result.getString("status");
                                    if (status.equals("success")) {
                                        user.password = new_pw;
                                        manager.storeUser(user);

                                        Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(),"error 1",Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(),"error 2",Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }

                                Log.d("debug",result.toString());
                            }

                            @Override
                            public void onError(JSONObject result) {
                                progressDialog.dismiss();
                                Toast.makeText(ChangePasswordActivity.this, "Change password error", Toast.LENGTH_LONG).show();
                                Log.d("debug",result.toString());
                            }
                        });

            }
        }
    }

    private boolean checkPwd(String password) {
        return password.length() < 8;
    }

}