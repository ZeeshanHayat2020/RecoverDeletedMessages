package com.example.recoverdeletedmessages.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.recoverdeletedmessages.adapters.AdapterMain;
import com.example.recoverdeletedmessages.constants.Constant;
import com.example.recoverdeletedmessages.database.DatabaseHelper;
import com.example.recoverdeletedmessages.models.ModelMain;
import com.example.recoverdeletedmessages.models.TableSetter;

import java.util.ArrayList;
import java.util.List;

public class FragmentWhatsApp extends Fragment {

    private String TAG = "FragmentWhatsApp";
    private View view;
    private Context context;
    private RecyclerView recyclerView;
    private AdapterMain mAdapter;
    private List<ModelMain> modelMainList = new ArrayList<>();
    private WhatsAppDataReceiver whatsAppDataReceiver;
    private SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper databaseHelper;
    private TableSetter tableSetter;


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
        tableSetter = new TableSetter();
        tableSetter.setTABLE_NAME("testtable");
        databaseHelper = new DatabaseHelper(getContext());

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
        modelMainList.addAll(databaseHelper.getAllData());
        mAdapter = new AdapterMain(context, modelMainList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    public class WhatsAppDataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            long id = intent.getLongExtra(Constant.KEY_INTENT_ID, 0);
            String title = intent.getStringExtra(Constant.KEY_INTENT_TITLE);
            String message = intent.getStringExtra(Constant.KEY_INTENT_MESSAGE);
            long timeStamp = intent.getLongExtra(Constant.KEY_INTENT_TIMESTAMP, 0);
            databaseHelper.insertData(id, title, message, timeStamp);
//            Log.d(TAG, "WhatsAppDataReceiver: Success" + databaseHelper.getData("Sender"));
//            databaseHelper.getData("Sender");
//            buildRecyclerView();

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(whatsAppDataReceiver);
    }
}
