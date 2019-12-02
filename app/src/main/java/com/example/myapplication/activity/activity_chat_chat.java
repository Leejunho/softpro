package com.example.myapplication.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.myapplication.R;
import com.example.myapplication.fragment.fragment_chat_chat;
import com.example.myapplication.fragment.fragment_chat_userListInRoom;


public class activity_chat_chat extends activity_main_basic {
    private DrawerLayout drawerLayout;
    private fragment_chat_chat chatFragment;
    private fragment_chat_userListInRoom userListInRoomFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        String toUid = getIntent().getStringExtra("toUid");
        final String roomID = getIntent().getStringExtra("roomID");
        String roomTitle = getIntent().getStringExtra("roomTitle");
        if (roomTitle!=null) {
            actionBar.setTitle(roomTitle);
        }

        // left drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        findViewById(R.id.rightMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    if (userListInRoomFragment==null) {
                        userListInRoomFragment = fragment_chat_userListInRoom.getInstance(roomID, chatFragment.getUserList());
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.drawerFragment, userListInRoomFragment)
                                .commit();
                    }
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });
        // chatting area
        chatFragment = fragment_chat_chat.getInstance(toUid, roomID);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, chatFragment )
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        chatFragment.backPressed();
        finish();
    }

}
