package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.DeliveryInfo;
import com.example.myapplication.MemberInfo;
import com.example.myapplication.PostInfo;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UserListInRoomFragment extends Fragment {
    private String roomID;
    private List<MemberInfo> userModels;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;

    public UserListInRoomFragment() {
    }

    public static final UserListInRoomFragment getInstance(String roomID, Map<String, MemberInfo> userModels) {
        List<MemberInfo> users = new ArrayList();
        for( Map.Entry<String, MemberInfo> elem : userModels.entrySet() ){
            users.add(elem.getValue());
        }

        UserListInRoomFragment f = new UserListInRoomFragment();
        f.setUserList(users);
        Bundle bdl = new Bundle();
        bdl.putString("roomID", roomID);
        f.setArguments(bdl);

        return f;
    }

    private void uploader(DocumentReference documentReference, final PostInfo postInfo) {
        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userlistinroom, container, false);
        if (getArguments() != null) {
            roomID = getArguments().getString("roomID");
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager( new LinearLayoutManager((inflater.getContext())));
        recyclerView.setAdapter(new UserFragmentRecyclerViewAdapter());

        // 채팅방 나가기 버튼을 눌렀을 때
        view.findViewById(R.id.Button_leave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                db = FirebaseFirestore.getInstance();
                db.collection("rooms").document(roomID)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // 삭제 성공
                                Context context = container.getContext();

                                //uploader(documentReference, new PostInfo(title, price, term, contents, user.getUid(), new Date(), viewCount, ""));

                                Toast.makeText(context, "채팅방을 삭제하였습니다", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //삭제 실패
                                Context context = container.getContext();
                                Toast.makeText(context, "채팅방을 삭제하지 못하였습니다", Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });
        // 택배열수있는 권한 주기 버튼 눌렀을 때
        view.findViewById(R.id.button_Authority).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                db = FirebaseFirestore.getInstance();
                DocumentReference documentReference = db.collection("rooms").document(roomID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            final DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {

                                    DocumentReference documentReference2 = db.collection("posts").document(document.getData().get("postID").toString());
                                    documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document2 = task.getResult();
                                                if (document2 != null) {
                                                    if (document2.exists()) {

                                                        DocumentReference documentReference3 = db.collection("users").document(document2.getData().get("publisher").toString());
                                                        documentReference3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document3 = task.getResult();
                                                                    if (document3 != null) {
                                                                        if (document3.exists()) {
                                                                            final DocumentReference documentReference_give = db.collection("delivery").document(document3.getData().get("boxnum").toString());
                                                                            DocumentReference documentReference4 = db.collection("delivery").document(document3.getData().get("boxnum").toString());
                                                                            documentReference4.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        DocumentSnapshot document4 = task.getResult();
                                                                                        if (document4 != null) {
                                                                                            if (document4.exists()) {
                                                                                                uploader_deliveryInfo(documentReference_give, new DeliveryInfo(document4.getData().get("telephone").toString(), document4.getData().get("boxnum").toString(), Date.valueOf(document4.getData().get("createdAt").toString()), document4.getData().get("consumertelphone").toString()));
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });

                                                    }
                                                }
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    }
                });
            }
        });

        // 택배 돌려줄 대체키 받기 눌렀을때
        view.findViewById(R.id.button_receivenum).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                db = FirebaseFirestore.getInstance();
                DocumentReference documentReference = db.collection("rooms").document(roomID);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {

                                    DocumentReference documentReference2 = db.collection("posts").document(document.getData().get("postID").toString());
                                    documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document2 = task.getResult();
                                                if (document2 != null) {
                                                    if (document2.exists()) {

                                                        DocumentReference documentReference3 = db.collection("users").document(document2.getData().get("publisher").toString());
                                                        documentReference3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    DocumentSnapshot document3 = task.getResult();
                                                                    if (document3 != null) {
                                                                        if (document3.exists()) {
                                                                            Context context = container.getContext();
                                                                            Toast.makeText(context, document3.getData().get("replacenum").toString(), Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });

                                                    }
                                                }
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    }
                });


            }
        });

        return view;
    }

    private void uploader_deliveryInfo(DocumentReference documentReference, final DeliveryInfo deliveryInfo) {
        documentReference.set(deliveryInfo.getDeliveryInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void setUserList(List<MemberInfo> users) {
        userModels = users;
    }

    class UserFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private StorageReference storageReference;
        final private RequestOptions requestOptions = new RequestOptions().transforms(new CenterCrop(), new RoundedCorners(90));

        public UserFragmentRecyclerViewAdapter() {
            storageReference  = FirebaseStorage.getInstance().getReference();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final MemberInfo user = userModels.get(position);
            CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            customViewHolder.user_name.setText(user.getNickname());
            //customViewHolder.user_msg.setText(user.getUsermsg());

            if (user.getPhotoUrl()==null) {
                Glide.with(getActivity()).load(R.drawable.user)
                        .apply(requestOptions)
                        .into(customViewHolder.user_photo);
            } else{
                Glide.with(getActivity())
                        .load(storageReference.child("userPhoto/"+user.getPhotoUrl()))
                        .apply(requestOptions)
                        .into(customViewHolder.user_photo);
            }
        }

        @Override
        public int getItemCount() {
            return userModels.size();
        }
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView user_photo;
        public TextView user_name;
        public TextView user_msg;

        public CustomViewHolder(View view) {
            super(view);
            user_photo = view.findViewById(R.id.user_photo);
            user_name = view.findViewById(R.id.user_name);
            user_msg = view.findViewById(R.id.user_msg);
            user_msg.setVisibility(View.GONE);
        }
    }
}
