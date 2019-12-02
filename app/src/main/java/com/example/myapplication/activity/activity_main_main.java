package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.info.memberInfo;
import com.example.myapplication.info.postInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

import static com.example.myapplication.util.util_util.showToast;

public class activity_main_main extends activity_main_basic {
    private static final String TAG = "activity_main_main";
    private FirebaseFirestore db;
    private FirebaseUser user;

    private TextView textView_nickname;
    private TextView textView_point;
    private TextView textView_posts;
    private TextView textView_rooms;
    private TextView textView_delivery;

    private int lastcountpost;
    private int lastcountmsg;
    private int lastcountbox;

    private int currentcountpost;
    private int currentcountmsg;
    private int currentcountbox;

    private String telephone;
    private String replacenum;

    private int count;
    private int count2;

    private String word;
    private int start;
    private int end;

    private long backbtnTime = 0;

    private String title;
    private int price;
    private String term;
    private String contents;
    private String publisher;
    private Date createdAt;
    private String postid;
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
        setContentView(R.layout.activity_main_main);

        findViewById(R.id.button_delivery).setOnClickListener(onClickListener);
        findViewById(R.id.button_board).setOnClickListener(onClickListener);
        findViewById(R.id.button_note).setOnClickListener(onClickListener);
        findViewById(R.id.button_profile).setOnClickListener(onClickListener);
        findViewById(R.id.imageView_mainlogo).setOnClickListener(onClickListener);

        textView_nickname = (TextView) findViewById(R.id.textView_usernickname);
        textView_point = (TextView) findViewById(R.id.textView_point);
        textView_posts = (TextView) findViewById(R.id.textView_posts);
        textView_rooms = (TextView) findViewById(R.id.textView_rooms);
        textView_delivery = (TextView) findViewById(R.id.textView_delivery);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        readlastcount();

        showToast(activity_main_main.this,"반갑습니다");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 현재 사용자 전화번호를 읽어옴
        readlastcount();
        ShowPopUp_NoRead_FinishTrade();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.button_delivery:  // 택배현황 버튼 클릭했을때 동작
                    updateviewcount(3);
                    myStartActivity(activity_delivery_delivery.class);
                    break;

                case R.id.button_board:  // 게시판 버튼 클릭했을때 동작
                    updateviewcount(1);
                    myStartActivity(activity_noticeBoard_main.class);
                    break;

                case R.id.button_note:  // 쪽지함 버튼 클릭했을때 동작
                    updateviewcount(2);
                    myStartActivity(activity_chat_room.class);
                    break;

                case R.id.button_profile:  // 회원정보 버튼 클릭했을때 동작
                    myStartActivity(activity_profile_profile.class);
                    break;

