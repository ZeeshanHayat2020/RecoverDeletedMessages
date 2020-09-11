package com.example.recoverdeletedmessages.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recoverdeletedmessages.R;


public class ActivityOpenWhatsApp extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolBarTitleTv;
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

        btnOpenChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = etNumber.getText().toString();

                Uri uri = Uri.parse("smsto:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.setPackage("com.whatsapp");
                startActivity(intent);

               /* try {
                    Intent sendIntent = new Intent("android.intent.action.MAIN");
                    sendIntent.setAction(Intent.ACTION_SENDTO);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                    sendIntent.putExtra("jid", phoneNumber + "@s.whatsapp.net");
                    sendIntent.setPackage("com.whatsapp");
                    startActivity(sendIntent);
                } catch (Exception e) {
                    Toast.makeText(ActivityOpenWhatsApp.this, "Error\n" + e.toString(), Toast.LENGTH_SHORT).show();
                }*/
            }
        });

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