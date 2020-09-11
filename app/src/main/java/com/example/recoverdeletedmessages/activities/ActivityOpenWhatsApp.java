package com.example.recoverdeletedmessages.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recoverdeletedmessages.R;
import com.hbb20.CountryCodePicker;

import java.net.URLEncoder;


public class ActivityOpenWhatsApp extends AppCompatActivity {
    private String TAG = "ActivityOpenWhatsApp";
    private Toolbar toolbar;
    private TextView toolBarTitleTv;
    CountryCodePicker countryCodePicker;
    private Button btnOpenChat;
    private EditText etNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_whats_app);
        initViews();
        setUpToolBar();
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
                    etNumber.setHint("Enter valid number");
                }
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(ActivityOpenWhatsApp.this, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void setUpToolBar() {
        this.setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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
}