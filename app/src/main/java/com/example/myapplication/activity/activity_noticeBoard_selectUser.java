package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.myapplication.adapter.adapter_noticeBoard_firestore;
import com.example.myapplication.info.postInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.util.util_util.showToast;

public class activity_noticeBoard_selectUser extends activity_main_basic {
    // 최종 입수하기 후 채팅방 이동 activity
    private String roomID;
    private Map<String, String> selectedUsers = new HashMap<>();
    private adapter_noticeBoard_firestore firestoreAdapter;
    private FirebaseFirestore db;
    private postInfo postInfo;
    private String nickname;
    private DatabaseReference mDatabase;
    private String postinfoid;

    @Override
    public void onStart() {
        super.onStart();
        if (firestoreAdapter != null) {
            firestoreAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firestoreAdapter != null) {
            firestoreAdapter.stopListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        roomID = getIntent().getStringExtra("roomID");
        postInfo = (postInfo)getIntent().getSerializableExtra("postInfo");

        nickname = "";
        DocumentReference documentReference = db.collection("users").document(postInfo.getPublisher());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            nickname = document.getData().get("nickname").toString();
                        }
                    }
                }
            }
        });

        if (roomID==null) {  // 채팅방을 새로 개설하는 경우
            postInfo = (postInfo)getIntent().getSerializableExtra("postInfo");
            selectedUsers.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), "");
            final DocumentReference newRoom = FirebaseFirestore.getInstance().collection("rooms").document();

            db.collection("posts")
                    .whereEqualTo("title", postInfo.getTitle())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    postinfoid = document.getId();

                                    DocumentReference documentReference2 = db.collection("posts").document(postinfoid);
                                    postInfo postInfo2 = new postInfo(postInfo.getTitle(), postInfo.getPrice(), postInfo.getTerm(), postInfo.getContents(), postInfo.getPublisher(), postInfo.getCreatedAt(), postinfoid, postInfo.getViewCount(), postInfo.getConsumer(), newRoom.getId(), postInfo.getCompletepublisher(), postInfo.getCompleteconsumer(), postInfo.getComplete(), postInfo.getBoxnum(), postInfo.getCheckpublisher(), postInfo.getCheckconsumer());
                                    uploader(documentReference2, postInfo2, newRoom);
                                }
                            }
                        }
                    });
        }
        else {  // 채팅방이 이미 있는 상태인 경우  -> 이거 안씀
            //CreateChattingRoom(FirebaseFirestore.getInstance().collection("rooms").document(roomID) );
        }
    }

    private void uploader(DocumentReference documentReference, final postInfo postInfo, final DocumentReference newRoom) {
        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CreateChattingRoom(postInfo, newRoom);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    public void CreateChattingRoom(final postInfo postInfo, final DocumentReference room) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Integer> users = new HashMap<>();
        String title = "";  // 채팅방 제목

        if(postInfo != null) {  // 채팅방 제목을 게시글 제목으로 설정
            title = postInfo.getTitle();
        }

        // 현재 사용자
        users.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), 0);
        // 채팅방 만든사람
        users.put(postInfo.getPublisher(), 0);

        // 같이 유저에 넣고 채팅방 개설
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("users", users);
        data.put("complete", "NO");
        data.put("postID", postInfo.getId());

        room.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    selectedUsers.put(postInfo.getPublisher(), nickname);

                    // activity to stack delete for wish activity
                    // go to main
                    Intent intent = new Intent(activity_noticeBoard_selectUser.this, activity_noticeBoard_main.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                    // go to post
                    Intent intent1 = new Intent(activity_noticeBoard_selectUser.this, activity_noticeBoard_post.class);
                    intent1.putExtra("postInfo", postInfo);
                    startActivity(intent1);


                    Intent intent2 = new Intent(activity_noticeBoard_selectUser.this, activity_chat_chat.class);
                    intent2.putExtra("roomID", room.getId());
                    startActivity(intent2);

                    //activity_noticeBoard_selectUser.this.finish();
                    showToast(activity_noticeBoard_selectUser.this, "거래신청을 완료 하였습니다");
                }
                else {
                    showToast(activity_noticeBoard_selectUser.this, "입수신청을 실패했습니다. 잠시 후 다시 시도해주세요");
                }
            }
        });
    }
}
