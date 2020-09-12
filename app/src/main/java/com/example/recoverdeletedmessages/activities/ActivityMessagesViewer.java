package com.example.recoverdeletedmessages.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Objects;

public class ActivityMessagesViewer extends AppCompatActivity {


    private Toolbar toolbar;
    private TextView toolBarTitleTv;
    private ProgressBar loadingBar;
    private CheckBox selectAllMenuItem;

    private String TAG = "ActivityMessagesViewer";
    private RelativeLayout recyclerRootView;
    private RecyclerView recyclerView;
    private AdapterMessageViewer mAdapter;
    private ArrayList<Messages> messagesList = new ArrayList<>();
    private MyDataBaseHelper myDataBaseHelper;
    private String userTitle;
    private String tableName;

    public boolean isContextualMenuOpen = false;
    public boolean isSelectAll = false;
    private ArrayList<Messages> multiSelectedItemList;
    private String selected;
    private String currentMessagesTitle = "";
    public int selectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_viewer);
        Intent intent = getIntent();
        if (intent != null) {
            userTitle = intent.getStringExtra(Constant.KEY_INTENT_SELECTED_MAIN_ITEM_TITLE);
            tableName = intent.getStringExtra(Constant.KEY_INTENT_SELECTED_TABLE_NAME);
            currentMessagesTitle = intent.getStringExtra(Constant.KEY_INTENT_SELECTED_MESSAGES_TITLE);
        }
        initViews();
        setUpToolBar();
        iniRecyclerView();
        buildRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ac_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acMain_menu_btnCreateNotification:
                return true;
            case R.id.acMain_btm_nav_btnSettings:
                return true;

            case R.id.menu_contextual_btnDelete:
                if (!multiSelectedItemList.isEmpty()) {
                    deleteMultipleDialog();
                } else {
                    Toast.makeText(this, "Please select any item first", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        if (isContextualMenuOpen) {
            closeContextualMenu();
        } else {
            super.onBackPressed();
        }
    }

    private void initViews() {
        myDataBaseHelper = new MyDataBaseHelper(this);
        recyclerRootView = (RelativeLayout) findViewById(R.id.rootView_recycler_ac_messageViewer);
        toolbar = (Toolbar) findViewById(R.id.toolBar_ac_messageViewer);
        loadingBar = (ProgressBar) findViewById(R.id.loadingBar_ac_messageViewer);
        loadingBar.setVisibility(View.INVISIBLE);
    }

    private void iniRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view_ac_messageViewer);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void setUpToolBar() {
        selected = getResources().getString(R.string.item_selected);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolBarTitleTv = (TextView) findViewById(R.id.toolBar_title_tv);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        updateToolBarTitle(currentMessagesTitle);
    }

    private void updateToolBarTitle(String title) {
        toolBarTitleTv.setText(title);
    }


    private void buildRecyclerView() {

        messagesList.addAll(myDataBaseHelper.getSelectedMessages(tableName, userTitle));
        mAdapter = new AdapterMessageViewer(this, messagesList, this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickeListener() {
            @Override
            public void onItemClicked(int position) {

            }

            @Override
            public void onItemLongClicked(int position) {
                openContextualMenu();
            }

            @Override
            public void onItemCheckBoxClicked(View view, int position) {
                try {
                    if (((CheckBox) view).isChecked()) {
                        multiSelectedItemList.add(messagesList.get(position));
                        String text = multiSelectedItemList.size() + selected;
                        updateToolBarTitle(text);
                        if (multiSelectedItemList.size() == messagesList.size()) {
                            selectAllMenuItem.setChecked(true);
                        }
                        Log.d(TAG, "onItemCheckBoxClicked For Check and Selection==== List Size:" + multiSelectedItemList.size() + "User List Size" + messagesList.size());
                    } else {
                        multiSelectedItemList.remove(messagesList.get(position));
                        String text = multiSelectedItemList.size() + selected;
                        updateToolBarTitle(text);
                        selectedIndex = position;
                        if (multiSelectedItemList.size() != messagesList.size()) {
                            selectAllMenuItem.setChecked(false);
                        }
                        Log.d(TAG, "onItemCheckBoxClicked For UnCheck and Selection==== List Size:" + multiSelectedItemList.size() + "User List Size" + messagesList.size());


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onItemCheckBoxClicked: " + e);
                }
            }
        });


    }


    private void openContextualMenu() {
        multiSelectedItemList = new ArrayList<>();
        isContextualMenuOpen = true;
        isSelectAll = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_ac_main_contextual);
        toolbar.setNavigationIcon(R.drawable.ic_cross);
        selectAllMenuItem = (CheckBox) toolbar.getMenu().findItem(R.id.menu_contextual_btnSelecAll).getActionView();
        selectAllMenuItem.setChecked(false);

        updateToolBarTitle("0" + selected);
        mAdapter.notifyDataSetChanged();
        selectAllMenuItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    selectAllItems(true);
                    isSelectAll = true;
                } else {
                    selectAllItems(false);
                    isSelectAll = false;
                }
            }
        });
    }

    private void selectAllItems(boolean isSelectAll) {
        if (isSelectAll) {
            if (!multiSelectedItemList.isEmpty()) {
                multiSelectedItemList.removeAll(messagesList);
                multiSelectedItemList.clear();
            }
            for (int i = 0; i < messagesList.size(); i++) {
                multiSelectedItemList.add(messagesList.get(i));
            }
            String text = multiSelectedItemList.size() + selected;
            updateToolBarTitle(text);
        } else {
           /* multiSelectedItemList.removeAll(messagesList);
            multiSelectedItemList.clear();*/
            for (int i = 0; i < messagesList.size(); i++) {
                multiSelectedItemList.remove(messagesList.get(i));
            }
            String text = multiSelectedItemList.size() + selected;
            updateToolBarTitle(text);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void closeContextualMenu() {
        multiSelectedItemList.removeAll(messagesList);
        multiSelectedItemList.clear();
        isContextualMenuOpen = false;
        isSelectAll = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_ac_main);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        updateToolBarTitle(currentMessagesTitle);
        mAdapter.notifyDataSetChanged();
    }


    private void deleteMultipleDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_alert)
                .setMessage(R.string.delete_user_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                recyclerRootView.setVisibility(View.INVISIBLE);
                                loadingBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {

                                for (int i = 0; i < multiSelectedItemList.size(); i++) {
                                    myDataBaseHelper.deleteMessages(tableName, multiSelectedItemList.get(i).getMessage());
                                    messagesList.remove(multiSelectedItemList.get(i));
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                closeContextualMenu();
                                loadingBar.setVisibility(View.GONE);
                                recyclerRootView.setVisibility(View.VISIBLE);
                            }
                        }.execute();


                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

}