package com.example.chattomate.account;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.chattomate.R;
import com.example.chattomate.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText edtPassWord, edtCfPassword, edtEmail;
    private Button btnRegister;
    private TextView login;
    private ProgressDialog pDialog;

    public static final String URL = "https://chattomate.cf/";
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
            StringRequest registerRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Register response: "+response);
                    String message = "";
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("success") == 1) {
                            User user = new User();
                            user.setEmail(jsonObject.getString("email"));
                            message = jsonObject.getString("message");
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        } else {
                            message = jsonObject.getString("message");
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                    pDialog.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    pDialog.dismiss();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(KEY_PASSWORD, password);
                    params.put(KEY_EMAIL, email);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(registerRequest);
        }
    }
}