package com.example.myapplication.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.myapplication.R;
import com.example.myapplication.PostInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class WritePostActivity extends BasicActivity {
    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    private RelativeLayout loaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        findViewById(R.id.button_send).setOnClickListener(onClickListener);

        loaderLayout = findViewById(R.id.loaderLayout);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_send:  // 등록 버튼 클릭했을때 동작
                    post_add();
                    break;
            }
        }
    };

    private void post_add() {
        final String title = ((EditText) findViewById(R.id.editText_title)).getText().toString();            // 제목
        final String item_name = ((EditText) findViewById(R.id.editText_item_name)).getText().toString();    // 물품명
        final String price = ((EditText) findViewById(R.id.editText_price)).getText().toString();            // 가격
        final String term = ((EditText) findViewById(R.id.editText_term)).getText().toString();              // 기간
        final String contents = ((EditText) findViewById(R.id.editText_contents)).getText().toString();      // 내용

        if (title.length() > 0 && item_name.length() > 0 && price.length() > 0 && term.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            user = FirebaseAuth.getInstance().getCurrentUser();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String id = getIntent().getStringExtra("id");
            DocumentReference dr;
            if(id == null) {
                dr = db.collection("posts").document();
            }
            else {
                dr = db.collection("posts").document(id);
            }
            final DocumentReference documentReference = dr;

            // Firebase db에 정보들을 저장하기 위해 값들을 받아옴
            PostInfo postInfo = new PostInfo(title, item_name, price, term, contents, user.getUid(), new Date());
            if (contents.length() == 0) {
                postInfo.setContents("내용없음");
            }
            // db 저장 함수
            uploader(documentReference, postInfo);
        } else {
            startToast("정보를 입력해 주세요");
        }
    }

    private void uploader(DocumentReference documentReference, PostInfo postInfo) {
        documentReference.set(postInfo)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loaderLayout.setVisibility(View.GONE);
                    startToast("게시글 등록에 성공하셨습니다");
                    // 게시판 새로 갱신
                    onBackPressed();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loaderLayout.setVisibility(View.GONE);
                    startToast("게시글 등록에 실패하셨습니다");
                }
            });
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
