package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

import static com.example.myapplication.Util.showToast;

public class WritePostActivity extends BasicActivity {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private RelativeLayout loaderLayout;
    private PostInfo postInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        findViewById(R.id.button_send).setOnClickListener(onClickListener);

        loaderLayout = findViewById(R.id.loaderLayout);

        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");
        postInit();
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
            db = FirebaseFirestore.getInstance();

            final DocumentReference documentReference = postInfo == null ? db.collection("posts").document() : db.collection("posts").document(postInfo.getId());
            //final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();

            // Firebase db에 정보들을 저장하기 위해 값들을 받아옴
            if (contents.length() == 0) {
                postInfo.setContents("내용없음");
            }
            // db 저장 함수
            uploader(documentReference, new PostInfo(title, item_name, price, term, contents, user.getUid(), new Date()));
        } else {
            showToast(WritePostActivity.this, "정보를 입력해 주세요");
        }
    }

    private void uploader(DocumentReference documentReference, final PostInfo postInfo) {
        documentReference.set(postInfo.getPostInfo())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loaderLayout.setVisibility(View.GONE);
                    showToast(WritePostActivity.this,"게시글 등록에 성공하셨습니다");
                    // 게시판 새로 갱신
                    Intent intent = new Intent();
                    intent.putExtra("postInfo", postInfo);
                    setResult(0, intent);
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loaderLayout.setVisibility(View.GONE);
                    showToast(WritePostActivity.this,"게시글 등록에 실패하셨습니다");
                }
            });
    }

    private void postInit() {
        if(postInfo != null) {
            ((EditText) findViewById(R.id.editText_title)).setText(postInfo.getTitle());
            ((EditText) findViewById(R.id.editText_item_name)).setText(postInfo.getItem_name());
            ((EditText) findViewById(R.id.editText_price)).setText(postInfo.getPrice());
            ((EditText) findViewById(R.id.editText_term)).setText(postInfo.getTerm());
            ((EditText) findViewById(R.id.editText_contents)).setText(postInfo.getContents());
        }
    }
}
