package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.example.myapplication.PostInfo;
import com.example.myapplication.adapter.FirestoreAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication.Util.showToast;

public class SelectUserActivity extends BasicActivity {
    // 최종 입수하기 후 채팅방 이동 activity
    private String roomID;
    private Map<String, String> selectedUsers = new HashMap<>();
    private FirestoreAdapter firestoreAdapter;
    private FirebaseFirestore db;
    private PostInfo postInfo;
    private String nickname;
    private DatabaseReference mDatabase;

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
        //setContentView(R.layout.activity_select_user);

        db = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        roomID = getIntent().getStringExtra("roomID");
        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");

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
            selectedUsers.put(FirebaseAuth.getInstance().getCurrentUser().getUid(), "");
            DocumentReference newRoom = FirebaseFirestore.getInstance().collection("rooms").document();
            CreateChattingRoom(newRoom);
        }
        else {  // 채팅방이 이미 있는 상태인 경우  -> 이거 안씀
            CreateChattingRoom(FirebaseFirestore.getInstance().collection("rooms").document(roomID) );
        }
    }


    public void CreateChattingRoom(final DocumentReference room) {
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

        room.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    selectedUsers.put(postInfo.getPublisher(), nickname);
                    Intent intent = new Intent(SelectUserActivity.this, ChatActivity.class);
                    intent.putExtra("roomID", room.getId());
                    startActivity(intent);
                    SelectUserActivity.this.finish();
                }
                else {
                    showToast(SelectUserActivity.this, "입수신청을 실패했습니다. 잠시 후 다시 시도해주세요");
                }
            }
        });
    }
}
