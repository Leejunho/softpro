package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import com.example.myapplication.MemberInfo;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;

import static com.example.myapplication.Util.showToast;


public class AddinfoActivity extends BasicActivity {
    private static final String TAG = "addInfoActivity";
    private FirebaseUser user;
    private RelativeLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addinfo);

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
                    myStartActivity2(CameraActivity.class);
                    break;
            }
        }
    };

    private void profile_add() {
        final String nickname = ((EditText)findViewById(R.id.editText_nickname)).getText().toString();
        final String address = ((EditText)findViewById(R.id.editText_address)).getText().toString();
        final String telephone = ((EditText)findViewById(R.id.editText_telephone)).getText().toString();

        if(nickname.length() > 0 && address.length() > 0 && telephone.length() > 9) {
            loaderLayout.setVisibility(View.VISIBLE);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();

            MemberInfo memberInfo = new MemberInfo(nickname, address, telephone, 0, user.getUid(), "...", FirebaseInstanceId.getInstance().getToken());
            uploader(memberInfo);
        }
        else {
            showToast(AddinfoActivity.this, "회원 정보를 입력해 주세요");
        }
    }

    private void uploader(MemberInfo memberInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(AddinfoActivity.this,"회원정보 등록에 성공하였습니다");
                        loaderLayout.setVisibility(View.GONE);
                        myStartActivity(MainActivity.class);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(AddinfoActivity.this,"회원정보 등록에 실패하였습니다");
                        loaderLayout.setVisibility(View.GONE);
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        myStartActivity(LoginActivity.class);
        showToast(AddinfoActivity.this,"회원정보 입력을 취소하셨습니다");
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
