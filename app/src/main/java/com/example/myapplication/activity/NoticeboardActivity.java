package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.PostInfo;
import com.example.myapplication.R;
import com.example.myapplication.adapter.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class NoticeboardActivity extends BasicActivity {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private PostAdapter postAdapter;
    private ArrayList<PostInfo> postList;
    private String name = "createdAt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        postList = new ArrayList<>();

        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);
        findViewById(R.id.menu).setOnClickListener(onClickListener);

        postAdapter = new PostAdapter(NoticeboardActivity.this, postList);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(NoticeboardActivity.this, 1));  // 게시판 spanCount:1 줄씩 표시
        recyclerView.setAdapter(postAdapter);
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
                case R.id.floatingActionButton:  // 글쓰기 버튼 클릭했을때 동작
                    myStartActivity(WritePostActivity.class);
                    break;

                case R.id.menu:  // 점3개 메뉴 버튼 클릭했을때 동작
                    showPopup(v);
                    break;
            }
        }
    };

    private void postUpdate() {
        if(user != null) {
            CollectionReference collectionReference = db.collection("posts");
            collectionReference.orderBy(name, Query.Direction.DESCENDING).get()  // 시간순으로 내림차순 정렬하여 게시판에 보여줌
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    postList.add(new PostInfo(
                                            // db에 저장되어있는 게시판 작성의 값들을 postList에 저장
                                            document.getData().get("title").toString(),
                                            Integer.parseInt(document.getData().get("price").toString()),
                                            document.getData().get("term").toString(),
                                            document.getData().get("contents").toString(),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId(),
                                            ((Long)document.getData().get("viewCount")).intValue(),
                                            document.getData().get("consumer").toString()
                                    ));
                                }
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.notice, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //Log.d("1", "메뉴를 눌렀슴: ");
                switch(menuItem.getItemId()){
                    case R.id.sortbytime:
                        // 시간순 정렬
                        name = "createdAt";
                        postUpdate();
                        break;

                    case R.id.sortbymoney:
                        // 금액순 정렬
                        name = "price";
                        postUpdate();
                        break;

                    case R.id.sortbyviewCount:
                        // 금액순 정렬
                        name = "viewCount";
                        postUpdate();
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}