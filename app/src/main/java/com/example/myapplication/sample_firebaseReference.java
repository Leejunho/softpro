package com.example.myapplication;
public class sample_firebaseReference { }


/*
firebase에서 자주쓰는 문서 참조용입니다.
*/



/*
컬렉션에서 여러 문서 가져오기
db.collection("cities")
        .whereEqualTo("capital", true)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });


 */

/*
users 정보 가져오기

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

 */

/*

int형 값 받을때
Integer.valueOf(document.getData().get("point").toString())
 */


/*
엑티비티 존재하는 스택 삭제
Intent intent = new Intent(activity_noticeBoard_selectUser.this, activity_noticeBoard_main.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

*/

/*
CollectionReference collectionReference = db.collection("posts");
            collectionReference.orderBy(name, Query.Direction.DESCENDING).get()  // 시간순으로 내림차순 정렬하여 게시판에 보여줌
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getData().get("title").toString(),
                                }
                            }
                        }
                    });
 */