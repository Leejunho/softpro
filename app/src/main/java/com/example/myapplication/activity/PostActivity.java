package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;

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

import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.example.myapplication.Util.showToast;

public class PostActivity extends BasicActivity {
    private PostInfo postInfo;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private TextView textView_title;
    private TextView textView_contents;
    private TextView createdAtTextView;
    private TextView textView_point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // 게시글을 클릭하면 postInfo 데이터를 PostActivity로 넘겨받음.
        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");


        findViewById(R.id.menu).setOnClickListener(onClickListener);
        findViewById(R.id.button_send).setOnClickListener(onClickListener);

        setTextinvalues();

        checkauthority();
    }

    private void setTextinvalues() {
        // 게시글 물품제목
        textView_title = findViewById(R.id.textView_title);
        textView_title.setText(postInfo.getTitle());

        // 게시글 제안금액
        textView_contents = findViewById(R.id.editText_price);
        textView_contents.setText(String.valueOf(postInfo.getPrice()));

        // 게시글 기간
        textView_contents = findViewById(R.id.editText_term);
        textView_contents.setText(postInfo.getTerm() + "까지");

        // 게시글 내용
        textView_contents = findViewById(R.id.textView_contents);
        textView_contents.setText(postInfo.getContents());

        // 게시글 올린 날짜
        createdAtTextView = findViewById(R.id.textView_createdAt);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss 작성", Locale.getDefault()).format(postInfo.getCreatedAt()));

        // 거래점수
        textView_point = findViewById(R.id.textView_point);
        DocumentReference documentReference = db.collection("users").document(postInfo.getPublisher());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            if(document.getData().get("point") != null) {
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
        else if(user.getUid().equals(postInfo.getPublisher())) {
            // 게시글의 작성자PostAdapter
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

    @Override
    protected void onResume() {
        super.onResume();
        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");
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
            }
        }
    };

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