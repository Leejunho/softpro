package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.myapplication.R;
import com.example.myapplication.info.memberInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Random;

import static com.example.myapplication.util.util_util.showToast;


public class activity_login_addInfo extends activity_main_basic {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private RelativeLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_addinfo);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.button_input).setOnClickListener(onClickListener);

        loaderLayout = findViewById(R.id.loaderLayout);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.button_input:  // 입력 버튼 클릭했을때 동작
                    profile_add();
                    break;

                case R.id.imageView_profile:  // 이미지를 클릭했을때 동작
                    myStartActivity2(activity_profile_camera.class);
                    break;
            }
        }
    };

    private void profile_add() {
        //  추가 정보를 입력
        final String nickname = ((EditText)findViewById(R.id.editText_nickname)).getText().toString();
        final String address = ((EditText)findViewById(R.id.editText_address)).getText().toString();
        final String telephone = ((EditText)findViewById(R.id.editText_telephone)).getText().toString();

        if(nickname.length() > 0 && address.length() > 0 && telephone.length() > 9) {
            loaderLayout.setVisibility(View.VISIBLE);

            Random rnd = new Random();
            int num = rnd.nextInt(2100000000);
            final String replacenum = "#" + num;    // 대체키 번호


            uploader(new memberInfo(nickname, address, telephone, 0, user.getUid(), "...", FirebaseInstanceId.getInstance().getToken(), replacenum, 0, 0, 0));
        }
        else {
            showToast(activity_login_addInfo.this, "회원 정보를 입력해 주세요");
        }
    }

    private void uploader(final memberInfo memberInfo) {
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(activity_login_addInfo.this,"회원정보 등록에 성공하였습니다");
                        loaderLayout.setVisibility(View.GONE);
                        myStartActivity(activity_main_main.class);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(activity_login_addInfo.this,"회원정보 등록에 실패하였습니다");
                        loaderLayout.setVisibility(View.GONE);
                    }
                });
    }

    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        myStartActivity(activity_login_login.class);
        showToast(activity_login_addInfo.this,"회원정보 입력을 취소하셨습니다");
        finish();
        super.onBackPressed();
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
