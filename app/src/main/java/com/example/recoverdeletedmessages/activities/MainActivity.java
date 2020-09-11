package com.example.recoverdeletedmessages.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.Set;

public class MainActivity extends ActivityBase {
    private Button btnCreateNotification;
    private Button btnSettings;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
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


    private BottomNavigationView.OnNavigationItemSelectedListener onBottomItemClicked = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.acMain_btm_nav_btnWhatsapp: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.acMain_fragments_container, fragmentWhatsApp).commit();
                }
                break;
                case R.id.acMain_btm_nav_btnFacebook: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.acMain_fragments_container, fragmentFacebook).commit();
                }
                break;
                case R.id.acMain_btm_nav_btnInstagram: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.acMain_fragments_container, fragmentInstagram).commit();
                }
                break;
                case R.id.acMain_btm_nav_btnInsDefault: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.acMain_fragments_container, fragmentDefault).commit();
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


    private void settingsIntent() {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
        Log.d(TAG, "settingsIntent: Clicked ");
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
}