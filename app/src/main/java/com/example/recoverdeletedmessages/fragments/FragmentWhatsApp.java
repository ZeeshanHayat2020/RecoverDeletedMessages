package com.example.recoverdeletedmessages.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.activities.ActivityMessagesViewer;
import com.example.recoverdeletedmessages.adapters.AdapterMain;
import com.example.recoverdeletedmessages.constants.Constant;
import com.example.recoverdeletedmessages.constants.TableName;
import com.example.recoverdeletedmessages.database.MyDataBaseHelper;
import com.example.recoverdeletedmessages.interfaces.OnRecyclerItemClickeListener;
import com.example.recoverdeletedmessages.models.Users;

import java.util.ArrayList;

public class FragmentWhatsApp extends Fragment {

    private String TAG = "FragmentWhatsApp";
    private View view;
    private Context context;
    private RecyclerView recyclerView;
    private AdapterMain mAdapter;
    private ArrayList<Users> usersList = new ArrayList<>();

    private WhatsAppDataReceiver whatsAppDataReceiver;
    private MyDataBaseHelper myDataBaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_whatsapp, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();

        initViews();
        iniRecyclerView();
        buildRecyclerView();
        registerReceiver();
    }

    private void initViews() {
        myDataBaseHelper = new MyDataBaseHelper(getContext());
    }

    private void registerReceiver() {
        whatsAppDataReceiver = new WhatsAppDataReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_INTENT_FILTER_WHATS_APP_RECEIVER);
        getContext().registerReceiver(whatsAppDataReceiver, intentFilter);
    }

    private void iniRecyclerView() {
        recyclerView = view.findViewById(R.id.recycler_view_fr_whatsApp);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void buildRecyclerView() {
        usersList.addAll(myDataBaseHelper.getALLUsers(TableName.TABLE_NAME_USER_WHATS_APP));
        mAdapter = new AdapterMain(context, usersList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickeListener() {
            @Override
            public void onItemClicked(int position) {
                Intent intent = new Intent(context, ActivityMessagesViewer.class);
                intent.putExtra(Constant.KEY_INTENT_SELECTED_MAIN_ITEM_TITLE, usersList.get(position).getUserTitle());
                startActivity(intent);

            }
        });


    }

    public class WhatsAppDataReceiver extends BroadcastReceiver {
        @Override

        public void onReceive(Context context, Intent intent) {

            long id = intent.getLongExtra(Constant.KEY_INTENT_ID, 0);
            String title = intent.getStringExtra(Constant.KEY_INTENT_TITLE);
            String message = intent.getStringExtra(Constant.KEY_INTENT_MESSAGE);
            String largeIconUri = intent.getStringExtra(Constant.KEY_INTENT_LATG_ICON_URI);
            long timeStamp = intent.getLongExtra(Constant.KEY_INTENT_TIMESTAMP, 0);


            if (!myDataBaseHelper.isColumnExist(TableName.TABLE_NAME_USER_WHATS_APP, myDataBaseHelper.KEY_USER_TITLE, title)) {
                myDataBaseHelper.insertUsers(TableName.TABLE_NAME_USER_WHATS_APP, id, title, largeIconUri);
                Log.d(TAG, "onReceive: Column Does not Exists");
            }
//           myDataBaseHelper.insertUsers(TableName.TABLE_NAME_USER_WHATS_APP, id, title, largeIconUri);
            myDataBaseHelper.insertMessages(TableName.TABLE_NAME_MESSAGES_WHATS_APP, title, message, timeStamp);

            Log.d(TAG, "onReceive: Received Notification");
            for (int i = 0; i < myDataBaseHelper.getUsersCount(); i++) {
                myDataBaseHelper.deleteUsers(i);
                Log.d(TAG, "onReceive: Table Users Count :" + i);
            }
            for (int i = 0; i < myDataBaseHelper.getMessagesCount(TableName.TABLE_NAME_MESSAGES_WHATS_APP); i++) {

                Log.d(TAG, "onReceive: Table Messages Count:" + i);
            }

            buildRecyclerView();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(whatsAppDataReceiver);
        if (!usersList.isEmpty()) {
            usersList.clear();
        }
    }
}
