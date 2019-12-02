package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.myapplication.util.util_util.showToast;

public class activity_login_signUp extends activity_main_basic {
    private FirebaseAuth mAuth;
    private RelativeLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.button_signup).setOnClickListener(onClickListener);

        loaderLayout = findViewById(R.id.loaderLayout);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_signup:  // 회원가입 버튼 클릭했을때 동작
                    singUp();
                    break;
            }
        }
    };

    private void singUp() {
        String email = ((EditText) findViewById(R.id.editText_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.editText_password)).getText().toString();
        String passwordcheck = ((EditText) findViewById(R.id.editText_passwordcheck)).getText().toString();

        if (email.length() > 0 && password.length() > 0 && passwordcheck.length() > 0) {
            if (password.equals(passwordcheck)) {
                loaderLayout.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    loaderLayout.setVisibility(View.GONE);
                                    showToast(activity_login_signUp.this,"회원가입에 성공하였습니다.");
                                    myStartActivity(activity_login_addInfo.class);
                                    finish();
                                } else {
                                    loaderLayout.setVisibility(View.GONE);
                                    showToast(activity_login_signUp.this,"회원가입에 실패하셨습니다.");
                                    if (task.getException() != null) {
                                        showToast(activity_login_signUp.this, task.getException().toString());
                                    }
                                }
                            }
                        });
            } else {
                showToast(activity_login_signUp.this,"비밀번호가 일치하지 않습니다.");
            }
        } else {
            showToast(activity_login_signUp.this,"이메일 또는 비밀번호를 입력해 주세요");
        }
    }
    
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}