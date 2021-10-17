package com.example.chattomate.accounts;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.chattomate.database.DBChattomate;
import com.example.chattomate.R;
import com.example.chattomate.database.EncryptDecrypt;
import com.example.chattomate.users.UserName;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editFirstName, editLastName;
    private EditText editUsername, editUserPassword, editUserCfPassword;
    private Button buttonRegister;
    private ToggleButton displayPassWord, displayCfPassWord;

    private DBChattomate db;
    private String hashInPutPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        init();

        db = new DBChattomate(this);
        buttonRegister.setOnClickListener(this);
        displayPassWord.setOnClickListener(this);
        displayCfPassWord.setOnClickListener(this);
    }

    public void init() {
        editFirstName = findViewById(R.id.edt_first_name);
        editLastName = findViewById(R.id.edt_last_name);
        editUsername = findViewById(R.id.edt_user_register);
        editUserPassword = findViewById(R.id.edt_passWd_register);
        editUserCfPassword = findViewById(R.id.edt_confirm_passWd);

        displayPassWord = findViewById(R.id.toggle_display_password);
        displayCfPassWord = findViewById(R.id.toggle_cf_display_password);
        buttonRegister = findViewById(R.id.btn_register_page);
    }

    // click vào các button gọi đến sự kiện
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_register_page) {
            UserName userName = createUser();
            if(userName != null) {
                boolean flag = db.insertUser(userName);
                if(flag) {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("registerSuccessfully", getResources().getString(R.string.register_succesfully));
                    startActivity(intent);
                } else {
                    showToast(getResources().getString(R.string.insert_false));
                }
            }

        } else if(view.getId() == R.id.toggle_display_password) {
            if(!displayPassWord.isChecked()) { // ẩn mật khẩu
                editUserPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editUserPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else { // hiển thị mật khẩu
                editUserPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                editUserPassword.setTransformationMethod(null);
            }

        } else if(view.getId() == R.id.toggle_cf_display_password) {
            if(!displayCfPassWord.isChecked()) { // ẩn mật khẩu
                editUserCfPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editUserCfPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            } else { // hiển thị mật khẩu
                editUserCfPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                editUserCfPassword.setTransformationMethod(null);
            }
        }
    }

    @SuppressLint("NewApi")
    private UserName createUser() {
        UserName userName = null;
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String username = editUsername.getText().toString().trim();
        String password = editUserPassword.getText().toString().trim();
        String cfPassword = editUserCfPassword.getText().toString().trim();
        boolean checkEmptyFirstName = checkInputEmpty(firstName, editFirstName);
        boolean checkEmptyLastName = checkInputEmpty(lastName, editLastName);
        boolean checkEmptyEmail = checkInputEmpty(username, editUsername);
        boolean checkUsername      = db.checkUsernameExists(username);
        boolean checkEmptyPassword = checkInputEmpty(password, editUserPassword);
        boolean checkEmptyCfPassword = checkInputEmpty(cfPassword, editUserCfPassword);

        // bất kể 1 trường nào còn trống thì đều báo lỗi
        if(checkEmptyFirstName || checkEmptyLastName || checkEmptyEmail || checkEmptyPassword || checkEmptyCfPassword) {
            showToast(getResources().getString(R.string.error_information));

        } else if(checkUsername) { // nếu tồn tại user trong database thì error
            showToast(getResources().getString(R.string.error_user_exists));
//        } else if(!checkInputPassword(password)) {
//            showToast("Mật khẩu phải có ít nhất 1 chữ cái Hoa và 1 chữ số");
        } else if(!password.equals(cfPassword)) { // nếu 2 mật khẩu ko khớp nhau thì error
            showToast(getResources().getString(R.string.error_confirm_password));
        } else { // ok. khi thỏa mãn hết thì lưu
            showToast("Đăng ký thành công!");
            userName = new UserName(firstName, lastName, username, hashInPutPassword);
        }

        return userName;
    }

    // check input empty
    @SuppressLint("NewApi")
    private boolean checkInputEmpty(String input, EditText setBackground) {
        if(input.isEmpty()) {
            //setBackground.setBackground(getResources().getDrawable(R.drawable.custom_background_error));
            return true;
        } else {
            // nếu mà input truyền vào là mk thì hash mk đó
            if (input.equals(editUserPassword.getText().toString())) {
                try {
                    hashInPutPassword = EncryptDecrypt.encrypt(input);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    }

    // check input password
    public boolean checkInputPassword(String input) {
        String regex = "^[a-zA-Z0-9]{1}([a-z0-9]*[.#$^+=!*()@%&]{1}[a-z0-9]*){8,20}$";// Lx!ssosdsd
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}