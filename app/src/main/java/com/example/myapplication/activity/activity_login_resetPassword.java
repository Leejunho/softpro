package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.myapplication.util.util_util.showToast;

public class activity_login_resetPassword extends activity_main_basic {
    private FirebaseAuth mAuth;
    private RelativeLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_resetpassword);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.button_sendemail).setOnClickListener(onClickListener);

        loaderLayout = findViewById(R.id.loaderLayout);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.button_sendemail:  // 이메일 보내기 버튼 클릭했을때 동작
                    send();
                    break;
            }
        }
    };

    private void send() {
        String email = ((EditText)findViewById(R.id.editText_email)).getText().toString();
        loaderLayout.setVisibility(View.VISIBLE);
        if(email.length() > 0) {
            // 비밀번호 재성정 이메일 보내기
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loaderLayout.setVisibility(View.GONE);
                                showToast(activity_login_resetPassword.this, "이메일을 보냈습니다");
                            }
                        }
                    });
        }
        else {
            loaderLayout.setVisibility(View.GONE);
            showToast(activity_login_resetPassword.this, "이메일을 입력해 주세요");
        }
    }
}

