package com.example.chattomate.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.example.chattomate.R;
import com.example.chattomate.config.Config;
import com.example.chattomate.database.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.User;
import com.example.chattomate.service.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupProfileActivity extends AppCompatActivity {
    private AppPreferenceManager manager;
    private User user;
    private EditText name, phone;
    private Button save;
    private Toolbar toolbar;
    private CircleImageView avatar;
    private Uri imageUri;
    private static final int REQUEST_CODE = 101;
    private final String URL = Config.HOST + Config.UPDATE_PROFILE_URL;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        toolbar = findViewById(R.id.toolbar_setup);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Cập nhật thông tin cá nhân");
        bar.setDisplayHomeAsUpEnabled(true);

        manager = new AppPreferenceManager(getApplicationContext());
        user = manager.getUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang cập nhật thông tin...");
        progressDialog.setCanceledOnTouchOutside(false);

        avatar = findViewById(R.id.avatarChangepwd);
        name = findViewById(R.id.inputName);
        phone = findViewById(R.id.phone);
        save = findViewById(R.id.btn_save);

        if(user.avatarUrl.length() > 0) {
            imageUri = Uri.parse(user.avatarUrl);
            avatar.setImageURI(imageUri);
        }
        name.setText(user.name);
        if(user.phone.length() > 8) phone.setText(user.phone);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
    }

    private void saveData() {
        String cName = name.getText().toString();
        String cPhone = phone.getText().toString();
        String cAvatarUrl;
        if(imageUri != null) cAvatarUrl = imageUri.toString();
        else cAvatarUrl = "";

        if(cName.length() < 2) name.setError("Tên quá ngắn");
        else if(cPhone.length() < 9 && cPhone.length() > 0) phone.setError("Số điện thoại phải có ít nhất 9 chữ số");
        else {
            progressDialog.show();

            JSONObject setupData = new JSONObject();
            try {
                if(!cName.equals(user.name)) setupData.put("name", cName);
                if(!cPhone.equals(user.phone)) setupData.put("phone", cPhone);
                if(!cAvatarUrl.equals(user.avatarUrl)) setupData.put("avatarUrl", cAvatarUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            API api = new API(this);
            Map<String, String> token = new HashMap<>();
            token.put("auth-token", manager.getToken(this));

            api.Call(Request.Method.PUT, URL, setupData, token, new APICallBack() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        String status = result.getString("status");
                        if (status.equals("success")) {
                            user.name = cName;
                            user.phone = cPhone;
                            user.avatarUrl = cAvatarUrl;
                            manager.storeUser(user);

                            Toast.makeText(SetupProfileActivity.this, "Đã chỉnh sửa thông tin", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),"Error (*)",Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Error (**)",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    Log.d("debug",result.toString());
                }

                @Override
                public void onError(JSONObject result) {
                    progressDialog.dismiss();
                    Toast.makeText(SetupProfileActivity.this, "Cập nhật lỗi", Toast.LENGTH_LONG).show();
                    Log.d("debug",result.toString());
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK && data!=null) {
            imageUri = data.getData();
            avatar.setImageURI(imageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
}