package com.example.chattomate.accounts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chattomate.MainActivity;
import com.example.chattomate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupProfileActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText inputName, inputCity;
    private Button save;
    private static final int REQUEST_CODE = 101;
    private Uri imageUri;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getSupportActionBar().hide();
        toolbar = findViewById(R.id.app_bar);
        toolbar.setTitle("Setup Profile");

        profileImageView = findViewById(R.id.profile_image);
        inputName = findViewById(R.id.inputName);
        inputCity = findViewById(R.id.inputCity);
        save = findViewById(R.id.btn_save);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        profileImageView.setOnClickListener(new View.OnClickListener() {
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
                SaveData();
            }
        });
    }

    private void SaveData() {
        String name = inputName.getText().toString();
        String city = inputCity.getText().toString();

        if(name.isEmpty()) showError(inputName,"Name is not valid");
        else if(city.isEmpty()) showError(inputCity,"City is not valid");
        else {
            progressDialog.setTitle("Adding Setup Profile");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            storageReference.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {
                        storageReference.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashMap = new HashMap();
                                hashMap.put("name",name);
                                hashMap.put("city",city);
                                hashMap.put("profileImage",uri.toString());
                                hashMap.put("status","offline");

                                mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        progressDialog.dismiss();
                                        Toast.makeText(SetupProfileActivity.this,"Setup profile success",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(SetupProfileActivity.this,"Setup profile fail",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    private void showError(EditText editText, String s) {
        editText.setError(s);
        editText.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestcode, int resultcode, @NonNull Intent data) {
        super.onActivityResult(requestcode,resultcode,data);
        if(requestcode==REQUEST_CODE && resultcode==RESULT_OK && data!=null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }
}