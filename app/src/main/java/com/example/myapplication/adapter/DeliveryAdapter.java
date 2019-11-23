package com.example.myapplication.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DeliveryInfo;
import com.example.myapplication.R;
import com.example.myapplication.listener.OnPostListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder> {
    private ArrayList<DeliveryInfo> mDataset;
    private Activity activity;
    private FirebaseFirestore db;
    private OnPostListener onPostListener;

    static class DeliveryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        DeliveryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public DeliveryAdapter(Activity activity, ArrayList<DeliveryInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
    }

    public void setOnPostListener(OnPostListener onPostListener) {
        this.onPostListener = onPostListener;
    }


    @NonNull
    @Override
    public DeliveryAdapter.DeliveryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery, parent, false);
        final DeliveryViewHolder deliveryViewHolder = new DeliveryViewHolder(cardView);

        // 택배함 게시판 열기 버튼을 클릭했을때
        cardView.findViewById(R.id.button_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostListener.onDelete(deliveryViewHolder.getAdapterPosition());
            }
        });

        return deliveryViewHolder;
    }



    @Override
    public void onBindViewHolder(DeliveryViewHolder holder, int position) {
        CardView cardView = holder.cardView;

        // 택배함 번호
        TextView TextView_title = cardView.findViewById(R.id.TextView_title);
        TextView_title.setText(mDataset.get(position).getBoxnum());

        // 택배가 도착한 날짜
        TextView textView_createdAt = cardView.findViewById(R.id.textView_createdAt);
        textView_createdAt.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm 도착", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}