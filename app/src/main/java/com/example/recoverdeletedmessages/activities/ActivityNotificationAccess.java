package com.example.recoverdeletedmessages.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.database.MyPreferences;

public class ActivityNotificationAccess extends AppCompatActivity {


    private Button btnNotifyAccess;
    private MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_access);
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myPreferences.haveNotificationAccess()) {
            btnNotifyAccess.setText("Next");
        }
    }

    private void initViews() {
        myPreferences = new MyPreferences(this);
        btnNotifyAccess = (Button) findViewById(R.id.btn_acNotificationAccess);
        btnNotifyAccess.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!myPreferences.haveNotificationAccess()) {
                settingsIntent();
            } else {
                intentMainActivity();
            }
        }
    };

    void intentMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void settingsIntent() {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        startActivity(intent);
    }
}