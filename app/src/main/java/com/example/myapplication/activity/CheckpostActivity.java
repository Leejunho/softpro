package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.MemberInfo;
import com.example.myapplication.PostInfo;
import com.example.myapplication.R;
import com.example.myapplication.fragment.ChatRoomFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.myapplication.Util.showToast;

public class CheckpostActivity extends BasicActivity {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private PostInfo postInfo;
    private TextView textView_nickname;
    private TextView textView_telephone;
    private TextView textView_address;

    private TextView textView_title;
    private TextView textView_price;
    private TextView textView_createdAt;
    private TextView textView_term;
    private TextView textView_contents;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkpost);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.button_send).setOnClickListener(onClickListener);
        findViewById(R.id.button_cancel).setOnClickListener(onClickListener);
        findViewById(R.id.button_editprofile).setOnClickListener(onClickListener);
        textView_nickname = (TextView) findViewById(R.id.textView_nickname) ;
        textView_telephone = (TextView) findViewById(R.id.textView_telephone) ;
        textView_address = (TextView) findViewById(R.id.textView_address) ;

        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");

        postInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userInit();
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.button_send:  // 최종입수하기 버튼 클릭했을때 동작
                    // 준영 채팅창 넣을 곳
                    if(postInfo != null) {
                        // consumer에 입수등록한 사람의 uid를 넣음
                        DocumentReference documentReference2 = db.collection("posts").document(postInfo.getId());
                        uploader(documentReference2, new PostInfo(postInfo.getTitle(), postInfo.getPrice(), postInfo.getTerm(), postInfo.getContents(), postInfo.getPublisher(), postInfo.getCreatedAt(), postInfo.getViewCount(), user.getUid() , postInfo.getRoomID(), postInfo.getCompletepublisher(), postInfo.getCompleteconsumer(), postInfo.getComplete(), postInfo.getBoxnum()));

                    }
                    break;

                case R.id.button_cancel:  // 취소하기 버튼 클릭했을때 동작
                    finish();
                    break;

                case R.id.button_editprofile:  // 개인정보수정 버튼 클릭했을때 동작
                    myStartActivity(profileActivity.class);
                    break;
            }
        }
    };

    private void uploader(DocumentReference documentReference, final PostInfo postInfo) {
        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(CheckpostActivity.this,"거래를 신청하셨습니다");
                        updateviewcount();
                        myStartActivity(SelectUserActivity.class, postInfo);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast(CheckpostActivity.this,"거래 신청에 실패하셨습니다");
                    }
                });
    }

    private void updateviewcount() {
        final DocumentReference documentReference = db.collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            // 게시판 카운트 개수 업데이트
                            MemberInfo memberInfo = new MemberInfo(document.getData().get("nickname").toString(), document.getData().get("address").toString(), document.getData().get("telephone").toString(), Integer.valueOf(document.getData().get("point").toString()), user.getUid(), document.getData().get("usermsg").toString(), document.getData().get("token").toString(), document.getData().get("replacenum").toString(), Integer.valueOf(document.getData().get("countpost").toString()), Integer.valueOf(document.getData().get("countbox").toString()), Integer.valueOf(document.getData().get("countmsg").toString()) + 1);
                            uploader_memberInfo(documentReference, memberInfo);
                        }
                    }
                }
            }
        });
    }

    private void uploader_memberInfo(DocumentReference documentReference, final MemberInfo memberInfo) {
        documentReference.set(memberInfo.getMemberInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void postInit() {
        if(postInfo != null) {
            textView_title = findViewById(R.id.textView_title);
            textView_title.setText(postInfo.getTitle());

            textView_price = findViewById(R.id.textView_price);
            textView_price.setText(String.valueOf(postInfo.getPrice()));

            textView_createdAt = findViewById(R.id.textView_createdAt);
            textView_createdAt.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 작성", Locale.getDefault()).format(postInfo.getCreatedAt()));

            textView_term = findViewById(R.id.textView_term);
            textView_term.setText(postInfo.getTitle());

            textView_contents = findViewById(R.id.textView_contents);
            textView_contents.setText(postInfo.getTitle());
        }
    }

    private void userInit() {
        if(user != null) {
            DocumentReference documentReference = db.collection("users").document(user.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                textView_nickname.setText(document.getData().get("nickname").toString());
                                textView_telephone.setText(document.getData().get("telephone").toString());
                                textView_address.setText(document.getData().get("address").toString());
                            }
                        }
                    }
                }
            });
        }
        else {
            showToast(CheckpostActivity.this, "잘못된 접근입니다.");
            finish();
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", postInfo);
        startActivityForResult(intent, 0);
    }
}
