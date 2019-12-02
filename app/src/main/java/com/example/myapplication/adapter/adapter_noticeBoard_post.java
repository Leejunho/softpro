package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.OnPostListener.OnPostListener;
import com.example.myapplication.R;
import com.example.myapplication.activity.activity_noticeBoard_post;
import com.example.myapplication.info.postInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class adapter_noticeBoard_post extends RecyclerView.Adapter<adapter_noticeBoard_post.PostViewHolder> {
    private ArrayList<postInfo> mDataset;
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private OnPostListener onPostListener;

    private String title;
    private int price;
    private String term;
    private String contents;
    private String publisher;
    private Date createdAt;
    private String id;
    private int viewCount;
    private String consumer;
    private String roomID;
    private String completepublisher;
    private String completeconsumer;
    private String complete;
    private int boxnum;
    private String checkpublisher;
    private String checkconsumer;

    static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        PostViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public adapter_noticeBoard_post(Activity activity, ArrayList<postInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
    }

    public void setOnPostListener(OnPostListener onPostListener) {
        this.onPostListener = onPostListener;
    }



    @NonNull
    @Override
    public adapter_noticeBoard_post.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_noticeboard_post, parent, false);
        final PostViewHolder postViewHolder = new PostViewHolder(cardView);
        // 게시판을 클릭했을 때 동작
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference documentReference_viewCountadd = db.collection("posts").document(mDataset.get(postViewHolder.getAdapterPosition()).getId());
                documentReference_viewCountadd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                if (document.exists()) {
                                    SetPostValues(document);

                                    uploader(documentReference_viewCountadd, new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount+1, consumer, roomID, completepublisher, completeconsumer, complete, boxnum, checkpublisher, checkconsumer));

                                    Intent intent = new Intent(activity, activity_noticeBoard_post.class);
                                    intent.putExtra("postInfo", new postInfo(title, price, term, contents, publisher, createdAt, id, viewCount+1, consumer, roomID, completepublisher, completeconsumer, complete, boxnum, checkpublisher, checkconsumer));
                                    activity.startActivity(intent);
                                }
                            }
                        }
                    }
                });
            }
        });

        return postViewHolder;
    }

    private void SetPostValues(DocumentSnapshot document) {
        title = document.getData().get("title").toString();
        price = Integer.valueOf(document.getData().get("price").toString());
        term = document.getData().get("term").toString();
        contents = document.getData().get("contents").toString();
        publisher = document.getData().get("publisher").toString();
        createdAt = document.getTimestamp("createdAt").toDate();
        if(document.getData().get("id") == null) id = document.getId();
        else id = document.getData().get("id").toString();
        viewCount = Integer.valueOf(document.getData().get("viewCount").toString());
        consumer = document.getData().get("consumer").toString();
        roomID = document.getData().get("roomID").toString();
        completepublisher = document.getData().get("completepublisher").toString();
        completeconsumer = document.getData().get("completeconsumer").toString();
        complete = document.getData().get("complete").toString();
        boxnum = Integer.valueOf(document.getData().get("boxnum").toString());
        checkpublisher = document.getData().get("checkpublisher").toString();
        checkconsumer = document.getData().get("checkconsumer").toString();
    }

    private void uploader(DocumentReference documentReference, final postInfo postInfo) {
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

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {
        // 게시판에 보여지는 cardview 속성값들 지정
        CardView cardView = holder.cardView;

        // 게시글 제목
        TextView textView_title = cardView.findViewById(R.id.textView_title);
        textView_title.setText(mDataset.get(position).getTitle());

        // 게시글 금액
        TextView textView_price = cardView.findViewById(R.id.textView_price);
        if(mDataset.get(position).getComplete().equals("YES")) {
            String str = "거래완료 " + mDataset.get(position).getPrice()+"원";
            SpannableStringBuilder ssb = new SpannableStringBuilder(str);
            ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#FA0000")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView_price.setText(ssb);
        }
        else {
            textView_price.setText(mDataset.get(position).getPrice()+"원");
        }

        // 게시글 내용
        TextView textView_contents = cardView.findViewById(R.id.textView_contents);
        textView_contents.setText(mDataset.get(position).getContents());

        // 게시글 올린 날짜
        TextView createdAtTextView = cardView.findViewById(R.id.textView_createdAt);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));

        // 게시글 조회수
        //TextView textView_viewCount = cardView.findViewById(R.id.textView_viewCount);
        //textView_viewCount.setText("조회수 " + String.valueOf(mDataset.get(position).getViewCount()));

        // 게시글 작성자 프로필 이미지
        final ImageView profileImageView = cardView.findViewById(R.id.imageView_profile);
        profileImageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
        DocumentReference documentReference = db.collection("users").document(mDataset.get(position).getPublisher());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            if(document.getData().get("photoUrl") != null) {
                                Glide.with(activity).
                                        load(String.valueOf(document.getData().get("photoUrl"))).
                                        centerCrop().
                                        override(50).
                                        into(profileImageView);
                            }
                        }
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}