package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.MemberInfo;
import com.example.myapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class profile extends BasicActivity {
    private static final String TAG = "profileActivity";
    private FirebaseAuth mAuth;
    private ImageView profileImageView;
    private String profilePath;
    private FirebaseUser user;
    private RelativeLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        profileImageView = findViewById(R.id.imageView_profile);
        profileImageView.setOnClickListener(onClickListener);

        findViewById(R.id.button_edit).setOnClickListener(onClickListener);
        findViewById(R.id.button_logout).setOnClickListener(onClickListener);
        findViewById(R.id.button_picture).setOnClickListener(onClickListener);
        findViewById(R.id.button_gallery).setOnClickListener(onClickListener);

        loaderLayout = findViewById(R.id.loaderLayout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if(resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra("profilePath");
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageView);
                }
                break;
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.button_edit:  // 입력 버튼 클릭했을때 동작
                    profile_add();
                    break;

                case R.id.button_logout:  // 로그아웃 버튼 클릭했을때 동작
                    FirebaseAuth.getInstance().signOut();
                    startToast("로그아웃 하였습니다");
                    myStartActivity(LoginActivity.class);
                    finish();
                    break;

                case R.id.imageView_profile:  // 이미지를 클릭했을때 동작
                    CardView cardView = findViewById(R.id.cardView_button);
                    if(cardView.getVisibility() == View.VISIBLE) {
                        cardView.setVisibility(View.GONE);
                    }
                    else {
                        cardView.setVisibility(View.VISIBLE);
                    }
                    break;

                case R.id.button_picture:  // 사진촬영 버튼 클릭했을때 동작
                    myStartActivity2(CameraActivity.class);
                    break;

                case R.id.button_gallery:  // 갤러리 버튼 클릭했을때 동작
                    if (ContextCompat.checkSelfPermission(profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        if (ActivityCompat.shouldShowRequestPermissionRationale(profile.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            } else {
                            startToast("권한을 허용해 주세요");
                        }
                    } else {
                        // 권한이 있을 때
                        myStartActivity2(GalleryActivity.class);
                    }
                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myStartActivity2(GalleryActivity.class);
                } else {
                    startToast("권한을 허용해 주세요");
                }
            }
        }
    }

    private void profile_add() {
        final String nickname = ((EditText)findViewById(R.id.editText_nickname)).getText().toString();
        final String address = ((EditText)findViewById(R.id.editText_address)).getText().toString();
        final String telephone = ((EditText)findViewById(R.id.editText_telephone)).getText().toString();

        if(nickname.length() > 0 && address.length() > 0 && telephone.length() > 9) {
            loaderLayout.setVisibility(View.VISIBLE);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();
            StorageReference storageRef = storage.getReference();
            final StorageReference mountainImagesRef = storageRef.child("users/"+user.getUid()+"/profileImage.jpg");

            // 사진을 입력하지 않았을 경우 사진을 제외하고 db에 정보가 들어감
            if(profilePath == null) {
                MemberInfo memberInfo = new MemberInfo(nickname, address, telephone);
                uploader(memberInfo);
                loaderLayout.setVisibility(View.GONE);
                startToast( "회원 정보 변경에 성공하셨습니다");
            }
            else {
                try{
                    InputStream stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                loaderLayout.setVisibility(View.GONE);
                                throw task.getException();
                            }
                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                MemberInfo memberInfo = new MemberInfo(nickname, address, telephone, downloadUri.toString());
                                uploader(memberInfo);
                                loaderLayout.setVisibility(View.GONE);
                                startToast( "회원 정보 변경에 성공하셨습니다");
                            } else {
                                loaderLayout.setVisibility(View.GONE);
                                startToast( "회원 정보 전송에 실패하였습니다");
                            }
                        }
                    });
                }catch (FileNotFoundException e){
                    loaderLayout.setVisibility(View.GONE);
                    Log.e("로그", "에러" + e.toString());
                    startToast( "회원 정보 전송에 실패하였습니다");
                }
            }
        }
        else {
            startToast( "회원 정보를 입력해 주세요");
        }
    }

    private void uploader(MemberInfo memberInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("회원정보 등록을 성공하였습니다");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("회원정보 등록에 실패하였습니다");
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void myStartActivity2(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}
