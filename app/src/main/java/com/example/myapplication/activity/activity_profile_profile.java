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

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.info.memberInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.myapplication.util.util_util.showToast;

public class activity_profile_profile extends activity_main_basic {
    private static final String TAG = "activity_profile_profile";
    private FirebaseAuth mAuth;
    private ImageView profileImageView;
    private String profilePath;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private RelativeLayout loaderLayout;
    private String photoUrl = null;
    private int point = 0;
    private String replacenum;
    private int countpost;
    private int countmsg;
    private int countbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        profileImageView = findViewById(R.id.imageView_profile);
        profileImageView.setOnClickListener(onClickListener);

        findViewById(R.id.button_edit).setOnClickListener(onClickListener);
        findViewById(R.id.button_logout).setOnClickListener(onClickListener);
        findViewById(R.id.button_picture).setOnClickListener(onClickListener);
        findViewById(R.id.button_gallery).setOnClickListener(onClickListener);

        loaderLayout = findViewById(R.id.loaderLayout);

        showuserData();
    }

    private void showuserData() {
        if(user != null) {
            DocumentReference documentReference = db.collection("users").document(user.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                if(document.getData().get("photoUrl") != null) {
                                    Glide.with(activity_profile_profile.this).load(document.getData().get("photoUrl")).centerCrop().override(500).into(profileImageView);
                                    photoUrl = document.getData().get("photoUrl").toString();
                                }
                                ((EditText) findViewById(R.id.editText_nickname)).setText(document.getData().get("nickname").toString());
                                ((EditText) findViewById(R.id.editText_telephone)).setText(document.getData().get("telephone").toString());
                                ((EditText) findViewById(R.id.editText_address)).setText(document.getData().get("address").toString());

                                if(document.getData().get("point") != null) {
                                    point = ((Long)document.getData().get("point")).intValue();
                                }
                            }
                        }
                    } else {
                        showToast(activity_profile_profile.this,"잠시 후 다시 시도해주세요");
                    }
                }
            });
        }
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
                    showToast(activity_profile_profile.this,"로그아웃 하였습니다");
                    myStartActivity(activity_login_login.class);
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
                    myStartActivity2(activity_profile_camera.class);
                    break;

                case R.id.button_gallery:  // 갤러리 버튼 클릭했을때 동작
                    if (ContextCompat.checkSelfPermission(activity_profile_profile.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity_profile_profile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity_profile_profile.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        } else {
                            showToast(activity_profile_profile.this,"권한을 허용해 주세요");
                        }
                    } else {
                        // 권한이 있을 때
                        myStartActivity2(activity_profile_gallery.class);
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
                    myStartActivity2(activity_profile_gallery.class);
                } else {
                    showToast(activity_profile_profile.this,"권한을 허용해 주세요");
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

            // 사진을 입력하지 않았을 경우 기존 저장되있던 사진이 올라감 없었으면 null 있으면 pictureUrl
            if(profilePath == null) {
                DocumentReference documentReference = db.collection("users").document(user.getUid());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    replacenum = document.getData().get("replacenum").toString();
                                    countpost = Integer.valueOf(document.getData().get("countpost").toString());
                                    countmsg = Integer.valueOf(document.getData().get("countmsg").toString());
                                    countbox = Integer.valueOf(document.getData().get("countbox").toString());

                                    memberInfo memberInfo = new memberInfo(nickname, address, telephone, photoUrl, point, user.getUid(), "...", FirebaseInstanceId.getInstance().getToken(), replacenum, countpost, countmsg, countbox);
                                    uploader(memberInfo);
                                    loaderLayout.setVisibility(View.GONE);
                                    showToast(activity_profile_profile.this, "회원 정보 변경에 성공하셨습니다");
                                }
                            }
                        }
                    }
                });
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
                                final Uri downloadUri = task.getResult();

                                DocumentReference documentReference = db.collection("users").document(user.getUid());
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document != null) {
                                                if (document.exists()) {
                                                    replacenum = document.getData().get("replacenum").toString();
                                                    countpost = Integer.valueOf(document.getData().get("countpost").toString());
                                                    countmsg = Integer.valueOf(document.getData().get("countmsg").toString());
                                                    countbox = Integer.valueOf(document.getData().get("countbox").toString());

                                                    memberInfo memberInfo = new memberInfo(nickname, address, telephone, downloadUri.toString(), point, user.getUid(), "...", FirebaseInstanceId.getInstance().getToken(), replacenum, countpost, countmsg, countbox);
                                                    uploader(memberInfo);
                                                    loaderLayout.setVisibility(View.GONE);
                                                    showToast(activity_profile_profile.this, "회원 정보 변경에 성공하셨습니다");
                                                }
                                            }
                                        }
                                    }
                                });
                            } else {
                                loaderLayout.setVisibility(View.GONE);
                                showToast(activity_profile_profile.this, "회원 정보 전송에 실패하였습니다");
                            }
                        }
                    });
                }catch (FileNotFoundException e){
                    loaderLayout.setVisibility(View.GONE);
                    Log.e("로그", "에러" + e.toString());
                    showToast(activity_profile_profile.this, "회원 정보 전송에 실패하였습니다");
                }
            }
        }
        else {
            showToast(activity_profile_profile.this, "회원 정보를 입력해 주세요");
        }
    }

    private void uploader(memberInfo memberInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(activity_profile_profile.this,"회원정보 등록을 성공하였습니다");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(activity_profile_profile.this,"회원정보 등록에 실패하였습니다");
                        Log.w("1", "Error writing document", e);
                    }
                });
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