                case R.id.imageView_mainlogo:  // 메인로고 클릭했을때 동작
                    myStartActivity(activity_main_developUser.class);
                    break;

            }
        }
    };


    private void ShowPopUp_NoRead_FinishTrade() {
        CollectionReference collectionReference = db.collection("posts");
        collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()  // 시간순으로 내림차순 정렬하여 게시판 값을 읽어들임
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 거래가 완료된 것들 중에
                                if(document.getData().get("complete").toString().equals("YES")) {
                                    // 게시글 작성자인 경우
                                    if(document.getData().get("publisher").toString().equals(user.getUid())) {
                                        // 아직 게시글을 확인하지 않은 사람에게
                                        if(document.getData().get("checkpublisher").toString().equals("NO")) {
                                            // 팝업 레이아웃을 띄움
                                            SetPostValues(document);
                                            showPopUp(new postInfo(title, price, term, contents, publisher, createdAt, postid, viewCount, consumer, roomID, completepublisher, completeconsumer, complete, boxnum, checkpublisher, checkconsumer));
                                        }
                                    }
                                    // 입수자인 경우
                                    if(document.getData().get("consumer").toString().equals(user.getUid())) {
                                        // 아직 게시글을 확인하지 않은 사람에게
                                        if(document.getData().get("checkconsumer").toString().equals("NO")) {
                                            // 팝업 레이아웃을 띄움
                                            SetPostValues(document);
                                            showPopUp(new postInfo(title, price, term, contents, publisher, createdAt, postid, viewCount, consumer, roomID, completepublisher, completeconsumer, complete, boxnum, checkpublisher, checkconsumer));
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void showPopUp(final postInfo postInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String str = "거래완료";
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FA0000")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.setTitle(ssb).setMessage("제목 : " + postInfo.getTitle() + "\n추천 기능을 이용하시겠습니까?");

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                myStartActivity(activity_noticeBoard_post.class, postInfo);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                final DocumentReference documentReference_cancel = db.collection("posts").document(postInfo.getId());
                documentReference_cancel.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    SetPostValues(document);
                                    if(user.getUid().equals(publisher)) {
                                        uploader_postInfo(documentReference_cancel, new postInfo(title, price, term, contents, publisher, createdAt, postid, viewCount, consumer, roomID, completepublisher, completeconsumer, complete, boxnum, "YES", checkconsumer));
                                    }
                                    else if(user.getUid().equals(consumer)) {
                                        uploader_postInfo(documentReference_cancel, new postInfo(title, price, term, contents, publisher, createdAt, postid, viewCount, consumer, roomID, completepublisher, completeconsumer, complete, boxnum, checkpublisher, "YES"));
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void readlastcount() {
        if(user == null) {
            myStartActivity(activity_login_login.class);
            finish();
        }
        else {
            DocumentReference documentReference = db.collection("users").document(user.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                lastcountpost = Integer.valueOf(document.getData().get("countpost").toString());
                                lastcountmsg = Integer.valueOf(document.getData().get("countmsg").toString());
                                lastcountbox = Integer.valueOf(document.getData().get("countbox").toString());

                                readcurrentcount();
                            }
                        }
                    }
                }
            });
        }
    }

    private void readcurrentcount() {
        db.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            count = 0;
                            count2 = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                count++;
                                if(document.getData().get("publisher").equals(user.getUid())) {
                                    if(!document.getData().get("consumer").equals("")) {
                                        count2++;
                                    }
                                }
                            }
                            currentcountpost = count;
                            currentcountmsg = count2;

                            if(currentcountpost- lastcountpost > 0) {
                                String numberstr = String.valueOf(currentcountpost- lastcountpost);
                                int numlength = numberstr.length()+1;
                                String str = "거래 게시판" + " +" + (currentcountpost- lastcountpost);
                                SpannableStringBuilder ssb = new SpannableStringBuilder(str);
                                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FA0000")), 7, 7 + numlength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                textView_posts.setText(ssb);
                            }
                            else textView_posts.setText("거래 게시판");

                            if(currentcountmsg- lastcountmsg> 0) {
                                String numberstr = String.valueOf(currentcountmsg- lastcountmsg);
                                int numlength = numberstr.length()+1;
                                String str = "채팅" + " +" + (currentcountmsg- lastcountmsg);
                                SpannableStringBuilder ssb = new SpannableStringBuilder(str);
                                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FA0000")), 3, 3 + numlength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                textView_rooms.setText(ssb);
                            }
                            else textView_rooms.setText("채팅");
                        }
                    }
                });

        db.collection("delivery")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentReference documentReference = db.collection("users").document(user.getUid());
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null) {
                                            if (document.exists()) {
                                                telephone = document.getData().get("telephone").toString();
                                                replacenum = document.getData().get("replacenum").toString();
                                            }
                                        }
                                    }
                                }
                            });

                            count = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getData().get("telephone").toString().equals(telephone) || document.getData().get("telephone").toString().equals(replacenum))
                                    count++;
                            }
                            currentcountbox = count;
                            if(currentcountbox- lastcountbox > 0) {
                                String numberstr = String.valueOf(currentcountbox- lastcountbox);
                                int numlength = numberstr.length()+1;
                                String str = "택배현황" + " +" + (currentcountbox- lastcountbox);
                                SpannableStringBuilder ssb = new SpannableStringBuilder(str);
                                ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FA0000")), 5, 5 + numlength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                textView_delivery.setText(ssb);
                            }
                            else textView_delivery.setText("택배현황");
                        }
                    }
                });
    }

    private void updateviewcount(int choice) {
        // 조회수 갱신
        final DocumentReference documentReference = db.collection("users").document(user.getUid());
        switch(choice) {
            case 1:
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    String photoUrl = null;
                                    if(document.getData().get("photoUrl") != null) {
                                        photoUrl = document.getData().get("photoUrl").toString();
                                    }
                                    memberInfo memberInfo = new memberInfo(document.getData().get("nickname").toString(), document.getData().get("address").toString(), document.getData().get("telephone").toString(), photoUrl, Integer.valueOf(document.getData().get("point").toString()), user.getUid(), document.getData().get("usermsg").toString(), document.getData().get("token").toString(), document.getData().get("replacenum").toString(), currentcountpost, lastcountmsg, lastcountbox);
                                    uploader_memberInfo(documentReference, memberInfo);
                                }
                            }
                        }
                    }
                });
                break;

            case 2:
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    String photoUrl = null;
                                    if(document.getData().get("photoUrl") != null) {
                                        photoUrl = document.getData().get("photoUrl").toString();
                                    }
                                    memberInfo memberInfo = new memberInfo(document.getData().get("nickname").toString(), document.getData().get("address").toString(), document.getData().get("telephone").toString(), photoUrl, Integer.valueOf(document.getData().get("point").toString()), user.getUid(), document.getData().get("usermsg").toString(), document.getData().get("token").toString(), document.getData().get("replacenum").toString(), lastcountpost, currentcountmsg, lastcountbox);
                                    uploader_memberInfo(documentReference, memberInfo);
                                }
                            }
                        }
                    }
                });
                break;

            case 3:
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    String photoUrl = null;
                                    if(document.getData().get("photoUrl") != null) {
                                        photoUrl = document.getData().get("photoUrl").toString();
                                    }
                                    memberInfo memberInfo = new memberInfo(document.getData().get("nickname").toString(), document.getData().get("address").toString(), document.getData().get("telephone").toString(), photoUrl, Integer.valueOf(document.getData().get("point").toString()), user.getUid(), document.getData().get("usermsg").toString(), document.getData().get("token").toString(), document.getData().get("replacenum").toString(), lastcountpost, lastcountmsg, currentcountbox);
                                    uploader_memberInfo(documentReference, memberInfo);
                                }
                            }
                        }
                    }
                });
                break;
        }
    }

    public void onStart() { // 사용자가 현재 로그인되어 있는지 확인
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // 로그인한 사용자 정보가 이미 입력되어 있는 상태가 아니라면 addInfo로 이동
        if(user != null) {
            DocumentReference documentReference = db.collection("users").document(user.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                if(document.getData().get("nickname") != null) {
                                    textView_nickname.setText(document.getData().get("nickname").toString() + "님 반갑습니다");
                                }
                                else {
                                    myStartActivity(activity_login_addInfo.class);
                                    finish();
                                    showToast(activity_main_main.this,"회원정보를 추가입력해주세요");
                                }
                                if(document.getData().get("point") != null) {
                                    textView_point.setText("거래점수: " + document.getData().get("point").toString() +"점");
                                }
                            } else {
                                myStartActivity(activity_login_addInfo.class);
                                finish();
                                showToast(activity_main_main.this,"회원정보를 추가입력해주세요");
                            }
                        }
                    } else {
                        showToast(activity_main_main.this,"잠시 후 다시 시도해주세요");
                    }
                }
            });
        }
        else {
            myStartActivity(activity_login_login.class);
            finish();
        }
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

    private void uploader_memberInfo(DocumentReference documentReference, final memberInfo memberInfo) {
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

    private void SetPostValues(DocumentSnapshot document) {
        title = document.getData().get("title").toString();
        price = Integer.valueOf(document.getData().get("price").toString());
        term = document.getData().get("term").toString();
        contents = document.getData().get("contents").toString();
        publisher = document.getData().get("publisher").toString();
        createdAt = document.getTimestamp("createdAt").toDate();
        postid = document.getData().get("id").toString();
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

    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backbtnTime;

        if(0 <= gapTime && 2000 >= gapTime) {
            super.onBackPressed();
        }
        else {
            backbtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show();
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void myStartActivity(Class c, postInfo postInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", postInfo);
        startActivityForResult(intent, 0);
    }
}
