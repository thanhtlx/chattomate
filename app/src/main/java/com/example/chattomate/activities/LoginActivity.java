package com.example.chattomate.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.example.chattomate.App;
import com.example.chattomate.MainActivity;
import com.example.chattomate.R;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.Conversation;
import com.example.chattomate.models.Message;
import com.example.chattomate.models.User;
import com.example.chattomate.service.API;
import com.example.chattomate.service.ServiceAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassWord;
    private TextView register, forgotPass;
    private Button btnLogin;
    private ProgressDialog pDialog;
    private ImageView ggLogin;
    private AppPreferenceManager manager;
    private ServiceAPI serviceAPI;

    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FB_TOKEN = "fb_token";
    public static final String LOGIN_URL = Config.HOST + Config.LOGIN_URL;
    public static String AUTH_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        manager = new AppPreferenceManager(getApplicationContext());

        edtEmail = (EditText) findViewById(R.id.email_regis);
        edtPassWord = (EditText) findViewById(R.id.inputPassword);
        forgotPass = (TextView) findViewById(R.id.forgot_passwd);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        register = (TextView) findViewById(R.id.register);
        ggLogin = (ImageView) findViewById(R.id.ggLogin);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait while login...");
        pDialog.setTitle("Login");
        pDialog.setCanceledOnTouchOutside(false);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAccount();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        ggLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LoginGoogleActivity.class));
            }
        });
    }

    public synchronized void loginAccount() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassWord.getText().toString().trim();

        if (email.isEmpty()) edtEmail.setError("Vui lòng nhập email");
        else if (!email.matches(emailPattern)) edtEmail.setError("Định dạng email không đúng");
        else if (password.isEmpty()) edtPassWord.setError("Hãy nhập mật khẩu");
        else {
            pDialog.show();

            JSONObject loginData = new JSONObject();
            try {
                loginData.put(KEY_EMAIL, email);
                loginData.put(KEY_PASSWORD, password);
//                loginData.put(KEY_FB_TOKEN,manager.getFBToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            API api = new API(this);
            api.Call(Request.Method.POST, LOGIN_URL, loginData, null, new APICallBack() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        String status = result.getString("status");
                        if(status.equals("success")) {
                            AUTH_TOKEN = result.getJSONObject("data").getString("token");
                            manager.saveToken(AUTH_TOKEN);

                            Calendar now = Calendar.getInstance();
                            now.add(Calendar.DATE,1);
                            manager.saveTimeToken(now);

                            JSONObject jsonObject = result.getJSONObject("data").getJSONObject("user");
                            User user = new User(jsonObject.getString("_id"),
                                    jsonObject.getString("name"),
                                    jsonObject.getString("avatarUrl"),"", email,password,jsonObject.getString("idApi"));

                            manager.setLogin(true);
                            manager.storeUser(user);
                            manager.setStateActive(true);
                            manager.setSilence(false);

                            getData();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                            App.initSocket();
//                            init socket when login success
                        } else Toast.makeText(LoginActivity.this,"login error (1)",Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "login error (2)", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    Log.d("debug",result.toString());
                    pDialog.dismiss();
                }

                @Override
                public void onError(JSONObject result) {
                    pDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_LONG).show();
//                    Log.d("debug",result.toString());
                }
            });

        }
    }

    public synchronized void getData() {
        serviceAPI = new ServiceAPI(this, manager);
        serviceAPI.getAll();

        serviceAPI.getFriends();
        serviceAPI.getAllConversation();
        serviceAPI.getAllFriendSendAdd();
        serviceAPI.getAllSendAddFriend();
    }
}