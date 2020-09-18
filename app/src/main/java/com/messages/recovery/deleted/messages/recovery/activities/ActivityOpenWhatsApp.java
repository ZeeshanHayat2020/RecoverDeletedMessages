package com.messages.recovery.deleted.messages.recovery.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.hbb20.CountryCodePicker;
import com.messages.recovery.deleted.messages.recovery.R;

import java.net.URLEncoder;


public class ActivityOpenWhatsApp extends ActivityBase {
    private String TAG = "ActivityOpenWhatsApp";
    private Toolbar toolbar;
    private TextView toolBarTitleTv;
    CountryCodePicker countryCodePicker;
    private Button btnOpenChat;
    private EditText etNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpStatusBar(getResources().getColor(R.color.colorFragmentWhatsappStatusBar));
        setContentView(R.layout.activity_open_whats_app);
        if (haveNetworkConnection()) {
            requestBanner((FrameLayout) findViewById(R.id.acOpenWhatsApp_bannerContainer));
        }
        initViews();
        setUpToolBar();
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
        }
        return true;
    }

    public void setUpStatusBar(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolBar_acOpenWhatsApp);
        btnOpenChat = findViewById(R.id.btnOpenChat_acOpenWhatsApp);
        etNumber = findViewById(R.id.editText_acOpenWhatsApp);
        countryCodePicker = findViewById(R.id.countryPicker);
        countryCodePicker.registerCarrierNumberEditText(etNumber);


        btnOpenChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToContact();
                Log.d(TAG, "onClick: Full number" + countryCodePicker.getFullNumber());
            }
        });

    }

    private void sendToContact() {
        final String phoneNumber = countryCodePicker.getFullNumber();
        countryCodePicker.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                // your code
                if (isValidNumber) {
                    PackageManager packageManager = getPackageManager();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    try {
                        String url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + URLEncoder.encode("", "UTF-8");
                        i.setPackage("com.whatsapp");
                        i.setData(Uri.parse(url));
                        if (i.resolveActivity(packageManager) != null) {
                            startActivity(i);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        String message = getResources().getString(R.string.whatsAppNotInstalled);
                        showToast(message);
                    }
                } else {
                    etNumber.setText("");
                    etNumber.setHint("Enter valid number");
                    blinkEditText();
                    Log.d(TAG, "onValidityChanged: Phone NUMber" + phoneNumber);
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(ActivityOpenWhatsApp.this, message, Toast.LENGTH_SHORT).show();
    }

    private void setUpToolBar() {
        this.setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorFragmentWhatsappToolbar));
        toolBarTitleTv = (TextView) findViewById(R.id.toolBar_title_tv);
        toolBarTitleTv.setText("Open Whatsapp Chat");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void blinkEditText() {
        Animation animation;
        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.anim_blink);
        etNumber.startAnimation(animation);
    }

}