package com.messages.recovery.deleted.messages.recovery.activities;

import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.messages.recovery.deleted.messages.recovery.R;
import com.messages.recovery.deleted.messages.recovery.database.MyPreferences;

public class ActivityNotificationAccess extends ActivityBase {


    private Button btnNotifyAccess;
    private MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(this);
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
        changeButtonBackground(btnNotifyAccess, R.color.colorFragmentFbToolbar);
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
        startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    private void settingsIntent() {
        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}