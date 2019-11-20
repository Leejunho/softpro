package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends BasicActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_delivery).setOnClickListener(onClickListener);
        findViewById(R.id.button_board).setOnClickListener(onClickListener);
        findViewById(R.id.button_note).setOnClickListener(onClickListener);
        findViewById(R.id.button_profile).setOnClickListener(onClickListener);
        TextView textView_nickname = (TextView) findViewById(R.id.textView_usernickname) ;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String name = profile.getDisplayName();
                textView_nickname.setText(name);
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.button_delivery:  // 택배현황 버튼 클릭했을때 동작
                    myStartActivity(delivery_status.class);
                    break;

                case R.id.button_board:  // 게시판 버튼 클릭했을때 동작
                    myStartActivity(notice_board.class);
                    break;

                case R.id.button_note:  // 쪽지함 버튼 클릭했을때 동작
                    myStartActivity(notice_box.class);
                    break;

                case R.id.button_profile:  // 회원정보 버튼 클릭했을때 동작
                    myStartActivity(profile.class);
                    break;
            }
        }
    };

    public void onStart() { // 사용자가 현재 로그인되어 있는지 확인
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // 로그인한 사용자 정보가 이미 입력되어 있는 상태가 아니라면 addInfo로 이동
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(user != null) {
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                startToast("반갑습니다");
                            } else {
                                myStartActivity(addinfo.class);
                                finish();
                                startToast("회원정보를 추가입력해주세요");
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        startToast("잠시 후 다시 시도해주세요");
                    }
                }
            });
        }
        else {
            myStartActivity(LoginActivity.class);
            finish();
        }
    }

    public void onBackPressed() {
        Toast.makeText(this, "앱을 종료합니다.", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
