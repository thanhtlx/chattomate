package com.example.chattomate.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.config.Config;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.service.API;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText edtPassWord, edtCfPassword, edtEmail;
    private Button btnRegister;
    private TextView login;
    private ProgressDialog pDialog;

    public static final String REGIS_URL = Config.HOST + Config.REGISTER_URL;
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_EMAIL = "email";
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        edtPassWord = (EditText) findViewById(R.id.passwd_regis);
        edtCfPassword = (EditText) findViewById(R.id.cfpasswd_regis);
        edtEmail = (EditText) findViewById(R.id.email_regis);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        login = (TextView) findViewById(R.id.login);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait while registration...");
        pDialog.setTitle("Registration");
        pDialog.setCanceledOnTouchOutside(false);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassWord.getText().toString().trim();
        String cfPassword = edtCfPassword.getText().toString().trim();

        if(email.isEmpty()) edtEmail.setError("Vui lòng nhập email");
        else if(!email.matches(emailPattern)) edtEmail.setError("Định dạng email không đúng");
        else if(password.length() < 8) edtPassWord.setError("Đặt mật khẩu dài ít nhất 8 ký tự");
        else if(!cfPassword.equals(password)) edtCfPassword.setError("Không khớp với mật khẩu trên");
        else {
            pDialog.show();

            JSONObject regisData = new JSONObject();
            try {
                regisData.put(KEY_EMAIL, email);
                regisData.put(KEY_PASSWORD, password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            API api = new API(this);
            api.Call(Request.Method.POST, REGIS_URL, regisData, null, new APICallBack() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        String status = result.getString("status");
                        if(status.equals("success")) {
                            Toast.makeText(getApplicationContext(),"Registration Success",Toast.LENGTH_LONG).show();
                            pDialog.dismiss();

                            Intent login = new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(login);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"registration error 1",Toast.LENGTH_LONG).show();
                            pDialog.dismiss();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"registration error 2",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_LONG).show();
                    Log.d("debug",result.toString());
                    pDialog.dismiss();
                }

                @Override
                public void onError(JSONObject result) {
                    //register thất bại
                    pDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                    Log.d("debug",result.toString());
                }
            });

        }

    }
}