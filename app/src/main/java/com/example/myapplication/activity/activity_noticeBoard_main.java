package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.adapter_noticeBoard_post;
import com.example.myapplication.info.postInfo;
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

public class activity_noticeBoard_main extends activity_main_basic {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private adapter_noticeBoard_post postAdapter;
    private ArrayList<postInfo> postList;
    private String name = "createdAt";
    private int ChoiceOrderShown = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeboard_main);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        postList = new ArrayList<>();

        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);
        findViewById(R.id.menu).setOnClickListener(onClickListener);

        postAdapter = new adapter_noticeBoard_post(activity_noticeBoard_main.this, postList);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(activity_noticeBoard_main.this, 1));  // 게시판 spanCount:1 줄씩 표시
        recyclerView.setAdapter(postAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //postAdapter.notifyDataSetChanged();
        showChoiceOrder();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.floatingActionButton:  // 글쓰기 버튼 클릭했을때 동작
                    myStartActivity(activity_noticeBoard_writePost.class);
                    break;

                case R.id.menu:  // 점3개 메뉴 버튼 클릭했을때 동작
                    showPopupMenu(v);
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
                                    String did = document.getData().get("id") == null ? document.getId() : document.getData().get("id").toString();
                                    postList.add(new postInfo(
                                            // db에 저장되어있는 게시판 작성의 값들을 postList에 저장
                                            document.getData().get("title").toString(),
                                            Integer.parseInt(document.getData().get("price").toString()),
                                            document.getData().get("term").toString(),
                                            document.getData().get("contents").toString(),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            did,
                                            ((Long) document.getData().get("viewCount")).intValue(),
                                            document.getData().get("consumer").toString(),
                                            document.getData().get("roomID").toString(),
                                            document.getData().get("completepublisher").toString(),
                                            document.getData().get("completeconsumer").toString(),
                                            document.getData().get("complete").toString(),
                                            ((Long) document.getData().get("boxnum")).intValue(),
                                            document.getData().get("checkpublisher").toString(),
                                            document.getData().get("checkconsumer").toString()
                                    ));
                                }
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    private void postUpdateIsmine() {
        if(user != null) {
            CollectionReference collectionReference = db.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()  // 시간순으로 내림차순 정렬하여 게시판에 보여줌
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getData().get("publisher").toString().equals(user.getUid())) {
                                        String did = document.getData().get("id") == null ? document.getId() : document.getData().get("id").toString();
                                        postList.add(new postInfo(
                                                // db에 저장되어있는 게시판 작성의 값들을 postList에 저장
                                                document.getData().get("title").toString(),
                                                Integer.parseInt(document.getData().get("price").toString()),
                                                document.getData().get("term").toString(),
                                                document.getData().get("contents").toString(),
                                                document.getData().get("publisher").toString(),
                                                new Date(document.getDate("createdAt").getTime()),
                                                did,
                                                ((Long) document.getData().get("viewCount")).intValue(),
                                                document.getData().get("consumer").toString(),
                                                document.getData().get("roomID").toString(),
                                                document.getData().get("completepublisher").toString(),
                                                document.getData().get("completeconsumer").toString(),
                                                document.getData().get("complete").toString(),
                                                ((Long) document.getData().get("boxnum")).intValue(),
                                                document.getData().get("checkpublisher").toString(),
                                                document.getData().get("checkconsumer").toString()
                                        ));

                                    }
                                }
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    private void postUpdatecurrentTrade() {
        if(user != null) {
            CollectionReference collectionReference = db.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()  // 시간순으로 내림차순 정렬하여 게시판에 보여줌
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getData().get("publisher").toString().equals(user.getUid()) || document.getData().get("consumer").toString().equals(user.getUid())) {
                                        String did = document.getData().get("id") == null ? document.getId() : document.getData().get("id").toString();
                                        postList.add(new postInfo(
                                                // db에 저장되어있는 게시판 작성의 값들을 postList에 저장
                                                document.getData().get("title").toString(),
                                                Integer.parseInt(document.getData().get("price").toString()),
                                                document.getData().get("term").toString(),
                                                document.getData().get("contents").toString(),
                                                document.getData().get("publisher").toString(),
                                                new Date(document.getDate("createdAt").getTime()),
                                                did,
                                                ((Long) document.getData().get("viewCount")).intValue(),
                                                document.getData().get("consumer").toString(),
                                                document.getData().get("roomID").toString(),
                                                document.getData().get("completepublisher").toString(),
                                                document.getData().get("completeconsumer").toString(),
                                                document.getData().get("complete").toString(),
                                                ((Long) document.getData().get("boxnum")).intValue(),
                                                document.getData().get("checkpublisher").toString(),
                                                document.getData().get("checkconsumer").toString()
                                        ));
                                    }
                                }
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    private void postreadyTrade() {
        if(user != null) {
            CollectionReference collectionReference = db.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()  // 시간순으로 내림차순 정렬하여 게시판에 보여줌
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getData().get("roomID").toString().equals("")) {
                                        String did = document.getData().get("id") == null ? document.getId() : document.getData().get("id").toString();
                                        postList.add(new postInfo(
                                                // db에 저장되어있는 게시판 작성의 값들을 postList에 저장
                                                document.getData().get("title").toString(),
                                                Integer.parseInt(document.getData().get("price").toString()),
                                                document.getData().get("term").toString(),
                                                document.getData().get("contents").toString(),
                                                document.getData().get("publisher").toString(),
                                                new Date(document.getDate("createdAt").getTime()),
                                                did,
                                                ((Long) document.getData().get("viewCount")).intValue(),
                                                document.getData().get("consumer").toString(),
                                                document.getData().get("roomID").toString(),
                                                document.getData().get("completepublisher").toString(),
                                                document.getData().get("completeconsumer").toString(),
                                                document.getData().get("complete").toString(),
                                                ((Long) document.getData().get("boxnum")).intValue(),
                                                document.getData().get("checkpublisher").toString(),
                                                document.getData().get("checkconsumer").toString()
                                        ));
                                    }
                                }
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    private void postfinishTrade() {
        if(user != null) {
            CollectionReference collectionReference = db.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()  // 시간순으로 내림차순 정렬하여 게시판에 보여줌
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if(document.getData().get("complete").toString().equals("YES")) {
                                        String did = document.getData().get("id") == null ? document.getId() : document.getData().get("id").toString();
                                        postList.add(new postInfo(
                                                // db에 저장되어있는 게시판 작성의 값들을 postList에 저장
                                                document.getData().get("title").toString(),
                                                Integer.parseInt(document.getData().get("price").toString()),
                                                document.getData().get("term").toString(),
                                                document.getData().get("contents").toString(),
                                                document.getData().get("publisher").toString(),
                                                new Date(document.getDate("createdAt").getTime()),
                                                did,
                                                ((Long) document.getData().get("viewCount")).intValue(),
                                                document.getData().get("consumer").toString(),
                                                document.getData().get("roomID").toString(),
                                                document.getData().get("completepublisher").toString(),
                                                document.getData().get("completeconsumer").toString(),
                                                document.getData().get("complete").toString(),
                                                ((Long) document.getData().get("boxnum")).intValue(),
                                                document.getData().get("checkpublisher").toString(),
                                                document.getData().get("checkconsumer").toString()
                                        ));
                                    }
                                }
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    public void showPopupMenu(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.notice, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.sortbytime:
                        // 시간순 정렬
                        name = "createdAt";
                        ChoiceOrderShown = 1;
                        postUpdate();
                        break;

                    case R.id.sortbymoney:
                        // 금액순 정렬
                        name = "price";
                        ChoiceOrderShown = 2;
                        postUpdate();
                        break;

                    case R.id.sortbyviewCount:
                        // 금액순 정렬
                        name = "viewCount";
                        ChoiceOrderShown = 3;
                        postUpdate();
                        break;

                    case R.id.sortbymine:
                        // 자기가 등록한 글만 보이게
                        ChoiceOrderShown = 4;
                        postUpdateIsmine();
                        break;

                    case R.id.sortbytrade:
                        // 자신이 거래 중인 게시판만 보이게
                        ChoiceOrderShown = 5;
                        postUpdatecurrentTrade();
                        break;

                    case R.id.sortbyready:
                        // 거래 대기중인 게시글만 보이게
                        ChoiceOrderShown = 6;
                        postreadyTrade();
                        break;

                    case R.id.sortbyfinish:
                        // 거래 완료된 게시글만 보이게
                        ChoiceOrderShown = 7;
                        postfinishTrade();
                        break;
                }
                return false;
            }
        });
        popup.show();
    }

    private void showChoiceOrder() {
        switch (ChoiceOrderShown) {
            case 1:
                postUpdate();
                break;

            case 2:
                postUpdate();
                break;

            case 3:
                postUpdate();
                break;

            case 4:
                postUpdateIsmine();
                break;

            case 5:
                postUpdatecurrentTrade();
                break;

            case 6:
                postreadyTrade();
                break;

            case 7:
                postfinishTrade();
                break;
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}