package com.messages.recovery.deleted.messages.recovery.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.messages.recovery.deleted.messages.recovery.R;
import com.messages.recovery.deleted.messages.recovery.database.MyPreferences;


public class ActivityPrivacyPolicy extends AppCompatActivity {

    private TextView tvPPDesc;

    private Button btnAccept;
    private Button bntDecline;
    private MyPreferences myPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(this);
        setContentView(R.layout.activity_privacy_policy);
        myPreferences = new MyPreferences(this);
        initViews();
        setPrivacyPolicyText();

    }

    public static void setStatusBarGradient(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorOffWhite));
            window.setNavigationBarColor(activity.getResources().getColor(R.color.colorOffWhite));
        }
    }

    private void initViews() {
        tvPPDesc = (TextView) findViewById(R.id.acPP_DescTV);

        btnAccept = findViewById(R.id.acPP_btnAccept);
        bntDecline = findViewById(R.id.acPP_btnDecline);
        btnAccept.setOnClickListener(onClickListener);
        bntDecline.setOnClickListener(onClickListener);
        changeButtonBackground(btnAccept, R.color.colorBtnPPAloww);
        changeButtonBackground(bntDecline, R.color.colorBtnPPDecline);
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

    private void setPrivacyPolicyText() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvPPDesc.setText(Html.fromHtml(getString(R.string.privacy_policy_text), Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvPPDesc.setText(Html.fromHtml(getString(R.string.privacy_policy_text)));
        }


    }


    private GradientDrawable getGradient(int color1, int color2) {
        int[] colors = {Integer.parseInt(String.valueOf(color1)),
                Integer.parseInt(String.valueOf(color2))
        };
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT,
                colors);
        float radius = getResources().getDimension(R.dimen._6sdp);
        gd.setCornerRadius(radius);
        return gd;
    }

    private void startNotificationAccessActivity() {
        Intent intent;
        if (!myPreferences.haveNotificationAccess()) {
            intent = new Intent(this, ActivityNotificationAccess.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        this.finish();
    }

    private void setAcceptance(boolean isAccept) {
        myPreferences.setPrivacyPolicyAcceptance(isAccept);
        if (isAccept) {
            startNotificationAccessActivity();
        } else {
            this.finish();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.acPP_btnAccept: {
                    setAcceptance(true);
                }
                break;
                case R.id.acPP_btnDecline: {
                    setAcceptance(false);
                }
                break;
            }
        }
    };


}