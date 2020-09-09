package com.example.recoverdeletedmessages.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.adapters.AdapterMain;
import com.example.recoverdeletedmessages.adapters.AdapterMessageViewer;
import com.example.recoverdeletedmessages.constants.Constant;
import com.example.recoverdeletedmessages.constants.TableName;
import com.example.recoverdeletedmessages.database.MyDataBaseHelper;
import com.example.recoverdeletedmessages.interfaces.OnRecyclerItemClickeListener;
import com.example.recoverdeletedmessages.models.Messages;
import com.example.recoverdeletedmessages.models.Users;

import java.util.ArrayList;

public class ActivityMessagesViewer extends AppCompatActivity {


    private String TAG = "ActivityMessagesViewer";
    private RecyclerView recyclerView;
    private AdapterMessageViewer mAdapter;
    private ArrayList<Messages> messagesList = new ArrayList<>();
    private MyDataBaseHelper myDataBaseHelper;
    private String userTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_viewer);
        Intent intent = getIntent();
        userTitle = intent.getStringExtra(Constant.KEY_INTENT_SELECTED_MAIN_ITEM_TITLE);
        iniRecyclerView();
        buildRecyclerView();
    }


    private void iniRecyclerView() {
        myDataBaseHelper = new MyDataBaseHelper(this);
        recyclerView = findViewById(R.id.recycler_view_ac_msg_viewer);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void buildRecyclerView() {

        messagesList.addAll(myDataBaseHelper.getSelectedMessages(TableName.TABLE_NAME_MESSAGES_WHATS_APP, userTitle));
        mAdapter = new AdapterMessageViewer(this, messagesList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickeListener() {
            @Override
            public void onItemClicked(int position) {
//                Users users = myDataBaseHelper.getUsers(position);

            }
        });

        for (int i = 0; i < myDataBaseHelper.getMessagesCount("messages_whats_app"); i++) {

            Log.d(TAG, "onReceive: Table Messages Count:" + i);
            Log.d(TAG, "onReceive: Message:" + messagesList.get(i).getMessage());
            Log.d(TAG, "onReceive: Time:" + messagesList.get(i).getTimeStamp());
        }

    }
}