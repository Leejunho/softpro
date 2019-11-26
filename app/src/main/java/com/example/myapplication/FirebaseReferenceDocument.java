package com.example.myapplication;
public class FirebaseReferenceDocument { }


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
