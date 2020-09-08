package com.example.recoverdeletedmessages.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {
    private Button btnCreateNotification;
    private Button btnSettings;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    private String TAG = this.getClass().getName();


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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container, fragmentWhatsApp).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ac_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acMain_menu_btnCreateNotification: {
                startNotificationService();
            }
            break;
            case R.id.acMain_btm_nav_btnSettings: {
                settingsIntent();
            }
            break;
        }
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onBottomItemClicked = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.acMain_btm_nav_btnWhatsapp: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container, fragmentWhatsApp).commit();
                }
                break;
                case R.id.acMain_btm_nav_btnFacebook: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container, fragmentFacebook).commit();
                }
                break;
                case R.id.acMain_btm_nav_btnInstagram: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container, fragmentInstagram).commit();
                }
                break;
                case R.id.acMain_btm_nav_btnInsDefault: {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container, fragmentDefault).commit();
                }
                break;
            }
            return true;
        }
    };


    private void initViews() {
        bottom_Nav = findViewById(R.id.bottom_navigation_tab);
        bottom_Nav.setOnNavigationItemSelectedListener(onBottomItemClicked);
        fragmentWhatsApp = new FragmentWhatsApp();
        fragmentFacebook = new FragmentFacebook();
        fragmentInstagram = new FragmentInstagram();
        fragmentDefault = new FragmentDefault();

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


}