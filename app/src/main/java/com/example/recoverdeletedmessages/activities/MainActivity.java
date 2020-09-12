package com.example.recoverdeletedmessages.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.constants.Constant;
import com.example.recoverdeletedmessages.fragments.FragmentDefault;
import com.example.recoverdeletedmessages.fragments.FragmentFacebook;
import com.example.recoverdeletedmessages.fragments.FragmentInstagram;
import com.example.recoverdeletedmessages.fragments.FragmentWhatsApp;
import com.example.recoverdeletedmessages.interfaces.MyListener;
import com.example.recoverdeletedmessages.services.NotificationService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import java.util.Set;

public class MainActivity extends ActivityBase {

    private AppUpdateManager appUpdateManager;
    private String TAG = this.getClass().getName();
    private FrameLayout fragmentContainer;
    private CardView permissionHolder;
    private Button btnAllow;
    private BottomNavigationView bottom_Nav;
    private FragmentWhatsApp fragmentWhatsApp;
    private FragmentFacebook fragmentFacebook;
    private FragmentInstagram fragmentInstagram;
    private FragmentDefault fragmentDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isNotificationServiceRunning()) {
            startNotificationService();
        }
        setUpInAppUpdate();
        initViews();
        if (hasStoragePermission()) {
            permissionHolder.setVisibility(View.INVISIBLE);
            fragmentContainer.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.acMain_fragments_container, fragmentWhatsApp).commit();
        } else {
            fragmentContainer.setVisibility(View.INVISIBLE);
            permissionHolder.setVisibility(View.VISIBLE);
        }


    }


    protected void onResume() {
        super.onResume();
        if (haveNetworkConnection()) {
            checkForUpdate();
        }
    }




    private BottomNavigationView.OnNavigationItemSelectedListener onBottomItemClicked = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.acMain_btm_nav_btnWhatsapp: {
                    if (hasStoragePermission()) {
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.acMain_fragments_container, fragmentWhatsApp)
                                .commit();
                    }
                }
                break;
                case R.id.acMain_btm_nav_btnFacebook: {
                    if (hasStoragePermission()) {
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.acMain_fragments_container, fragmentFacebook)
                                .commit();
                    }
                }
                break;
                case R.id.acMain_btm_nav_btnInstagram: {
                    if (hasStoragePermission()) {
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.acMain_fragments_container, fragmentInstagram)
                                .commit();
                    }
                }
                break;
                case R.id.acMain_btm_nav_btnInsDefault: {
                    if (hasStoragePermission()) {
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .replace(R.id.acMain_fragments_container, fragmentDefault)
                                .commit();
                    }
                }
                break;
            }
            return true;
        }
    };


    private void initViews() {
        fragmentContainer = (FrameLayout) findViewById(R.id.acMain_fragments_container);
        permissionHolder = (CardView) findViewById(R.id.cardView_permissionHolder_acMain);
        btnAllow = (Button) findViewById(R.id.btnAllow_acMain);
        bottom_Nav = findViewById(R.id.bottom_navigation_tab);
        bottom_Nav.setOnNavigationItemSelectedListener(onBottomItemClicked);
        fragmentWhatsApp = new FragmentWhatsApp();
        fragmentFacebook = new FragmentFacebook();
        fragmentInstagram = new FragmentInstagram();
        fragmentDefault = new FragmentDefault();
        btnAllow.setOnClickListener(onClickListener);

    }

    public boolean isNotificationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NotificationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startNotificationService() {
        Intent serviceIntent = new Intent(this, NotificationService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
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