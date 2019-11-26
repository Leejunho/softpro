package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.DeliveryInfo;
import com.example.myapplication.MemberInfo;
import com.example.myapplication.PostInfo;
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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.example.myapplication.Util.showToast;

public class PostActivity extends BasicActivity {
    private PostInfo postInfo;
    private MemberInfo memberInfo;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // 게시글을 클릭하면 postInfo 데이터를 PostActivity로 넘겨받음.
        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");

        // 입수신청 거래완료 거래취소 추천하기 버튼 보이지 않게 만듬
        findViewById(R.id.button_send).setVisibility(View.GONE);
        findViewById(R.id.button_complete).setVisibility(View.GONE);
        findViewById(R.id.button_cancel).setVisibility(View.GONE);
        findViewById(R.id.button_recommend).setVisibility(View.GONE);

        // 입수자 정보 보이지 않게 만듬
        findViewById(R.id.textView_i1).setVisibility(View.GONE);
        findViewById(R.id.LinearLayout_i3).setVisibility(View.GONE);
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

        setTextinvalues();

        if(!checktradefinish()) {
            checkauthority();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.button_send).setVisibility(View.GONE);
        findViewById(R.id.button_complete).setVisibility(View.GONE);
        findViewById(R.id.button_cancel).setVisibility(View.GONE);
        findViewById(R.id.button_recommend).setVisibility(View.GONE);
        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");

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
        if(user.getUid().equals("1R5r4L6O1PeA4h93RrCNYK4zQzS2") || user.getUid().equals("4cuaqNgeOCaun6dxrakLdRTkDpj1") || user.getUid().equals("T8oDzbNpoUYKd77AycscZU3bXry1"))  {
            // 관리자 GM
            // 모든 글 수정 삭제 가능
            findViewById(R.id.menu).setVisibility(View.VISIBLE);

            // 입수신청 버튼이 사용가능
            findViewById(R.id.button_send).setVisibility(View.VISIBLE);
        }
        else if(!postInfo.getConsumer().equals("")) {
            // 거래 진행 중

            // 입수신청 버튼이 사용불가
            findViewById(R.id.button_send).setVisibility(View.GONE);
            textView_currentprogress.setText("거래가 진행중인 물품입니다.");

            if(postInfo.getConsumer().equals(user.getUid()) || postInfo.getPublisher().equals(user.getUid())) {
                findViewById(R.id.button_complete).setVisibility(View.VISIBLE);
                findViewById(R.id.button_cancel).setVisibility(View.VISIBLE);

                showcosumerInfo();
            }
        }
        else if(user.getUid().equals(postInfo.getPublisher())) {
            // 게시글의 작성자
            // 수정 삭제 가능
            findViewById(R.id.menu).setVisibility(View.VISIBLE);

            // 입수신청 버튼이 사용불가
            findViewById(R.id.button_send).setVisibility(View.GONE);
        }
        else {
            // 자신의 게시글이 아닌사람
            // 수정 삭제 불가능하도록 View gone
            findViewById(R.id.menu).setVisibility(View.GONE);

            // 입수신청 버튼이 사용가능
            findViewById(R.id.button_send).setVisibility(View.VISIBLE);
        }
    }

    public boolean checktradefinish() {
        if(postInfo.getComplete().equals("YES")) {
            if(postInfo.getPublisher().equals(user.getUid())) {
                // 글 작성자
                showcosumerInfo();
                if(postInfo.getCompletepublisher().equals("YES")) {
                    // 투표권이 있는 경우에만 추천하기 버튼을 보여줌
                    findViewById(R.id.button_recommend).setVisibility(View.VISIBLE);
                }
            }
            else if(postInfo.getConsumer().equals(user.getUid())) {
                // 입수자
                showcosumerInfo();
                if(postInfo.getCompleteconsumer().equals("YES")) {
                    // 투표권이 있는 경우에만 추천하기 버튼을 보여줌
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



                    myStartActivity(CheckpostActivity.class, postInfo);
                    break;

                case R.id.button_complete:  // 거래완료 버튼 클릭했을때 동작
                    DocumentReference documentReference = db.collection("posts").document(postInfo.getId());
                    // db 저장 함수
                    if(user.getUid().equals(postInfo.getPublisher())) {
                        // 자신이 게시판 작성자 인 경우
                        if(postInfo.getCompletepublisher().equals("YES")) {
                            // 게시판 작성자가 이미 거래완료를 클릭한 경우
                            showToast(PostActivity.this, "거래완료 신청을 이미 하셨습니다.");
                        }
                        else {
                            uploader_postInfo(documentReference, new PostInfo(postInfo.getTitle(), postInfo.getPrice(), postInfo.getTerm(), postInfo.getContents(), postInfo.getPublisher(), postInfo.getCreatedAt(), postInfo.getViewCount(), postInfo.getConsumer(), postInfo.getRoomID(), "YES", postInfo.getCompleteconsumer(), postInfo.getComplete(), postInfo.getBoxnum()));
                            if (postInfo.getCompleteconsumer().equals("NO")) {
                                showToast(PostActivity.this, "거래를 완료하였습니다. 상대방의 거래완료를 대기중입니다.");
                            }
                            else {
                                chatmakecomplete();
                                textView_currentprogress.setText("거래가 완료되었습니다");
                            }
                        }
                    }
                    else if(user.getUid().equals(postInfo.getConsumer())){
                        if(postInfo.getCompletepublisher().equals("YES")) {
                            // 이미 거래완료를 클릭했을 경우
                            showToast(PostActivity.this, "거래완료 신청을 이미 하셨습니다.");
                        }
                        else {
                            uploader_postInfo(documentReference, new PostInfo(postInfo.getTitle(), postInfo.getPrice(), postInfo.getTerm(), postInfo.getContents(), postInfo.getPublisher(), postInfo.getCreatedAt(), postInfo.getViewCount(), postInfo.getConsumer(), postInfo.getRoomID(), postInfo.getCompletepublisher(), "YES", postInfo.getComplete(), postInfo.getBoxnum()));
                            if (postInfo.getCompletepublisher().equals("NO")) {
                                showToast(PostActivity.this, "거래를 완료하였습니다. 상대방의 거래완료를 대기중입니다.");
                            }
                            else {
                                chatmakecomplete();
                                textView_currentprogress.setText("거래가 완료되었습니다");
                            }
                        }
                    }



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
                                        uploader_deliveryInfo(documentReference_boxnum, new DeliveryInfo(document_boxnum.getData().get("telephone").toString(), document_boxnum.getData().get("boxnum").toString(), document_boxnum.getTimestamp("createdAt").toDate(), ""));
                                    }
                                }
                            }
                        }
                    });


                        // postinfo 거래 진행중 정보 삭제
                    DocumentReference documentReference_cancel = db.collection("posts").document(postInfo.getId());
                    uploader_postInfo(documentReference_cancel, new PostInfo(postInfo.getTitle(), postInfo.getPrice(), postInfo.getTerm(), postInfo.getContents(), postInfo.getPublisher(), postInfo.getCreatedAt(), postInfo.getViewCount(), "", "", "NO", "NO", "NO", postInfo.getBoxnum()));
                    showToast(PostActivity.this, "거래를 취소하였습니다.");
                    finish();
                    break;

                case R.id.button_recommend:  // 추천버튼 클릭했을때 동작
                    final String useruid;
                    // 상대에게 추천을 하기위해 상대 uid 정보를 받아옴
                    if(postInfo.getPublisher().equals(user.getUid())) {
                        // 거래글을 작성한 사람이 추천버튼을 눌렀을 때
                        useruid = postInfo.getConsumer();
                    }
                    else {
                        // 입수하여 물건을 맡아준 사람이 추천버튼을 눌렀을 때
                        useruid = postInfo.getPublisher();
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
                                            memberInfo = new MemberInfo(document.getData().get("nickname").toString(), document.getData().get("address").toString(), document.getData().get("telephone").toString(), photoUrl, Integer.valueOf(document.getData().get("point").toString()) + 1, document.getData().get("uid").toString(), document.getData().get("usermsg").toString(), document.getData().get("token").toString(), document.getData().get("replacenum").toString(), Integer.valueOf(document.getData().get("countpost").toString()), Integer.valueOf(document.getData().get("countmsg").toString()), Integer.valueOf(document.getData().get("countbox").toString()));
                                        }
                                        DocumentReference documentReference_recommend = db.collection("users").document(useruid);
                                        uploader_memberInfo(documentReference_recommend, memberInfo);

                                        DocumentReference documentReference_cancel = db.collection("posts").document(postInfo.getId());
                                        if(postInfo.getPublisher().equals(user.getUid())) {
                                            // 거래글을 작성한 사람이 추천버튼을 눌렀을 때
                                            uploader_postInfo(documentReference_cancel, new PostInfo(postInfo.getTitle(), postInfo.getPrice(), postInfo.getTerm(), postInfo.getContents(), postInfo.getPublisher(), postInfo.getCreatedAt(), postInfo.getViewCount(), postInfo.getConsumer(), postInfo.getRoomID(), "NO", postInfo.getCompleteconsumer(), postInfo.getComplete(), postInfo.getBoxnum()));
                                        }
                                        else {
                                            // 입수하여 물건을 맡아준 사람이 추천버튼을 눌렀을 때
                                            uploader_postInfo(documentReference_cancel, new PostInfo(postInfo.getTitle(), postInfo.getPrice(), postInfo.getTerm(), postInfo.getContents(), postInfo.getPublisher(), postInfo.getCreatedAt(), postInfo.getViewCount(), postInfo.getConsumer(), postInfo.getRoomID(), postInfo.getCompletepublisher(), "NO", postInfo.getComplete(), postInfo.getBoxnum()));
                                        }
                                        finish();
                                        showToast(PostActivity.this, "상대에게 거래점수를 1점 주셨습니다.");
                                    }
                                }
                            });
                    break;
            }
        }
    };

    private void chatmakecomplete() {
        // 거래 대화 진행중인 채팅방 complete YES로 변경 (거래 완료)
        Map<String, Object> data = new HashMap<>();
        data.put("complete", "YES");
        DocumentReference room = FirebaseFirestore.getInstance().collection("rooms").document(postInfo.getRoomID());
        room.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });

        // 해당 게시판 complete YES로 변경 (거래 완료)
        DocumentReference documentReference_complete = db.collection("posts").document(postInfo.getId());
        uploader_postInfo(documentReference_complete, new PostInfo(postInfo.getTitle(), postInfo.getPrice(), postInfo.getTerm(), postInfo.getContents(), postInfo.getPublisher(), postInfo.getCreatedAt(), postInfo.getViewCount(), postInfo.getConsumer(), postInfo.getRoomID(), postInfo.getCompletepublisher(), postInfo.getCompleteconsumer(), "YES", postInfo.getBoxnum()));
    }

    private void uploader_postInfo(DocumentReference documentReference, final PostInfo postInfo) {
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

    private void uploader_deliveryInfo(DocumentReference documentReference, final DeliveryInfo deliveryInfo) {
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
                    postInfo = (PostInfo) data.getSerializableExtra("postInfo");

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
                        myStartActivity(WritePostActivity.class, postInfo);
                        break;
                    case R.id.delete:
                        // 삭제
                        String id = postInfo.getId();
                        db.collection("posts").document(id)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        showToast(PostActivity.this, "게시글을 삭제하였습니다");
                                        onBackPressed();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showToast(PostActivity.this, "게시글을 삭제하지 못하였습니다");
                                    }
                                });
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", postInfo);
        startActivityForResult(intent, 0);
    }
}