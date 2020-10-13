package com.messages.recovery.deleted.messages.recovery.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.messages.recovery.deleted.messages.recovery.R;
import com.messages.recovery.deleted.messages.recovery.database.MyPreferences;
import com.google.android.gms.ads.AdListener;

public class ActivitySplash extends ActivityBase {

    private Handler handler;
    private Runnable runnable;
    private int loadAttempts;
    private MyPreferences myPreferences;
    private boolean isAdLeftApp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setStatusBarGradient(this);
        myPreferences = new MyPreferences(this);
        reqNewInterstitial(this);

    }

    public static void setStatusBarGradient(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable background = activity.getResources().getDrawable(R.drawable.backgrond_splash);
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorTransparent));
            window.setNavigationBarColor(activity.getResources().getColor(R.color.colorTransparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAdLeftApp = false;
        if (haveNetworkConnection()) {
            loadInterstitial();
        } else {
            launchWithDelay();
        }
    }

    private void launchWithDelay() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdLeftApp) {
                    launchLanguageActivity();
                }
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1500);
    }

    private void launchLanguageActivity() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                final Intent intent;
                if (myPreferences.isFirstTimeLaunch()) {
                    intent = new Intent(ActivitySplash.this, ActivityIntroSLides.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                } else {
                    if (!myPreferences.isPrivacyPolicyAccepted()) {
                        intent = new Intent(ActivitySplash.this, ActivityPrivacyPolicy.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    } else {
                        if (myPreferences.haveNotificationAccess()) {
                            intent = new Intent(ActivitySplash.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        } else {
                            intent = new Intent(ActivitySplash.this, ActivityNotificationAccess.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        }

                    }

                }
                startActivity(intent);
                finish();

                return null;
            }
        }.execute();

    }

    void loadInterstitial() {
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mInterstitialAd.isLoaded() && !isAdLeftApp) {
                    mInterstitialAd.show();
                } else {
                    if (!isAdLeftApp) {
                        launchWithDelay();
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }
                }
                super.onAdLoaded();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                loadAttempts++;
                if (loadAttempts >= 1) {
                    loadAttempts = 0;
                    launchLanguageActivity();
                } else {
                    reqNewInterstitial(ActivitySplash.this);
                    loadInterstitial();
                }
                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdClosed() {
                launchLanguageActivity();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAdLeftApp = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAdLeftApp = true;
    }
}