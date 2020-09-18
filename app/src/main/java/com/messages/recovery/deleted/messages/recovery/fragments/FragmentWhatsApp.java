package com.messages.recovery.deleted.messages.recovery.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.messages.recovery.deleted.messages.recovery.R;
import com.messages.recovery.deleted.messages.recovery.activities.ActivityMessagesViewer;
import com.messages.recovery.deleted.messages.recovery.activities.ActivityOpenWhatsApp;
import com.messages.recovery.deleted.messages.recovery.activities.MainActivity;
import com.messages.recovery.deleted.messages.recovery.adapters.AdapterMain;
import com.messages.recovery.deleted.messages.recovery.constants.Constant;
import com.messages.recovery.deleted.messages.recovery.constants.TableName;
import com.messages.recovery.deleted.messages.recovery.database.MyDataBaseHelper;
import com.messages.recovery.deleted.messages.recovery.interfaces.OnRecyclerItemClickeListener;
import com.messages.recovery.deleted.messages.recovery.models.Messages;
import com.messages.recovery.deleted.messages.recovery.models.Users;
import com.google.android.gms.ads.AdListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentWhatsApp extends FragmentBase {

    private String TAG = "FragmentWhatsApp";
    private View view;
    private RelativeLayout emptyAnimViewRoot;
    private Toolbar toolbar;
    private TextView toolBarTitleTv;
    private ProgressBar loadingBar;
    private CheckBox selectAllMenuItem;
    private Context context;
    private RelativeLayout recyclerRootView;
    private RecyclerView recyclerView;
    private FloatingActionButton btnFab;
    private AdapterMain mAdapter;
    private ArrayList<Users> usersList = new ArrayList<>();
    private WhatsAppDataReceiver whatsAppDataReceiver;
    private MyDataBaseHelper myDataBaseHelper;
    public boolean isContextualMenuOpen = false;
    public boolean isSelectAll = false;
    private ArrayList<Users> multiSelectedItemList;
    private String selected;
    private String currentFragmentTitle = "Whats App";

    ReviewManager reviewManager;
    ReviewInfo reviewInfo = null;
    Handler handler;
    private int reviewCounter = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setUpStatusBar(getContext().getResources().getColor(R.color.colorFragmentWhatsappStatusBar));
        view = inflater.inflate(R.layout.fragment_whatsapp, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        if (haveNetworkConnection()) {
            requestBanner((FrameLayout) view.findViewById(R.id.fr_whatsApp_bannerContainer));
        }
        reqNewInterstitial(context);
        initViews();
        setUpToolBar();
        iniRecyclerView();
        registerReceiver();
        setUpInAppReview();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessageInBackgroundTask();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ac_main, menu);
        Log.d(TAG, "onCreateOptionsMenu: ");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acMain_menu_btnShareUs: {
                ((MainActivity) getActivity()).shareUs();

            }
            break;
            case R.id.acMain_menu_btnRateUs: {
                ((MainActivity) getActivity()).rateUs();
            }
            break;
            case R.id.menu_contextual_btnDelete:
                if (!multiSelectedItemList.isEmpty()) {
                    deleteMultipleDialog();
                } else {
                    Toast.makeText(context, "Please select any item first", Toast.LENGTH_SHORT).show();
                }
        }
        return true;
    }

    private void initViews() {
        myDataBaseHelper = new MyDataBaseHelper(getContext());
        handler = new Handler(Looper.getMainLooper());
        recyclerRootView = (RelativeLayout) view.findViewById(R.id.rootView_recycler_fr_whatsApp);
        toolbar = (Toolbar) view.findViewById(R.id.fr_whatsApp_toolbar);
        emptyAnimViewRoot = view.findViewById(R.id.emptyAnimView_root_fr_whatsApp);
        btnFab = view.findViewById(R.id.btnFab_fr_whatsApp);
        btnFab.setOnClickListener(onFabButtonClicked);
        loadingBar = (ProgressBar) view.findViewById(R.id.fr_whatsApp_loadingBar);
        loadingBar.setVisibility(View.INVISIBLE);
    }

    private View.OnClickListener onFabButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        startActivity(new Intent(context, ActivityOpenWhatsApp.class));
                    }

                });
            } else {
                startActivity(new Intent(context, ActivityOpenWhatsApp.class));
            }
        }
    };


    private void setUpToolBar() {
        selected = getResources().getString(R.string.item_selected);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorFragmentWhatsappToolbar));
        toolBarTitleTv = (TextView) view.findViewById(R.id.toolBar_title_tv);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isContextualMenuOpen) {
                    closeContextualMenu();
                } else {
                    ((MainActivity) getActivity()).onBackPressed();
                }
            }
        });
        updateToolBarTitle(currentFragmentTitle);
    }


    private void iniRecyclerView() {
        recyclerView = view.findViewById(R.id.recycler_view_fr_whatsApp);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void buildRecyclerView() {
        if (usersList.isEmpty()) {
            emptyAnimViewRoot.setVisibility(View.VISIBLE);
        } else {
            emptyAnimViewRoot.setVisibility(View.INVISIBLE);
        }
        mAdapter = new AdapterMain(context, this, null, null, null, usersList, Constant.ACTIVE_FRAGMENT_WHATS_APP);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickeListener() {
            @Override
            public void onItemClicked(int position) {
                updateTableMessages(position);
                Intent intent = new Intent(context, ActivityMessagesViewer.class);
                intent.putExtra(Constant.KEY_INTENT_SELECTED_MAIN_ITEM_TITLE, usersList.get(position).getUserTitle());
                intent.putExtra(Constant.KEY_INTENT_SELECTED_TABLE_NAME, TableName.TABLE_NAME_MESSAGES_WHATS_APP);
                intent.putExtra(Constant.KEY_INTENT_SELECTED_MESSAGES_TITLE, "Whatsapp Messages");
                startActivityForResult(intent, Constant.REQUEST_CODE_IN_APP_REVIEW);

            }

            @Override
            public void onItemLongClicked(int position) {
                openContextualMenu();
            }

            @Override
            public void onItemCheckBoxClicked(View view, int position) {
                try {
                    if (((CheckBox) view).isChecked()) {
                        multiSelectedItemList.add(usersList.get(position));
                        String text = multiSelectedItemList.size() + selected;
                        updateToolBarTitle(text);
                        if (multiSelectedItemList.size() == usersList.size()) {
                            selectAllMenuItem.setChecked(true);
                        } else {
                            selectAllMenuItem.setChecked(false);
                        }
                        Log.d(TAG, "onItemCheckBoxClicked For Check and Selection==== List Size:" + multiSelectedItemList.size() + "User List Size" + usersList.size());
                    } else {
                        multiSelectedItemList.remove(usersList.get(position));
                        String text = multiSelectedItemList.size() + selected;
                        updateToolBarTitle(text);
                        if (multiSelectedItemList.size() == usersList.size()) {
                            selectAllMenuItem.setChecked(false);
                        }
                        Log.d(TAG, "onItemCheckBoxClicked For Check and Selection==== List Size:" + multiSelectedItemList.size() + "User List Size" + usersList.size());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG, "onItemCheckBoxClicked: " + e);
                }
            }
        });

    }

    private void updateToolBarTitle(String title) {
        toolBarTitleTv.setText(title);
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
                multiSelectedItemList.removeAll(usersList);
                multiSelectedItemList.clear();
            }
            for (int i = 0; i < usersList.size(); i++) {
                multiSelectedItemList.add(usersList.get(i));
            }
            String text = multiSelectedItemList.size() + selected;
            updateToolBarTitle(text);
        } else {
            multiSelectedItemList.removeAll(usersList);
            multiSelectedItemList.clear();
            String text = multiSelectedItemList.size() + selected;
            updateToolBarTitle(text);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void closeContextualMenu() {
        multiSelectedItemList.removeAll(usersList);
        multiSelectedItemList.clear();
        isContextualMenuOpen = false;
        isSelectAll = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_ac_main);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        updateToolBarTitle(currentFragmentTitle);
        mAdapter.notifyDataSetChanged();
    }

    private void getMessageInBackgroundTask() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                recyclerRootView.setVisibility(View.INVISIBLE);
                loadingBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                if (!usersList.isEmpty()) {
                    usersList.clear();
                }
                usersList.addAll(myDataBaseHelper.getALLUsers(TableName.TABLE_NAME_USER_WHATS_APP));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                buildRecyclerView();
                loadingBar.setVisibility(View.INVISIBLE);
                recyclerRootView.setVisibility(View.VISIBLE);
            }
        }.execute();
    }


    private void updateTableMessages(int position) {
        Users currentUser = usersList.get(position);
        List<Messages> list = myDataBaseHelper.getSelectedMessages(TableName.TABLE_NAME_MESSAGES_WHATS_APP, currentUser.getUserTitle());
        Messages messages = list.get(list.size() - 1);
        messages.setReadStatus("Read");
        myDataBaseHelper.updateMessages(TableName.TABLE_NAME_MESSAGES_WHATS_APP, messages);
    }

    private void updateMessages() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                if (!usersList.isEmpty()) {
                    usersList.clear();
                }
                usersList.addAll(myDataBaseHelper.getALLUsers(TableName.TABLE_NAME_USER_WHATS_APP));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                buildRecyclerView();
            }
        }.execute();
    }

    private void registerReceiver() {
        whatsAppDataReceiver = new WhatsAppDataReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_INTENT_FILTER_WHATS_APP_RECEIVER);
        getContext().registerReceiver(whatsAppDataReceiver, intentFilter);
    }


    private void deleteMultipleDialog() {
        new AlertDialog.Builder(context)
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
                                    myDataBaseHelper.deleteUsers(TableName.TABLE_NAME_USER_WHATS_APP, multiSelectedItemList.get(i).getUserTitle());
                                    usersList.remove(multiSelectedItemList.get(i));
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

    public class WhatsAppDataReceiver extends BroadcastReceiver {
        @Override

        public void onReceive(Context context, Intent intent) {

           /* long id = intent.getLongExtra(Constant.KEY_INTENT_ID, 0);
            String title = intent.getStringExtra(Constant.KEY_INTENT_TITLE);
            String message = intent.getStringExtra(Constant.KEY_INTENT_MESSAGE);
            String largeIconUri = intent.getStringExtra(Constant.KEY_INTENT_LATG_ICON_URI);
            long timeStamp = intent.getLongExtra(Constant.KEY_INTENT_TIMESTAMP, 0);

            boolean recordExists = myDataBaseHelper.checkIsRecordExist(TableName.TABLE_NAME_USER_WHATS_APP, myDataBaseHelper.KEY_USER_TITLE, title);
            if (!recordExists) {
                myDataBaseHelper.insertUsers(TableName.TABLE_NAME_USER_WHATS_APP, id, title, largeIconUri);
            }

            if (!message.contains("new messages")) {
                myDataBaseHelper.insertMessages(TableName.TABLE_NAME_MESSAGES_WHATS_APP, title, message, timeStamp);
            }
*/
            Log.d(TAG, "onReceive: Received Notification");
            updateMessages();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(whatsAppDataReceiver);
        if (isContextualMenuOpen) {
            closeContextualMenu();
        }
        if (!usersList.isEmpty()) {
            usersList.clear();
        }
    }


    private void setUpInAppReview() {
//        reviewManager = ReviewManagerFactory.create(context);
        reviewManager = new FakeReviewManager(context);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(@NonNull Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    reviewInfo = task.getResult();
                } else {
                    // There was some problem, continue regardless of the result.
                    reviewInfo = null;
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }


 /*   @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_IN_APP_REVIEW) {
            if (resultCode == RESULT_OK) {
                reviewCounter++;
                if (reviewCounter > 0) {
                    reviewCounter = 0;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Task<Void> flow = reviewManager.launchReviewFlow(getActivity(), reviewInfo);
                            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d(TAG, "onComplete: Thanks for the feedback!");
                                }
                            });
                            flow.addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    Log.d(TAG, "onSuccess:  Reviewed successfully");
                                }
                            });
                            flow.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Log.d(TAG, "onFailed:  Reviewed Failed!");
                                }
                            });
                        }
                    }, 3000);
                }
            }
        }
    }*/
}
