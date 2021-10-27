package com.example.chattomate.account;

import androidx.appcompat.app.AppCompatActivity;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chattomate.MainActivity;
import com.example.chattomate.R;
import com.example.chattomate.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getSimpleName();
    private EditText edtEmail, edtPassWord;
    private TextView register, forgotPass;
    private Button btnLogin;
    private ProgressDialog pDialog;
    private ImageView ggLogin;

    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final String URL = "https://chattomate.cf/";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        edtEmail = (EditText) findViewById(R.id.email_regis);
        edtPassWord = (EditText) findViewById(R.id.inputPassword);
        forgotPass = (TextView) findViewById(R.id.forgot_passwd);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        register = (TextView) findViewById(R.id.register);
        ggLogin = (ImageView) findViewById(R.id.ggLogin);
        pDialog = new ProgressDialog(this);

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

            }
        });
    }

    public void loginAccount() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassWord.getText().toString().trim();

        if(email.isEmpty()) edtEmail.setError("Vui lòng nhập email");
        else if(!email.matches(emailPattern)) edtEmail.setError("Định dạng email không đúng");
        else if(password.isEmpty()) edtPassWord.setError("Hãy nhập mật khẩu");
        else {
            pDialog.setMessage("Please wait while login...");
            pDialog.setTitle("Login");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
            StringRequest requestLogin = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Login response: "+response);
                    String message = "";
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("success") == 1) {
                            User user = new User();
                            user.setEmail(jsonObject.getString("email"));
                            message = jsonObject.getString("message");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("login", user);
                            startActivity(intent);
                        } else {
                            message = jsonObject.getString("message");
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Login error response: " + error.getMessage());
                    pDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(KEY_EMAIL, email);
                    params.put(KEY_PASSWORD, password);
                    return params;
                }


            };


            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(requestLogin);
        }
    }
}