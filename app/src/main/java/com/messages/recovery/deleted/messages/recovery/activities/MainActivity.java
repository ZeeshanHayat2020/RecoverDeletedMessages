package com.messages.recovery.deleted.messages.recovery.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.messages.recovery.deleted.messages.recovery.R;
import com.messages.recovery.deleted.messages.recovery.adapters.AdapterMainBottomView;
import com.messages.recovery.deleted.messages.recovery.constants.Constant;
import com.messages.recovery.deleted.messages.recovery.fragments.FragmentDefault;
import com.messages.recovery.deleted.messages.recovery.fragments.FragmentFacebook;
import com.messages.recovery.deleted.messages.recovery.fragments.FragmentInstagram;
import com.messages.recovery.deleted.messages.recovery.fragments.FragmentWhatsApp;
import com.messages.recovery.deleted.messages.recovery.interfaces.OnRecyclerItemClickeListener;
import com.messages.recovery.deleted.messages.recovery.models.ModelBottomView;
import com.messages.recovery.deleted.messages.recovery.services.NotificationService;
import com.google.android.gms.ads.AdListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActivityBase {

    private AppUpdateManager appUpdateManager;
    private String TAG = this.getClass().getName();
    private FrameLayout fragmentContainer;
    private CardView permissionHolder;
    private Button btnAllow;
    private FragmentWhatsApp fragmentWhatsApp;
    private FragmentFacebook fragmentFacebook;
    private FragmentInstagram fragmentInstagram;
    private FragmentDefault fragmentDefault;

    private RelativeLayout recyclerRootView;
    public RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    private AdapterMainBottomView mAdapter;
    private List<ModelBottomView> bottomViewList = new ArrayList<>();
    public int selectedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        reqNewInterstitial(this);
        setUpInAppUpdate();
        initViews();
        iniRecyclerView();
        setUpRecyclerView();
        checkForStopService(intent);

        if (hasStoragePermission()) {
            permissionHolder.setVisibility(View.INVISIBLE);
            fragmentContainer.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.acMain_fragments_container, fragmentWhatsApp).commit();
        } else {
            fragmentContainer.setVisibility(View.INVISIBLE);
            permissionHolder.setVisibility(View.VISIBLE);
        }

    }

    private void checkForStopService(Intent intent) {
        if (intent != null) {
            String stopRequest = intent.getStringExtra(Constant.KEY_INTENT_FROM_NOTIFICATION);
            Log.d(TAG, "onCreate: " + stopRequest);
            if (stopRequest != null) {
                if (stopRequest.equals("StopService")) {
                    showStopServiceDialog();
                }
            }
        }
    }

    private void showStopServiceDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.stopServiceHeading)
                .setMessage(R.string.stopServiceMessage)
                .setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener() {
                                @Override
                                public void onAdClosed() {
                                    super.onAdClosed();
                                    stopNotificationService();
                                }

                                @Override
                                public void onAdLeftApplication() {
                                    super.onAdLeftApplication();
                                    stopNotificationService();
                                }
                            });
                        } else {
                            reqNewInterstitial(MainActivity.this);
                            stopNotificationService();
                        }
                    }
                })
                .setNegativeButton("Not Now", null)
                .show();
    }

    protected void onResume() {
        super.onResume();
        if (haveNetworkConnection()) {
            checkForUpdate();
        }

    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.acMain_fragments_container);
        if (f instanceof FragmentWhatsApp) {
            super.onBackPressed();
            this.finish();
        } else {
            selectedIndex = 0;
            mAdapter.notifyDataSetChanged();
            setUpBottomBacgorund(R.drawable.ic_main_bottom_whatsapp);
            openFragment(fragmentWhatsApp);

        }

    }

    private void initViews() {
        fragmentContainer = (FrameLayout) findViewById(R.id.acMain_fragments_container);
        permissionHolder = (CardView) findViewById(R.id.cardView_permissionHolder_acMain);
        recyclerRootView = (RelativeLayout) findViewById(R.id.acMain_bottomRecycler_root);
        btnAllow = (Button) findViewById(R.id.btnAllow_acMain);
        fragmentWhatsApp = new FragmentWhatsApp();
        fragmentFacebook = new FragmentFacebook();
        fragmentInstagram = new FragmentInstagram();
        fragmentDefault = new FragmentDefault();
        btnAllow.setOnClickListener(onClickListener);
        changeButtonBackground(btnAllow, R.color.colorFragmentWhatsappToolbar);

    }

    private void changeButtonBackground(Button btnAccept, int colorId) {
        Drawable background = btnAccept.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(ContextCompat.getColor(this, colorId));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(ContextCompat.getColor(this, colorId));

        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(ContextCompat.getColor(this, colorId));
        }
    }

    private void iniRecyclerView() {
        recyclerView = findViewById(R.id.acMain_bottom_recycler);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        bottomViewList = new ArrayList<>();
    }

    private void setUpRecyclerView() {
        int[] imgIds = {
                R.drawable.ic_bottom_nav_whatsapp,
                R.drawable.ic_bottom_nav_fb,
                R.drawable.ic_bottom_nav_insta,
                R.drawable.ic_botttom_nav_sms

        };
        String[] title = {
                "Whatsapp",
                "Messenger",
                "Instagram",
                "Sms"
        };
        for (int i = 0; i < imgIds.length; i++) {
            bottomViewList.add(new ModelBottomView(imgIds[i], title[i]));
        }
        mAdapter = new AdapterMainBottomView(this, this, bottomViewList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickeListener() {
            @Override
            public void onItemClicked(int position) {
                selectedIndex = position;
                mAdapter.notifyDataSetChanged();
                switch (position) {
                    case 0: {
                        setUpBottomBacgorund(R.drawable.ic_main_bottom_whatsapp);
                        openFragment(fragmentWhatsApp);
                    }
                    break;
                    case 1: {
                        setUpBottomBacgorund(R.drawable.ic_main_bottom_fb);
                        openFragment(fragmentFacebook);
                    }
                    break;
                    case 2: {
                        setUpBottomBacgorund(R.drawable.ic_main_bottom_insta);
                        openFragment(fragmentInstagram);
                    }
                    break;
                    case 3: {
                        setUpBottomBacgorund(R.drawable.ic_main_bottom_sms);
                        openFragment(fragmentDefault);
                    }
                    break;
                }
            }

            @Override
            public void onItemLongClicked(int position) {

            }

            @Override
            public void onItemCheckBoxClicked(View view, int position) {

            }
        });
    }


    public void setUpBottomBacgorund(int source) {
        recyclerRootView.setBackgroundResource(source);

    }

    public void openFragment(final Fragment fragment) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    if (hasStoragePermission()) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.acMain_fragments_container, fragment)
                                .commit();
                    }
                }
            });
        } else {
            reqNewInterstitial(this);
            if (hasStoragePermission()) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.acMain_fragments_container, fragment)
                        .commit();
            }
        }

    }

    private void stopNotificationService() {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);

    }
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            checkStoragePermission();
        }
    };


    private void setUpInAppUpdate() {
        appUpdateManager = (AppUpdateManager) AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.IMMEDIATE,
                                // The current activity making the update request.
                                MainActivity.this,
                                // Include a request code to later monitor this update request.
                                Constant.REQUEST_CODE_FOR_IN_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void checkForUpdate() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                MainActivity.this,
                                Constant.REQUEST_CODE_FOR_IN_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
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
                permissionHolder.setVisibility(View.INVISIBLE);
                fragmentContainer.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.acMain_fragments_container, fragmentWhatsApp).commit();
            } else {
                fragmentContainer.setVisibility(View.INVISIBLE);
                permissionHolder.setVisibility(View.VISIBLE);
            }
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_FOR_IN_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, "Installation Failed!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }


}