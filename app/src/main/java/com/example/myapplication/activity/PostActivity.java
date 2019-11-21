package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.myapplication.PostInfo;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.example.myapplication.Util.showToast;

public class PostActivity extends BasicActivity {
    private PostInfo postInfo;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");
        db = FirebaseFirestore.getInstance();

        // 게시글 제목
        TextView textView_title = findViewById(R.id.TextView_title);
        textView_title.setText(postInfo.getTitle());

        // 게시글 내용
        TextView textView_contents = findViewById(R.id.textView_contents);
        textView_contents.setText(postInfo.getContents());

        // 게시글 올린 날짜
        TextView createdAtTextView = findViewById(R.id.textView_createdAt);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(postInfo.getCreatedAt()));

        findViewById(R.id.menu).setOnClickListener(onClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        postUpdate();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu:  // 점3개 메뉴 버튼 클릭했을때 동작
                    showPopup(v);
                    break;
            }
        }
    };

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
                    case R.id.modify:
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
        startActivity(intent);
    }
}