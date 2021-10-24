package com.example.chattomate.accounts;

import android.app.ProgressDialog;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chattomate.MainActivity;
import com.example.chattomate.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputPass;
    private Button btnLogin, nextPageRegister, btnLoginGG;
    private ToggleButton displayPassWord;
    private ProgressDialog progressDialog;
    private TextView forgotPass;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private static final int LOGIN_REQUEST_GG = 10;
    public static int LOGIN_CODE = 0;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.login_activity);
        getSupportActionBar().hide();

        inputEmail = findViewById(R.id.edt_username);
        inputPass = findViewById(R.id.edt_passwd);
        displayPassWord =  findViewById(R.id.btn_displaypw);
        btnLogin = findViewById(R.id.btn_login);
        forgotPass =  findViewById(R.id.forgot_passwd);
        nextPageRegister = findViewById(R.id.creat_acc);
        btnLoginGG = findViewById(R.id.btn_login_gg);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);

        nextPageRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perForLogin();
            }
        });

        displayPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!displayPassWord.isChecked()) { // set mật khẩu ẩn đi
                    inputPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inputPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else { // set mật khẩu hiển thị
                    inputPass.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputPass.setTransformationMethod(null);
                }
            }
        });

        btnLoginGG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, LoginGoogleActivity.class));
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void perForLogin() {
        String email = inputEmail.getText().toString();
        String password = inputPass.getText().toString();

        if(!email.matches(emailPattern) || email.isEmpty()) inputEmail.setError("Invalid Email");
        else if(password.isEmpty() || password.length() < 8) inputPass.setError("Nhập mật khẩu dài ít nhất 8 ký tự");
        else {
            progressDialog.setMessage("Please wait while login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(LoginActivity.this,"Login successful",Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}