package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.MemberInfo;
import com.example.myapplication.R;
import com.example.myapplication.PostInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

import static com.example.myapplication.Util.showToast;

public class WritePostActivity extends BasicActivity {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private RelativeLayout loaderLayout;
    private PostInfo postInfo;

    private TextView textView_nickname;
    private TextView textView_telephone;
    private TextView textView_address;
    private TextView textView_boxnum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.button_send).setOnClickListener(onClickListener);
        findViewById(R.id.button_cancel).setOnClickListener(onClickListener);
        findViewById(R.id.button_editprofile).setOnClickListener(onClickListener);

        textView_nickname = (TextView) findViewById(R.id.textView_nickname) ;
        textView_telephone = (TextView) findViewById(R.id.textView_telephone) ;
        textView_address = (TextView) findViewById(R.id.textView_address) ;
        textView_boxnum = (TextView) findViewById(R.id.textView_boxnum) ;

        loaderLayout = findViewById(R.id.loaderLayout);

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
            switch (v.getId()) {
                case R.id.button_send:  // 입수등록 버튼 클릭했을때 동작
                    post_add();
                    break;

                case R.id.button_cancel:  // 등록취소 버튼 클릭했을때 동작
                    finish();
                    break;

                case R.id.button_editprofile:  // 개인정보수정 버튼 클릭했을때 동작
                    myStartActivity(profileActivity.class);
                    break;
            }
        }
    };

    private void post_add() {
        final String title = ((EditText) findViewById(R.id.textView_title)).getText().toString();            // 물품제목
        String p = ((EditText) findViewById(R.id.textView_price)).getText().toString();
        if(p.length() < 0) {
            showToast(WritePostActivity.this, "가격 정보를 입력해 주세요");
            return;
        }
        String p2 = ((EditText) findViewById(R.id.textView_boxnum)).getText().toString();
        if(p2.length() < 0) {
            showToast(WritePostActivity.this, "택배함 번호를 입력해 주세요");
            return;
        }
        final int price = Integer.parseInt(p);                                                               // 가격
        final int boxnum = Integer.parseInt(p2);                                                               // 택배함번호
        final String term = ((EditText) findViewById(R.id.textView_term)).getText().toString();              // 기간
        final String contents = ((EditText) findViewById(R.id.textView_contents)).getText().toString();      // 내용

        DocumentReference documentReference = db.collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            DocumentReference documentReference2 = db.collection("delivery").document(String.valueOf(boxnum));
                            documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document2 = task.getResult();
                                        if (document2 != null) {
                                            if (document2.exists()) {
                                                // 유저가 작성한 택배함 번호에 해당하는 정보를 가지고 잇는지 확인
                                                //               택배함의 전화번호                                  현재 유저 전화번호
                                                if(document2.getData().get("telephone").toString().equals(document.getData().get("telephone").toString())) {
                                                    if (title.length() > 0 && price >= 0 && term.length() > 0) {
                                                        loaderLayout.setVisibility(View.VISIBLE);
                                                        user = FirebaseAuth.getInstance().getCurrentUser();
                                                        db = FirebaseFirestore.getInstance();

                                                        final DocumentReference documentReference = postInfo == null ? db.collection("posts").document() : db.collection("posts").document(postInfo.getId());
                                                        //final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();

                                                        // Firebase db에 정보들을 저장하기 위해 값들을 받아옴
                                                        if (contents.length() == 0) {
                                                            postInfo.setContents("내용없음");
                                                        }

                                                        int viewCount = 0;
                                                        String consumerblank = "";
                                                        String roomidblank = "";
                                                        String completePublisher = "NO";
                                                        String completeComsumer = "NO";
                                                        String complete = "NO";
                                                        if (postInfo != null) {
                                                            viewCount = postInfo.getViewCount();
                                                            consumerblank = postInfo.getConsumer();
                                                            roomidblank = postInfo.getRoomID();
                                                            completePublisher = postInfo.getCompletepublisher();
                                                            completeComsumer = postInfo.getCompleteconsumer();
                                                            complete = postInfo.getComplete();
                                                        }

                                                        // db 저장 함수

                                                        uploader(documentReference, new PostInfo(title, price, term, contents, user.getUid(), new Date(), viewCount, consumerblank, roomidblank, completePublisher, completeComsumer, complete, boxnum));
                                                    }
                                                    else {
                                                        showToast(WritePostActivity.this, "정보를 입력해 주세요");
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

    }

    private void uploader(DocumentReference documentReference, final PostInfo postInfo) {
        documentReference.set(postInfo.getPostInfo())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loaderLayout.setVisibility(View.GONE);
                    showToast(WritePostActivity.this,"게시글 등록에 성공하셨습니다");
                    // view count 새로 갱신
                    updateviewcount();

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
                            MemberInfo memberInfo = new MemberInfo(document.getData().get("nickname").toString(), document.getData().get("address").toString(), document.getData().get("telephone").toString(), Integer.valueOf(document.getData().get("point").toString()), user.getUid(), document.getData().get("usermsg").toString(), document.getData().get("token").toString(), document.getData().get("replacenum").toString(), Integer.valueOf(document.getData().get("countpost").toString()) + 1, Integer.valueOf(document.getData().get("countbox").toString()), Integer.valueOf(document.getData().get("countmsg").toString()));
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
            ((EditText) findViewById(R.id.textView_title)).setText(postInfo.getTitle());
            ((EditText) findViewById(R.id.textView_price)).setText(String.valueOf(postInfo.getPrice()));
            ((EditText) findViewById(R.id.textView_term)).setText(postInfo.getTerm());
            ((EditText) findViewById(R.id.textView_contents)).setText(postInfo.getContents());
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
                                //String formattingNumber = PhoneNumberUtils.formatNumber("010123454567");
                                textView_address.setText(document.getData().get("address").toString());
                            }
                        }
                    }
                }
            });
        }
        else {
            showToast(WritePostActivity.this, "잘못된 접근입니다.");
            finish();
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
