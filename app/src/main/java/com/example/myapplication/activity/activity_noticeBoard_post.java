package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.info.deliveryInfo;
import com.example.myapplication.info.postInfo;
import com.example.myapplication.info.memberInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.myapplication.util.util_util.showToast;

public class activity_noticeBoard_post extends activity_main_basic {
    private postInfo postInfo;
    private com.example.myapplication.info.memberInfo memberInfo;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private TextView textView_title;
    private TextView textView_price;
    private TextView textView_term;
    private TextView textView_contents;
    private TextView textView_createdAt;
    private TextView textView_nickname;
    private TextView textView_point;
    private TextView textView_currentprogress;
    private TextView textView_ipoint;
    private TextView textView_inickname;
    private TextView textView_iboxnum;
    private TextView textView_viewCount;

    private String title;
    private int price;
    private String term;
    private String contents;
    private String publisher;
    private Date createdAt;
    private String id;
    private int viewCount;
    private String consumer;
    private String roomID;
    private String completepublisher;
    private String completeconsumer;
    private String complete;
    private int boxnum;
    private String checkpublisher;
    private String checkconsumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeboard_post);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // 게시글을 클릭하면 postInfo 데이터를 PostActivity로 넘겨받음.
        postInfo = (com.example.myapplication.info.postInfo)getIntent().getSerializableExtra("postInfo");

        // 입수자 정보 보이지 않게 만듬
        findViewById(R.id.LinearLayout_i3).setVisibility(View.GONE);
        findViewById(R.id.textView_i1).setVisibility(View.GONE);
        findViewById(R.id.LinearLayout_i2).setVisibility(View.GONE);
        findViewById(R.id.LinearLayout_i4).setVisibility(View.GONE);
        findViewById(R.id.view_i2).setVisibility(View.GONE);
        findViewById(R.id.view_i3).setVisibility(View.GONE);
        findViewById(R.id.view_i4).setVisibility(View.GONE);
        findViewById(R.id.view_i).setVisibility(View.GONE);


        findViewById(R.id.menu).setOnClickListener(onClickListener);
        findViewById(R.id.button_send).setOnClickListener(onClickListener);
        findViewById(R.id.button_complete).setOnClickListener(onClickListener);
        findViewById(R.id.button_cancel).setOnClickListener(onClickListener);
        findViewById(R.id.button_recommend).setOnClickListener(onClickListener);

        textView_title = findViewById(R.id.textView_title);
        textView_price = findViewById(R.id.textView_price);
        textView_term = findViewById(R.id.textView_term);
        textView_contents = findViewById(R.id.textView_contents);
        textView_createdAt = findViewById(R.id.textView_createdAt);
        textView_nickname = findViewById(R.id.textView_nickname);
        textView_point = findViewById(R.id.textView_point);
        textView_currentprogress = findViewById(R.id.textView_currentprogress);
        textView_ipoint = findViewById(R.id.textView_ipoint);
        textView_inickname = findViewById(R.id.textView_inickname);
        textView_iboxnum = findViewById(R.id.textView_iboxnum);
        textView_viewCount = findViewById(R.id.textView_viewCount);

        setTextinvalues();

        if(!checktradefinish()) {
            // 거래가 완료된 상태이면 게시글 작성자도 수정 삭제 불가능
            checkauthority();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 입수신청 거래완료 거래취소 추천하기 버튼 보이지 않게 만듬
        findViewById(R.id.button_send).setVisibility(View.GONE);
        findViewById(R.id.button_complete).setVisibility(View.GONE);
        findViewById(R.id.button_cancel).setVisibility(View.GONE);
        findViewById(R.id.button_recommend).setVisibility(View.GONE);
        findViewById(R.id.menu).setVisibility(View.GONE);

        postInfo = (com.example.myapplication.info.postInfo)getIntent().getSerializableExtra("postInfo");

        if(!checktradefinish()) {
            checkauthority();
        }
    }


    private void setTextinvalues() {
        /* 게시글 정보*/
        // 게시글 물품제목
        textView_title.setText(postInfo.getTitle());
        // 게시글 제안금액
        textView_price.setText(String.valueOf(postInfo.getPrice()));
        // 게시글 기간
        textView_term.setText(postInfo.getTerm());
        // 게시글 내용
        textView_contents.setText(postInfo.getContents());
        // 게시글 올린 날짜
        textView_createdAt.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 작성", Locale.getDefault()).format(postInfo.getCreatedAt()));
        // 게시글 조회수
        textView_viewCount.setText("조회수 " + String.valueOf(postInfo.getViewCount()));
        /*신청자 정보*/
        DocumentReference documentReference = db.collection("users").document(postInfo.getPublisher());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            if(document.getData().get("point") != null) {
                                // 닉네임
                                textView_nickname.setText(document.getData().get("nickname").toString());
                                // 거래점수
                                textView_point.setText(document.getData().get("point").toString() +"점");
                            }
                        }
                    }
                }
            }
        });
    }

    public void checkauthority() {
        /*
        if(user.getUid().equals("1R5r4L6O1PeA4h93RrCNYK4zQzS2") || user.getUid().equals("4cuaqNgeOCaun6dxrakLdRTkDpj1") || user.getUid().equals("T8oDzbNpoUYKd77AycscZU3bXry1"))  {
            // 관리자 GM
            // 모든 글 수정 삭제 가능
            findViewById(R.id.menu).setVisibility(View.VISIBLE);

            // 입수신청 버튼이 사용가능
            findViewById(R.id.button_send).setVisibility(View.VISIBLE);
        }
        */

        // 게시글 작성자는 메뉴버튼 활성
        if(user.getUid().equals(postInfo.getPublisher())) {
            findViewById(R.id.menu).setVisibility(View.VISIBLE);
        }

        if(!postInfo.getConsumer().equals("")) {
            // 거래중
            textView_currentprogress.setText("거래가 진행중인 물품입니다.");

            if(postInfo.getConsumer().equals(user.getUid()) || postInfo.getPublisher().equals(user.getUid())) {
                findViewById(R.id.button_complete).setVisibility(View.VISIBLE);
                findViewById(R.id.button_cancel).setVisibility(View.VISIBLE);

                showcosumerInfo();
            }
        }
        else {
            // 거래 대기중
            if(!postInfo.getPublisher().equals(user.getUid())) {
                findViewById(R.id.button_send).setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean checktradefinish() {
        if(postInfo.getComplete().equals("YES")) {
            // 거래 완료
            textView_currentprogress.setText("거래가 완료된 물품입니다.");

            if(postInfo.getPublisher().equals(user.getUid())) {
                // 글 작성자
                showcosumerInfo();
                if(postInfo.getCompletepublisher().equals("YES")) {
                    // 투표권이 있는 경우에만 추천하기 버튼을 보여줌
                    if(postInfo.getCheckpublisher().equals("NO")) {
                        final DocumentReference documentReference_check = db.collection("posts").document(postInfo.getId());
                        documentReference_check.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        if (document.exists()) {
                                            SetPostValues(document);
                                            uploader_postInfo(documentReference_check, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, completepublisher, completeconsumer, complete, boxnum, "YES", checkconsumer));
                                        }
                                    }
                                }
                            }
                        });
                    }
                    findViewById(R.id.button_recommend).setVisibility(View.VISIBLE);
                }
            }
            else if(postInfo.getConsumer().equals(user.getUid())) {
                // 입수자
                showcosumerInfo();
                if(postInfo.getCompleteconsumer().equals("YES")) {
                    // 투표권이 있는 경우에만 추천하기 버튼을 보여줌
                    if(postInfo.getCheckconsumer().equals("NO")) {
                        final DocumentReference documentReference_check = db.collection("posts").document(postInfo.getId());
                        documentReference_check.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        if (document.exists()) {
                                            SetPostValues(document);
                                            uploader_postInfo(documentReference_check, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, completepublisher, completeconsumer, complete, boxnum, checkpublisher, "YES"));
                                        }
                                    }
                                }
                            }
                        });
                    }
                    findViewById(R.id.button_recommend).setVisibility(View.VISIBLE);
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    private void showcosumerInfo() {
        findViewById(R.id.textView_i1).setVisibility(View.VISIBLE);
        findViewById(R.id.LinearLayout_i3).setVisibility(View.VISIBLE);
        findViewById(R.id.LinearLayout_i2).setVisibility(View.VISIBLE);
        findViewById(R.id.LinearLayout_i4).setVisibility(View.VISIBLE);
        findViewById(R.id.view_i2).setVisibility(View.VISIBLE);
        findViewById(R.id.view_i3).setVisibility(View.VISIBLE);
        findViewById(R.id.view_i4).setVisibility(View.VISIBLE);
        findViewById(R.id.view_i).setVisibility(View.VISIBLE);

        DocumentReference documentReference = db.collection("users").document(postInfo.getConsumer());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            // 닉네임
                            textView_inickname.setText(document.getData().get("nickname").toString());
                            // 거래점수
                            textView_ipoint.setText(document.getData().get("point").toString() + "점");
                        }
                    }
                }
            }
        });
        // 택배함 번호
        textView_iboxnum.setText(String.valueOf(postInfo.getBoxnum()));
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu:  // 점3개 메뉴 버튼 클릭했을때 동작
                    showPopup(v);
                    break;

                case R.id.button_send:  // 입수신청 메뉴 버튼 클릭했을때 동작
                    myStartActivity(activity_noticeBoard_postCheck.class, postInfo);
                    break;

                case R.id.button_complete:  // 거래완료 버튼 클릭했을때 동작
                    final DocumentReference documentReference = db.collection("posts").document(postInfo.getId());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    if (document.exists()) {
                                        SetPostValues(document);

                                        if(complete.equals("YES")) {
                                            // 거래완료 된 상품이라면 추천하기 페이지로 갱신
                                            showToast(activity_noticeBoard_post.this, "거래를 완료하였습니다. 추천 기능을 이용해주세요");
                                            myStartActivity(activity_noticeBoard_post.class, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, completepublisher, completeconsumer, complete, boxnum, checkpublisher, checkconsumer));
                                            finish();
                                        }


                                        // db 저장 함수
                                        if(user.getUid().equals(publisher)) {
                                            // 자신이 게시판 작성자 인 경우
                                            if(completepublisher.equals("YES")) {
                                                // 게시판 작성자가 이미 거래완료를 클릭한 경우
                                                showToast(activity_noticeBoard_post.this, "거래완료 신청을 이미 하셨습니다.");
                                            }
                                            else {
                                                if (completeconsumer.equals("YES")) {
                                                    uploader_postInfo(documentReference, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, "YES", completeconsumer, "YES", boxnum, checkpublisher, checkconsumer));
                                                    showToast(activity_noticeBoard_post.this, "거래를 완료하였습니다. 추천 기능을 이용해주세요");
                                                    myStartActivity(activity_noticeBoard_post.class, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, "YES", completeconsumer, "YES", boxnum, checkpublisher, checkconsumer));
                                                    finish();
                                                }
                                                else {
                                                    uploader_postInfo(documentReference, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, "YES", completeconsumer, complete, boxnum, checkpublisher, checkconsumer));
                                                    showToast(activity_noticeBoard_post.this, "거래를 완료하였습니다. 상대방의 거래완료를 대기중입니다.");
                                                }
                                            }
                                        }
                                        else if(user.getUid().equals(consumer)){
                                            // 자신이 입수자인 경우
                                            if(completeconsumer.equals("YES")) {
                                                // 이미 거래완료를 클릭했을 경우
                                                showToast(activity_noticeBoard_post.this, "거래완료 신청을 이미 하셨습니다.");
                                            }
                                            else {
                                                if (completepublisher.equals("YES")) {
                                                    uploader_postInfo(documentReference, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, completepublisher, "YES", "YES", boxnum, checkpublisher, checkconsumer));
                                                    showToast(activity_noticeBoard_post.this, "거래를 완료하였습니다. 추천 기능을 이용해주세요");
                                                    myStartActivity(activity_noticeBoard_post.class, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, completepublisher, "YES", "YES", boxnum, checkpublisher, checkconsumer));
                                                    finish();
                                                }
                                                else {
                                                    uploader_postInfo(documentReference, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, completepublisher, "YES", complete, boxnum, checkpublisher, checkconsumer));
                                                    showToast(activity_noticeBoard_post.this, "거래를 완료하였습니다. 상대방의 거래완료를 대기중입니다.");
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        showToast(activity_noticeBoard_post.this, "오류가 발생했습니다. 잠시후 다시 시도해주세요.");

                                    }
                                }
                            }
                        }
                    });
                    break;

                case R.id.button_cancel:  // 거래취소 메뉴 버튼 클릭했을때 동작
                    // 채팅방
                    /*
                    // Yes로 만드는 구문
                    final DocumentReference docRef = db.collection("rooms").document(postInfo.getRoomID());
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (!task.isSuccessful()) {return;}
                            DocumentSnapshot document = task.getResult();
                            document.getReference().update("complete", "YES");
                        }
                    });
                    */

                    // 삭제 구문
                    /*
                    db.collection("rooms").document(postInfo.getRoomID())
                            .delete()
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
                    */

                    // 택배함 정보에서
                    final DocumentReference documentReference_boxnum = db.collection("delivery").document(String.valueOf(postInfo.getBoxnum()));
                    documentReference_boxnum.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document_boxnum = task.getResult();
                                if (document_boxnum != null) {
                                    if (document_boxnum.exists()) {
                                        uploader_deliveryInfo(documentReference_boxnum, new deliveryInfo(document_boxnum.getData().get("telephone").toString(), document_boxnum.getData().get("boxnum").toString(), document_boxnum.getTimestamp("createdAt").toDate(), ""));
                                    }
                                }
                            }
                        }
                    });


                    // postinfo 거래 진행중 정보 삭제
                    final DocumentReference documentReference_cancel = db.collection("posts").document(postInfo.getId());

                    documentReference_cancel.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    if (document.exists()) {
                                        SetPostValues(document);

                                        uploader_postInfo(documentReference_cancel, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, "", "", "NO", "NO", "NO", boxnum, checkpublisher, checkconsumer));
                                        showToast(activity_noticeBoard_post.this, "거래를 취소하였습니다.");
                                        finish();
                                    }
                                }
                            }
                        }
                    });
                    break;

                case R.id.button_recommend:  // 추천버튼 클릭했을때 동작

                    final DocumentReference documentReference_recoPostInfo = db.collection("posts").document(postInfo.getId());

                    documentReference_recoPostInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    if (document.exists()) {
                                        SetPostValues(document);


                                        final String useruid;
                                        // 상대에게 추천을 하기위해 상대 uid 정보를 받아옴
                                        if(publisher.equals(user.getUid())) {
                                            // 거래글을 작성한 사람이 추천버튼을 눌렀을 때
                                            useruid = consumer;
                                        }
                                        else {
                                            // 입수하여 물건을 맡아준 사람이 추천버튼을 눌렀을 때
                                            useruid = publisher;
                                        }

                                        // 유저 데이터를 찾아서 가져와 uploader_postInfo 즉 새로 update 시킴
                                        db.collection("users")
                                                .whereEqualTo("uid", useruid)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            // 추천점수 +1 점 상승
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String photoUrl = document.getData().get("photoUrl") == null ? null : document.getData().get("photoUrl").toString();
                                                                memberInfo = new memberInfo(document.getData().get("nickname").toString(), document.getData().get("address").toString(), document.getData().get("telephone").toString(), photoUrl, Integer.valueOf(document.getData().get("point").toString()) + 1, document.getData().get("uid").toString(), document.getData().get("usermsg").toString(), document.getData().get("token").toString(), document.getData().get("replacenum").toString(), Integer.valueOf(document.getData().get("countpost").toString()), Integer.valueOf(document.getData().get("countmsg").toString()), Integer.valueOf(document.getData().get("countbox").toString()));
                                                            }
                                                            DocumentReference documentReference_recommend = db.collection("users").document(useruid);
                                                            uploader_memberInfo(documentReference_recommend, memberInfo);

                                                            if(publisher.equals(user.getUid())) {
                                                                // 거래글을 작성한 사람이 추천버튼을 눌렀을 때 추천 권한 삭제
                                                                uploader_postInfo(documentReference_recoPostInfo, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, "NO", completeconsumer, complete, boxnum, checkpublisher, checkconsumer));
                                                                myStartActivity(activity_noticeBoard_post.class, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, "NO", completeconsumer, complete, boxnum, checkpublisher, checkconsumer));

                                                            }
                                                            else {
                                                                // 입수하여 물건을 맡아준 사람이 추천버튼을 눌렀을 때 추천 권한 삭제
                                                                uploader_postInfo(documentReference_recoPostInfo, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, completepublisher, "NO", complete, boxnum, checkpublisher, checkconsumer));
                                                                myStartActivity(activity_noticeBoard_post.class, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount, consumer, roomID, completepublisher, "NO", complete, boxnum, checkpublisher, checkconsumer));
                                                            }
                                                            finish();

                                                            showToast(activity_noticeBoard_post.this, "상대에게 거래점수를 1점 주셨습니다.");
                                                        }
                                                    }
                                                });




                                    }
                                }
                            }
                        }
                    });
                    break;
            }
        }
    };

    private void SetPostValues(DocumentSnapshot document) {
        title = document.getData().get("title").toString();
        price = Integer.valueOf(document.getData().get("price").toString());
        term = document.getData().get("term").toString();
        contents = document.getData().get("contents").toString();
        publisher = document.getData().get("publisher").toString();
        createdAt = document.getTimestamp("createdAt").toDate();
        id = document.getData().get("id").toString();
        viewCount = Integer.valueOf(document.getData().get("viewCount").toString());
        consumer = document.getData().get("consumer").toString();
        roomID = document.getData().get("roomID").toString();
        completepublisher = document.getData().get("completepublisher").toString();
        completeconsumer = document.getData().get("completeconsumer").toString();
        complete = document.getData().get("complete").toString();
        boxnum = Integer.valueOf(document.getData().get("boxnum").toString());
        checkpublisher = document.getData().get("checkpublisher").toString();
        checkconsumer = document.getData().get("checkconsumer").toString();
    }

    private void uploader_postInfo(DocumentReference documentReference, final com.example.myapplication.info.postInfo postInfo) {
        documentReference.set(postInfo.getPostInfo())
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

    private void uploader_memberInfo(DocumentReference documentReference, final com.example.myapplication.info.memberInfo memberInfo) {
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

    private void uploader_deliveryInfo(DocumentReference documentReference, final deliveryInfo deliveryInfo) {
        documentReference.set(deliveryInfo.getDeliveryInfo())
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if(data != null) {
                    postInfo = (com.example.myapplication.info.postInfo) data.getSerializableExtra("postInfo");

                    // 수정한 내용이 보이도록 새로 갱신
                    setTextinvalues();
                }
                break;
        }
    }

    //버튼이 눌렸을때 여기로옴
    public void showPopup(View v){
        //팝업 메뉴 객체 만듬
        PopupMenu popup = new PopupMenu(this, v);
        //xml파일에 메뉴 정의한것을 가져오기위해서 전개자 선언
        MenuInflater inflater = popup.getMenuInflater();
        //실제 메뉴 정의한것을 가져오는 부분 menu 객체에 넣어줌
        inflater.inflate(R.menu.post, popup.getMenu());
        //메뉴가 클릭했을때 처리하는 부분
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //각 메뉴별 아이디를 조사한후 할일을 적어줌
                switch(menuItem.getItemId()){
                    case R.id.notify:
                        // 수정
                        myStartActivity(activity_noticeBoard_writePost.class, postInfo);
                        break;
                    case R.id.delete:
                        // 삭제
                        String id = postInfo.getId();
                        db.collection("posts").document(id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        showToast(activity_noticeBoard_post.this, "게시글을 삭제하였습니다");
                                        onBackPressed();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showToast(activity_noticeBoard_post.this, "게시글을 삭제하지 못하였습니다");
                                    }
                                });
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    private void myStartActivity(Class c, com.example.myapplication.info.postInfo postInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", postInfo);
        startActivityForResult(intent, 0);
    }
}