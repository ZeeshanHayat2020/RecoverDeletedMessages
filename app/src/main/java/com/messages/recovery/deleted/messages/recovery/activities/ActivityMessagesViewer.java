package com.messages.recovery.deleted.messages.recovery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EdgeEffect;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.messages.recovery.deleted.messages.recovery.R;
import com.messages.recovery.deleted.messages.recovery.adapters.AdapterMessageViewer;
import com.messages.recovery.deleted.messages.recovery.constants.Constant;
import com.messages.recovery.deleted.messages.recovery.database.MyDataBaseHelper;
import com.messages.recovery.deleted.messages.recovery.interfaces.OnRecyclerItemClickeListener;
import com.messages.recovery.deleted.messages.recovery.models.Messages;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class ActivityMessagesViewer extends ActivityBase {


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
    public String currentMessagesTitle = "";

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
        if (haveNetworkConnection()) {
            requestBanner((FrameLayout) findViewById(R.id.acMessageViewer_bannerContainer));
        }
        initViews();
        setUpToolBar();
        iniRecyclerView();
        setToolbarAndStatusBarColor(currentMessagesTitle);
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
            case R.id.acMain_menu_btnShareUs: {
                shareUs();

            }
            return true;
            case R.id.acMain_menu_btnRateUs: {
                rateUs();
            }
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
            setResult(RESULT_OK);
            finish();
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

    private void setToolbarColor(int color) {
        toolbar.setBackgroundColor(color);
        loadingBar.getIndeterminateDrawable().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
    }

    public void setUpStatusBar(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    private void setToolbarAndStatusBarColor(String currentMessagesTitle) {
        switch (currentMessagesTitle) {
            case "Whatsapp Messages": {
                setUpStatusBar(getResources().getColor(R.color.colorFragmentWhatsappStatusBar));
                setToolbarColor(getResources().getColor(R.color.colorFragmentWhatsappToolbar));

            }
            break;
            case "Instagram Messages": {
                setUpStatusBar(getResources().getColor(R.color.colorFragmentInstaStatusBar));
                setToolbarColor(getResources().getColor(R.color.colorFragmentInstaToolbar));
            }
            break;
            case "Facebook Messages": {
                setUpStatusBar(getResources().getColor(R.color.colorFragmentFbStatusBar));
                setToolbarColor(getResources().getColor(R.color.colorFragmentFbToolbar));
            }
            break;
            case "Sms": {
                setUpStatusBar(getResources().getColor(R.color.colorFragmentSmsStatusBar));
                setToolbarColor(getResources().getColor(R.color.colorFragmentSmsToolbar));
            }
            break;


        }
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
                       /* if (multiSelectedItemList.size() != messagesList.size()) {
                            selectAllMenuItem.setChecked(false);
                        }*/
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
            multiSelectedItemList.removeAll(messagesList);
            multiSelectedItemList.clear();
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