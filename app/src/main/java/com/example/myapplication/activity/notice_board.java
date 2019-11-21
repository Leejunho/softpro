package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.PostInfo;
import com.example.myapplication.R;
import com.example.myapplication.adapter.PostAdapter;
import com.example.myapplication.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static com.example.myapplication.Util.showToast;

public class notice_board extends BasicActivity {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private PostAdapter postAdapter;
    private ArrayList<PostInfo> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        postList = new ArrayList<>();

        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        postAdapter = new PostAdapter(notice_board.this, postList);
        postAdapter.setOnPostListener(onPostListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(notice_board.this, 1));  // 게시판 spanCount:1 줄씩 표시

        recyclerView.setAdapter(postAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        postUpdate();
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(int position) {
            // 게시글 삭제
            String id = postList.get(position).getId();
            db.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(notice_board.this, "게시글을 삭제하였습니다");
                            postUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(notice_board.this, "게시글을 삭제하지 못하였습니다");
                        }
                    });
        }

        @Override
        public void onModify(int position) {
            // 게시글 수정  postList의 정보들을 전달  postList 정보들이 postInfo 형식임
            myStartActivity(WritePostActivity.class, postList.get(position));
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.floatingActionButton:  // 글쓰기 버튼 클릭했을때 동작
                    myStartActivity(WritePostActivity.class);
                    break;
            }
        }
    };

    private void postUpdate() {
        if(user != null) {
            CollectionReference collectionReference = db.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()  // 시간순으로 내림차순 정렬하여 게시판에 보여줌
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    postList.add(new PostInfo(
                                            // db에 저장되어있는 게시판 작성의 값들을 postList에 저장
                                            document.getData().get("title").toString(),
                                            document.getData().get("item_name").toString(),
                                            document.getData().get("price").toString(),
                                            document.getData().get("term").toString(),
                                            document.getData().get("contents").toString(),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId()
                                    ));
                                }
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", postInfo);
        startActivity(intent);
    }
}