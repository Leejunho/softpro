package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.PostInfo;
import com.example.myapplication.R;
import com.example.myapplication.activity.PostActivity;
import com.example.myapplication.listener.OnPostListener;
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
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private OnPostListener onPostListener;

    static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        PostViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public PostAdapter(Activity activity, ArrayList<PostInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
    }

    public void setOnPostListener(OnPostListener onPostListener) {
        this.onPostListener = onPostListener;
    }



    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final PostViewHolder postViewHolder = new PostViewHolder(cardView);
        // 게시판을 클릭했을 때 동작
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewCountadd(mDataset.get(postViewHolder.getAdapterPosition()));

                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra("postInfo", mDataset.get(postViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        return postViewHolder;
    }

    private void viewCountadd(PostInfo postInfo) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        final DocumentReference documentReference = postInfo == null ? db.collection("posts").document() : db.collection("posts").document(postInfo.getId());
        // db 저장 함수
        uploader(documentReference, new PostInfo(postInfo.getTitle(), postInfo.getPrice(), postInfo.getTerm(), postInfo.getContents(), postInfo.getPublisher(), postInfo.getCreatedAt(), postInfo.getViewCount()+1));
    }

    private void uploader(DocumentReference documentReference, final PostInfo postInfo) {
        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("!", "onSuccess: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("!", "onFailure: ");
                    }
                });
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        // 게시글 제목
        TextView textView_title = cardView.findViewById(R.id.textView_title);
        textView_title.setText(mDataset.get(position).getTitle());

        // 게시글 금액
        TextView textView_price = cardView.findViewById(R.id.textView_price);
        textView_price.setText(mDataset.get(position).getPrice()+"원");

        // 게시글 내용
        TextView textView_contents = cardView.findViewById(R.id.textView_contents);
        textView_contents.setText(mDataset.get(position).getContents());

        // 게시글 올린 날짜
        TextView createdAtTextView = cardView.findViewById(R.id.textView_createdAt);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));

        // 게시글 조회수
        TextView textView_viewCount = cardView.findViewById(R.id.textView_viewCount);
        textView_viewCount.setText("조회수 " + String.valueOf(mDataset.get(position).getViewCount()));

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
                                Glide.with(activity).load(String.valueOf(document.getData().get("photoUrl"))).centerCrop().override(50).into(profileImageView);
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