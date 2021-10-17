package com.example.chattomate.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chattomate.database.DBChattomate;
import com.example.chattomate.MainActivity;
import com.example.chattomate.R;
import com.example.chattomate.database.EncryptDecrypt;
import com.example.chattomate.users.UserGoogle;
import com.example.chattomate.users.UserName;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText userLogin, userPassword;
    private TextView forgotPassword, nextPageRegister, displayRegisterSuccessful;
    private Button buttonLogin;
    private SignInButton btnLoginGG;
    private ToggleButton displayPassword;
    private DBChattomate db;
    private List<UserName> listUserName;
    private List<UserGoogle> userGoogles;

    String hashPassword = "";
    private static final int LOGIN_REQUEST_GG = 10;
    public static int LOGIN_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        db = new DBChattomate(this);
        init();

        // nhận dữ liệu gửi sang khi đăng kí hoac doi matkhau thành công
        Intent intent = getIntent();
        String registerSuccessfully = intent.getStringExtra("registerSuccessfully");
        String changePassword = intent.getStringExtra("changePassword");
        if (registerSuccessfully != null) {
            displayRegisterSuccessful.setVisibility(View.VISIBLE);
            displayRegisterSuccessful.setText(registerSuccessfully);
        } else if(changePassword != null) {
            displayRegisterSuccessful.setVisibility(View.VISIBLE);
            displayRegisterSuccessful.setText(changePassword);
        }

        buttonLogin.setOnClickListener(this);
        btnLoginGG.setOnClickListener(this);
        displayPassword.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        nextPageRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_login) {
            functionLogin();

        } else if(view.getId() == R.id.btn_login_gg) {
            LOGIN_CODE = 1;
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            GoogleSignInClient client = GoogleSignIn.getClient(this, options);
            Intent intent = client.getSignInIntent();
            //setResult(LoginActivity.RESULT_OK,intent);
            startActivityForResult(intent, LOGIN_REQUEST_GG);

        } else if(view.getId() == R.id.btn_displaypw) {
            if (!displayPassword.isChecked()) { // set mật khẩu ẩn đi
                userPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            } else { // set mật khẩu hiển thị
                userPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                userPassword.setTransformationMethod(null);
            }

        } else if(view.getId() == R.id.forgot_passwd) { // quên mật khẩu
            //startActivity(new Intent(LoginActivity.this, ForgotPassword.class));

        } else if(view.getId() == R.id.creat_acc) { // chưa có tài khoản => đăng kí
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LOGIN_REQUEST_GG) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult();
            UserGoogle userGoogle = new UserGoogle(account.getEmail(), account.getDisplayName().trim());
            if(account != null) {
                Log.d("LoginGoogle", "name: " + account.getDisplayName());
                Log.d("LoginGoogle","id: " + account.getId());
            }
            if(db.checkEmailLogin(userGoogle.getUserEmail())) {
                userGoogles = db.getDataUserGoogle(userGoogle.getUserEmail());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("dataUserGG", (Serializable) userGoogles.get(userGoogles.size()-1));
                startActivity(intent);
            } else {
                if(db.insertUserEmail(userGoogle)) {
                    startActivity(new Intent(this, MainActivity.class));
                }
            }
//            if(resultCode == RESULT_OK) {
//                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//                handleSignin(task);
//            } else {
//                showToast("Login with Google ko khả dụng.");
//            }
        }
    }

    // click vào login: kiểm tra đầu vào của người dùng
    private void functionLogin() {
        String username = userLogin.getText().toString().trim();
        String userPassword = this.userPassword.getText().toString().trim();

        boolean checkEmptyUsername = checkInputEmpty(username);
        boolean checkEmptyPassword = checkInputEmpty(userPassword);

        if(checkEmptyUsername || checkEmptyPassword) {
            showToast(getResources().getString(R.string.error_login_empty));
        } else {
            // check trong database xem có username và mật khẩu trùng ko
            boolean flag = db.checkUserLogin(username, hashPassword);
            if(flag) {
                // nếu mà tồn tại thì sẽ đăng nhập thành công
                listUserName = db.getDataUser(username);
                Intent intent = new Intent(this, MainActivity.class);
                // muốn gửi dữ liệu đi phải implement class Serializable ở class User
                // gửi list dữ liệu đi
                intent.putExtra("dataUsername", (Serializable) listUserName.get(listUserName.size()-1));
                startActivity(intent);
            } else {
                showToast(getResources().getString(R.string.error_login));
            }
        }
    }

    // check input empty
    @SuppressLint("NewApi")
    private boolean checkInputEmpty(String input) {
        if(input.isEmpty()) {
            return true;
        } else {
            // nếu mà input truyền vào là mk thì hash mk đó
            if (input.equals(userPassword.getText().toString())) {
                try {
                    hashPassword = EncryptDecrypt.encrypt(input);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    private void init() {
        userLogin =  findViewById(R.id.edt_username);
        userPassword =  findViewById(R.id.edt_passwd);
        forgotPassword =  findViewById(R.id.forgot_passwd);
        nextPageRegister = findViewById(R.id.creat_acc);
        buttonLogin =  findViewById(R.id.btn_login);
        displayPassword =  findViewById(R.id.btn_displaypw);
        listUserName = new ArrayList<>();
        btnLoginGG = findViewById(R.id.btn_login_gg);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}