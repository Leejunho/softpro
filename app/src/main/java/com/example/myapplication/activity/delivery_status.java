package com.example.myapplication.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DeliveryInfo;
import com.example.myapplication.R;
import com.example.myapplication.adapter.DeliveryAdapter;
import com.example.myapplication.listener.OnPostListener;
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

import java.util.ArrayList;
import java.util.Date;

import static com.example.myapplication.Util.showToast;

public class delivery_status extends BasicActivity {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private RelativeLayout loaderLayout;
    private DeliveryInfo deliveryInfo;
    private DeliveryAdapter deliveryAdapter;
    private ArrayList<DeliveryInfo> deliveryList;
    private String currentusertelephone = "nuuuull";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_status);
        loaderLayout = findViewById(R.id.loaderLayout);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        deliveryList = new ArrayList<>();

        findViewById(R.id.button_make).setOnClickListener(onClickListener);

        deliveryAdapter = new DeliveryAdapter(delivery_status.this, deliveryList);
        deliveryAdapter.setOnPostListener(onPostListener);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(delivery_status.this, 1));  // 게시판 spanCount:1 줄씩 표시
        recyclerView.setAdapter(deliveryAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 현재 사용자 전화번호를 읽어옴
        deliveryUpdate();
        // task 의 실행순서가 스택처럼 쌓아놓고 마지막에 쌓인 task 작업부터 하나씩 완료해 나가기 때문에 전화번호를 받아오는 작업을 Update 함수 뒤에 작성하였음
        get_CurrentUserTelephone();
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(int position) {
            // 택배 open 후 삭제
            String boxnum = deliveryList.get(position).getBoxnum();
            db.collection("delivery").document(boxnum)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(delivery_status.this, "택배함을 열었습니다");
                            deliveryUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(delivery_status.this, "택배함을 열지 못하였습니다");
                        }
                    });
        }
    };

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_make:  // 택배정보만들기 버튼 클릭했을때 동작 // 아두이노 통신해서 값 저장해야함
                    delivery_add();
                    break;
            }
        }
    };

    private void deliveryUpdate() {
        if(user != null) {
            CollectionReference collectionReference = db.collection("delivery");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()  // 시간순으로 내림차순 정렬하여 게시판에 보여줌
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Log.d("0", "deliveryUpdate 실행");  // 지우지 말것
                            if (task.isSuccessful()) {
                                deliveryList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // 현재 사용자 전화번호와 택배함 전화번호가 일치하는 것들만 보여줌
                                     if(currentusertelephone.equals(document.getData().get("telephone").toString())) {
                                       deliveryList.add(new DeliveryInfo(
                                                // db에 저장되어있는 게시판 작성의 값들을 postList에 저장
                                                document.getData().get("telephone").toString(),
                                                document.getData().get("boxnum").toString(),
                                                new Date(document.getDate("createdAt").getTime())
                                        ));
                                    }
                                }
                                deliveryAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    private void delivery_add() {
        // 난수
        int ran = (int)(Math.random()*10000)%10000;
        final String boxintelephone = "01012345678";       // 전화번호
        final String boxnum = String.valueOf(ran);    // 택배함 번호

        if (boxintelephone.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);

            deliveryInfo = new DeliveryInfo(boxintelephone, boxnum, new Date());

            // db 저장 함수
            db.collection("delivery").document(deliveryInfo.getBoxnum())
                    .set(deliveryInfo.getDeliveryInfo())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loaderLayout.setVisibility(View.GONE);
                            showToast(delivery_status.this, "택배가 도착했습니다");
                            deliveryUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loaderLayout.setVisibility(View.GONE);
                            showToast(delivery_status.this, "실패");
                        }
                    });
        }
    }

    private void get_CurrentUserTelephone() {
        if(user != null) {
            DocumentReference documentReference = db.collection("users").document(user.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d("0", "get_CurrentUserTelephone 실행");  // 지우지 말것
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                currentusertelephone = document.getData().get("telephone").toString();
                            }
                        }
                    }
                }
            });
        }
    }
}
