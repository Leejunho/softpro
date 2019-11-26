package com.example.myapplication.activity;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MemberInfo;
import com.example.myapplication.R;
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

import static com.example.myapplication.Util.showToast;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        showToast(MainActivity.this,"반갑습니다");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 현재 사용자 전화번호를 읽어옴
        readlastcount();

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.button_delivery:  // 택배현황 버튼 클릭했을때 동작
                    updateviewcount(3);
                    myStartActivity(DeliverystatusActivity.class);
                    break;

                case R.id.button_board:  // 게시판 버튼 클릭했을때 동작
                    updateviewcount(1);
                    myStartActivity(NoticeboardActivity.class);
                    break;

                case R.id.button_note:  // 쪽지함 버튼 클릭했을때 동작
                    updateviewcount(2);
                    myStartActivity(ChatRoomActivity.class);
                    break;

                case R.id.button_profile:  // 회원정보 버튼 클릭했을때 동작
                    myStartActivity(profileActivity.class);
                    break;

                case R.id.imageView_mainlogo:  // 메인로고 클릭했을때 동작
                    myStartActivity(DevelopuserActivity.class);
                    break;
            }
        }
    };

    private void readlastcount() {
        if(user == null) {
            myStartActivity(LoginActivity.class);
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
                            else textView_posts.setText("게시판");

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
                                    myStartActivity(AddinfoActivity.class);
                                    finish();
                                    showToast(MainActivity.this,"회원정보를 추가입력해주세요");
                                }
                                if(document.getData().get("point") != null) {
                                    textView_point.setText("거래점수: " + document.getData().get("point").toString() +"점");
                                }
                            } else {
                                myStartActivity(AddinfoActivity.class);
                                finish();
                                showToast(MainActivity.this,"회원정보를 추가입력해주세요");
                            }
                        }
                    } else {
                        showToast(MainActivity.this,"잠시 후 다시 시도해주세요");
                    }
                }
            });
        }
        else {
            myStartActivity(LoginActivity.class);
            finish();
        }
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
                                    MemberInfo memberInfo = new MemberInfo(document.getData().get("nickname").toString(), document.getData().get("address").toString(), document.getData().get("telephone").toString(), document.getData().get("photoUrl").toString(), Integer.valueOf(document.getData().get("point").toString()), user.getUid(), document.getData().get("usermsg").toString(), document.getData().get("token").toString(), document.getData().get("replacenum").toString(), currentcountpost, lastcountmsg, lastcountbox);
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
                                    MemberInfo memberInfo = new MemberInfo(document.getData().get("nickname").toString(), document.getData().get("address").toString(), document.getData().get("telephone").toString(), document.getData().get("photoUrl").toString(), Integer.valueOf(document.getData().get("point").toString()), user.getUid(), document.getData().get("usermsg").toString(), document.getData().get("token").toString(), document.getData().get("replacenum").toString(), lastcountpost, currentcountmsg, lastcountbox);
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
                                    MemberInfo memberInfo = new MemberInfo(document.getData().get("nickname").toString(), document.getData().get("address").toString(), document.getData().get("telephone").toString(), document.getData().get("photoUrl").toString(), Integer.valueOf(document.getData().get("point").toString()), user.getUid(), document.getData().get("usermsg").toString(), document.getData().get("token").toString(), document.getData().get("replacenum").toString(), lastcountpost, lastcountmsg, currentcountbox);
                                    uploader_memberInfo(documentReference, memberInfo);
                                }
                            }
                        }
                    }
                });
                break;
        }
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


    public void onBackPressed() {
        Toast.makeText(this, "앱을 종료합니다.", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}
