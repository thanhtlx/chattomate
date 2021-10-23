package com.example.chattomate.accounts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chattomate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private TextView alreadyHaveAcc;
    private EditText inputEmail, inputPass, inputCfPass, editFirstName, editLastName;
    private Button btnRegister;
    private ToggleButton displayPassWord, displayCfPassWord;
    private final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.register_activity);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        alreadyHaveAcc = findViewById(R.id.alreadyHaveAcc);
        editLastName = findViewById(R.id.edt_last_name);
        editFirstName = findViewById(R.id.edt_first_name);
        inputEmail = findViewById(R.id.inputEmail);
        inputPass = findViewById(R.id.inputPass);
        inputCfPass = findViewById(R.id.inputCfPass);
        displayPassWord = findViewById(R.id.toggle_display_password);
        displayCfPassWord = findViewById(R.id.toggle_cf_display_password);
        btnRegister = findViewById(R.id.btn_register_page);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        displayPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!displayPassWord.isChecked()) { // ẩn mật khẩu
                    inputPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inputPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else { // hiển thị mật khẩu
                    inputPass.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputPass.setTransformationMethod(null);
                }
            }
        });

        displayCfPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!displayCfPassWord.isChecked()) { // ẩn mật khẩu
                    inputCfPass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inputCfPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else { // hiển thị mật khẩu
                    inputCfPass.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputCfPass.setTransformationMethod(null);
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perForAuth();
            }
        });
    }

    private void perForAuth() {
        String email = inputEmail.getText().toString();
        String password = inputPass.getText().toString();
        String cfPassword = inputCfPass.getText().toString();
        String firstName = editFirstName.getText().toString();
        String lastName = editLastName.getText().toString();

        if(firstName.isEmpty())  editFirstName.setError("Không được để trống");
        else if(lastName.isEmpty()) editLastName.setError("Không được để trống");
        else if(!email.matches(emailPattern) || email.isEmpty()) inputEmail.setError("Invalid Email");
        else if(password.isEmpty() || password.length() < 8) inputPass.setError("Nhập mật khẩu dài ít nhất 8 ký tự");
        else if(!password.equals(cfPassword)) inputCfPass.setError("Password not match both field");
        else {
            progressDialog.setMessage("Please wait while registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        progressDialog.dismiss();
                        sendUserToNextActivity();
                        Toast.makeText(RegisterActivity.this,"Registration successful",Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,""+task.getException(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}