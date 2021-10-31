package com.example.chattomate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.helper.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.User;
import com.example.chattomate.service.API;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity {
    private EditText oldPw, newPw, cfNewPw;
    private Button save_changePw;
    private Toolbar toolbar;
    ProgressDialog progressDialog;
    AppPreferenceManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().hide();
        manager = new AppPreferenceManager(getApplicationContext());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đổi mật khẩu...");
        progressDialog.setCanceledOnTouchOutside(false);

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
        String old_pw = oldPw.getText().toString();
        String new_pw = newPw.getText().toString();
        String cf_new_pw = cfNewPw.getText().toString();
        User user = manager.getUser();

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
                api.Call(Request.Method.PUT, "", changePwdData, LoginActivity.AUTH_TOKEN,
                        new APICallBack() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                manager.editor.putString("password",new_pw).commit();
//                                try {
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }

                                Log.d("debug",result.toString());
                                progressDialog.dismiss();
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
        return password.length() > 8;
    }
}