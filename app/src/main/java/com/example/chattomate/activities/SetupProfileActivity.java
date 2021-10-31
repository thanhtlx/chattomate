package com.example.chattomate.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.example.chattomate.MainActivity;
import com.example.chattomate.R;
import com.example.chattomate.helper.AppPreferenceManager;
import com.example.chattomate.interfaces.APICallBack;
import com.example.chattomate.models.User;
import com.example.chattomate.service.API;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupProfileActivity extends AppCompatActivity {
    private AppPreferenceManager manager;
    private Toolbar toolbar;
    private EditText name, phone;
    private Button save;
    private CircleImageView avatar;
    private Uri imageUri;
    private static final int REQUEST_CODE = 101;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        getSupportActionBar().hide();
        manager = new AppPreferenceManager(getApplicationContext());

        toolbar = findViewById(R.id.bar_setup);
        toolbar.setTitle("Cập nhật thông tin cá nhân");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang cập nhật thông tin...");
        progressDialog.setCanceledOnTouchOutside(false);

        name = findViewById(R.id.inputName);
        phone = findViewById(R.id.phone);
        save = findViewById(R.id.btn_save);
        avatar = findViewById(R.id.profile_image);

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
        String url = imageUri.toString();
        User user = manager.getUser();

        if(cName.length() < 2) name.setError("");
        else if(cPhone.isEmpty()) phone.setError("");
        else {
            progressDialog.show();

            JSONObject setupData = new JSONObject();
            try {
                setupData.put("name", cName);
                setupData.put("phone", cPhone);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            API api = new API(this);
            api.Call(Request.Method.PUT, "", setupData, LoginActivity.AUTH_TOKEN,
                    new APICallBack() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        JSONObject jsonObject = result.getJSONObject("data").getJSONObject("user");
                        manager.editor.putString("name",cName);
                        manager.editor.putString("phone",cPhone);

                        finish();
                    } catch (JSONException e) {
                        Toast.makeText(SetupProfileActivity.this, "error", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    Toast.makeText(SetupProfileActivity.this, "Đã chỉnh sửa thông tin", Toast.LENGTH_LONG).show();
                    Log.d("debug",result.toString());
                    progressDialog.dismiss();
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
}