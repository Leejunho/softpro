package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import static com.example.myapplication.Util.showToast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class DeliverystatusActivity extends BasicActivity {
    private FirebaseUser user;
    private FirebaseFirestore db;
    private RelativeLayout loaderLayout;
    private DeliveryInfo deliveryInfo;
    private DeliveryAdapter deliveryAdapter;
    private ArrayList<DeliveryInfo> deliveryList;
    private String currentusertelephone = "nuuuull";
    private String currentuserreplacenum = "nuuuull";
    private String consumertelphone = "nuuuull";
    private RecyclerView recyclerView;



    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> mPairedDevices;
    List<String> mListPairedDevices;

    Button mBton;
    Button mBtoff;

    Handler mBluetoothHandler;
    ConnectedBluetoothThread mThreadConnectedBluetooth;
    BluetoothDevice mBluetoothDevice;
    BluetoothSocket mBluetoothSocket;


    final static int BT_REQUEST_ENABLE = 1;
    final static int BT_MESSAGE_READ = 2;
    final static int BT_CONNECTING_STATUS = 3;
    final static UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_status);

        loaderLayout = findViewById(R.id.loaderLayout);
        //findViewById(R.id.button_make).setOnClickListener(onClickListener);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        deliveryList = new ArrayList<>();

        deliveryAdapter = new DeliveryAdapter(DeliverystatusActivity.this, deliveryList);
        deliveryAdapter.setOnPostListener(onPostListener);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(DeliverystatusActivity.this, 1));  // 게시판 spanCount:1 줄씩 표시
        recyclerView.setAdapter(deliveryAdapter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        get_CurrentUserDatanum();
        deliveryUpdate();
        // task 의 실행순서가 스택처럼 쌓아놓고 마지막에 쌓인 task 작업부터 하나씩 완료해 나가기 때문에 전화번호를 받아오는 작업을 Update 함수 뒤에 작성하였음
        get_CurrentUserDatanum();


        //블루투스 연결
        if(mBluetoothAdapter == null) {
            showToast(DeliverystatusActivity.this, "블루투스를 사용할 수 없는 기기 입니다.");
            finish();
        }

        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            if (mPairedDevices.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("장치 선택");

                mListPairedDevices = new ArrayList<String>();
                for (BluetoothDevice device : mPairedDevices) {
                    mListPairedDevices.add(device.getName());
                    //mListPairedDevices.add(device.getName() + "\n" + device.getAddress());
                }
                final CharSequence[] items = mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);
                mListPairedDevices.toArray(new CharSequence[mListPairedDevices.size()]);

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        connectSelectedDevice(items[item].toString());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                showToast(DeliverystatusActivity.this, "페어링된 장치가 없습니다");
            }

        }
        else {
            showToast(DeliverystatusActivity.this, "블루투스가 비활성화 되어 있습니다.");
        }



        //블루투스 메시지 송신부분 (아두이노 시리얼 모니터를 통해 입력)  ex) 01041101575*09(휴대폰번호*택배함번호)
        mBluetoothHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == BT_MESSAGE_READ){
                    String readMessage = null;
                    String phoneNumber = null;
                    String deliveryNumber = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    int endofindex = readMessage.indexOf("*");
                    if(endofindex>10){
                        if(mThreadConnectedBluetooth != null) {
                            mThreadConnectedBluetooth.write("F"); //택배함 닫힘
                        }

                        //readMessage = readMessage.substring(0,11);
                        phoneNumber = readMessage.substring(0,11); //휴대폰번호
                        deliveryNumber = readMessage.substring(12,14); //택배함번호

                    }
                    //Firebase로 데이터 전송하는 부분

                    delivery_add(deliveryNumber, phoneNumber);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 현재 사용자 전화번호를 읽어옴
        deliveryUpdate();
        // task 의 실행순서가 스택처럼 쌓아놓고 마지막에 쌓인 task 작업부터 하나씩 완료해 나가기 때문에 전화번호를 받아오는 작업을 Update 함수 뒤에 작성하였음
        get_CurrentUserDatanum();
    }
    /*
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_make:  // 택배 정보 만들기 버튼
                    delivery_add2();
                    break;
            }
        }
    };
    */

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

                            if(mThreadConnectedBluetooth != null) {
                                mThreadConnectedBluetooth.write("N"); //택배함 열림
                            }

                            showToast(DeliverystatusActivity.this, "택배함을 열었습니다");
                            deliveryUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(DeliverystatusActivity.this, "택배함을 열지 못하였습니다");
                        }
                    });
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
                                    // 현재 사용자 전화번호와 택배함 전화번호가 일치하는 것들만 보여줌   또는 자신에게 권한이 있으면 보여줌
                                     if(currentusertelephone.equals(document.getData().get("telephone").toString()) || currentuserreplacenum.equals(document.getData().get("telephone").toString()) || consumertelphone.equals(document.getData().get("telephone").toString())) {
                                         if(document.getData().get("consumertelphone") != null) consumertelphone = document.getData().get("consumertelphone").toString();
                                         else consumertelphone = "";
                                         deliveryList.add(new DeliveryInfo(
                                                // db에 저장되어있는 게시판 작성의 값들을 postList에 저장
                                                document.getData().get("telephone").toString(),
                                                document.getData().get("boxnum").toString(),
                                                new Date(document.getDate("createdAt").getTime()),
                                                 consumertelphone
                                        ));
                                    }
                                }
                                deliveryAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }
    private void delivery_add2() {
        // 난수
        int ran = (int)(Math.random()*10000)%10000;
        final String boxintelephone = "01012345678";       // 전화번호
        final String boxnum = String.valueOf(ran);    // 택배함 번호

        if (boxintelephone.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);

            deliveryInfo = new DeliveryInfo(boxintelephone, boxnum, new Date(), "");

            // db 저장 함수
            db.collection("delivery").document(deliveryInfo.getBoxnum())
                    .set(deliveryInfo.getDeliveryInfo())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loaderLayout.setVisibility(View.GONE);
                            showToast(DeliverystatusActivity.this, "택배가 도착했습니다");
                            deliveryUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loaderLayout.setVisibility(View.GONE);
                            showToast(DeliverystatusActivity.this, "실패");
                        }
                    });
        }
    }

    private void delivery_add(String boxnum, String boxintelephone) {
        // 난수
        //int ran = (int)(Math.random()*10000)%10000;
        //final String boxintelephone = "01012345678";       // 전화번호
        //final String boxnum = String.valueOf(ran);    // 택배함 번호

        if (boxintelephone.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);

            deliveryInfo = new DeliveryInfo(boxintelephone, boxnum, new Date(), "");

            // db 저장 함수
            db.collection("delivery").document(deliveryInfo.getBoxnum())
                    .set(deliveryInfo.getDeliveryInfo())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            loaderLayout.setVisibility(View.GONE);
                            showToast(DeliverystatusActivity.this, "택배가 도착했습니다");
                            deliveryUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loaderLayout.setVisibility(View.GONE);
                            showToast(DeliverystatusActivity.this, "실패");
                        }
                    });
        }
    }

    private void get_CurrentUserDatanum() {
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
                                currentuserreplacenum = document.getData().get("replacenum").toString();
                                if(document.getData().get("consumertelphone") != null) consumertelphone = document.getData().get("consumertelphone").toString();
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BT_REQUEST_ENABLE:
                if (resultCode == RESULT_OK) { // 블루투스 활성화를 확인을 클릭하였다면
                    Toast.makeText(getApplicationContext(), "블루투스 활성화", Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_CANCELED) { // 블루투스 활성화를 취소를 클릭하였다면
                    Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void connectSelectedDevice(String selectedDeviceName) {
        for(BluetoothDevice tempDevice : mPairedDevices) {
            if (selectedDeviceName.equals(tempDevice.getName())) {
                mBluetoothDevice = tempDevice;

                break;
            }
        }
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(BT_UUID);
            mBluetoothSocket.connect();
            mThreadConnectedBluetooth = new ConnectedBluetoothThread(mBluetoothSocket);
            mThreadConnectedBluetooth.start();
            mBluetoothHandler.obtainMessage(BT_CONNECTING_STATUS, 1, -1).sendToTarget();

        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectedBluetoothThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedBluetoothThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
            Toast.makeText(getApplicationContext(), "장치연결됨.", Toast.LENGTH_LONG).show();
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if (bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mBluetoothHandler.obtainMessage(BT_MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }
        public void write(String str) {
            byte[] bytes = str.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "데이터 전송 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "소켓 해제 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

}
