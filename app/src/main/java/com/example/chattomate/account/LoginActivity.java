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
import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.config.Config;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.service.API;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getSimpleName();
    private EditText edtEmail, edtPassWord;
    private TextView register, forgotPass;
    private Button btnLogin;
    private ProgressDialog pDialog;
    private ImageView ggLogin;

    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
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

        if (email.isEmpty()) edtEmail.setError("Vui lòng nhập email");
        else if (!email.matches(emailPattern)) edtEmail.setError("Định dạng email không đúng");
        else if (password.isEmpty()) edtPassWord.setError("Hãy nhập mật khẩu");
        else {
            pDialog.setMessage("Please wait while login...");
            pDialog.setTitle("Login");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
            JSONObject loginData = new JSONObject();
            try {
                loginData.put(KEY_EMAIL, email);
                loginData.put(KEY_PASSWORD, password);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String loginUrl = Config.HOST + Config.LOGIN_URL;
            API api = new API(this);
            api.Call(Request.Method.POST, loginUrl, loginData, null, new APICallBack() {
                @Override
                public void onSuccess(JSONObject result) {
//                    vào đây là login thành công
//                    lưu lại thông tin người
//                    token sẽ được trả lại ở đây và chỉ có tác dụng trong 1 ngày, hết 1 ngày thì phải
                    Log.d("debug",result.toString());
                    pDialog.dismiss();
                }

                @Override
                public void onError(JSONObject result) {
//                    vào đây là login thất bại mật khẩu sai hoặc gì đó
                    pDialog.dismiss();
                    Log.d("debug",result.toString());
                }
            });

        }
    }
}
