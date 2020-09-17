package com.example.recoverdeletedmessages.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.adapters.AdapterIntroSlidesViewPager;
import com.example.recoverdeletedmessages.database.MyPreferences;

import java.util.Objects;


public class ActivityIntroSLides extends ActivityBase {


    private Toolbar toolbar;
    private TextView toolBarTitleTv;
    private PreferenceManager preferenceManager;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] screensList;
    private Button btnSkip, btnNext;
    private ViewPager viewPager;
    private AdapterIntroSlidesViewPager pagerAdapter;
    private MyPreferences myPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarGradient(this);
        setContentView(R.layout.activity_intro_slides);
        myPreferences = new MyPreferences(this);
        initViews();
        setUpViewPager();
        addBottomDots(0);

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
        viewPager = (ViewPager) findViewById(R.id.acIntroSlide_viewPager);
        dotsLayout = (LinearLayout) findViewById(R.id.acIntroSlides_barsRoot);
        btnSkip = (Button) findViewById(R.id.acIntroSlides_btnSkip);
        btnNext = (Button) findViewById(R.id.acIntroSlides_btnNext);
        viewPager.setOnPageChangeListener(viewPagerPageChangeListener);
        btnNext.setOnClickListener(onButtonsClicked);
        btnSkip.setOnClickListener(onButtonsClicked);
        changeButtonBackground();
    }

    private void changeButtonBackground() {
        Drawable background = btnNext.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(ContextCompat.getColor(this, R.color.colorIntroSlideBtn));
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(ContextCompat.getColor(this, R.color.colorIntroSlideBtn));

        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(ContextCompat.getColor(this, R.color.colorIntroSlideBtn));
        }
    }

    private void setUpViewPager() {
        screensList = new int[]{
                R.layout.layout_introslide_one,
                R.layout.layout_introslide_two,
                R.layout.layout_introslide_three
        };
        pagerAdapter = new AdapterIntroSlidesViewPager(this, screensList);
        viewPager.setAdapter(pagerAdapter);
    }

    private void launchPrivacyPolicyScreen(boolean isFirstLaunch) {
        myPreferences.setFirstTimeLaunch(isFirstLaunch);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Intent intent;
                if (!myPreferences.isPrivacyPolicyAccepted()) {
                    intent = new Intent(ActivityIntroSLides.this, ActivityPrivacyPolicy.class);
                } else {
                    if (!myPreferences.haveNotificationAccess()) {
                        intent = new Intent(ActivityIntroSLides.this, ActivityNotificationAccess.class);
                    } else {
                        intent = new Intent(ActivityIntroSLides.this, MainActivity.class);
                    }
                }
                startActivity(intent);
                finish();
                return null;
            }
        }.execute();

    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[screensList.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(40);
            dots[i].setTextColor(colorsInactive[i]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    View.OnClickListener onButtonsClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.acIntroSlides_btnNext: {
                    int current = getItem(+1);
                    if (current < screensList.length) {
                        viewPager.setCurrentItem(current);
                    } else {
                        launchPrivacyPolicyScreen(false);
                    }

                }
                break;
                case R.id.acIntroSlides_btnSkip: {

                    launchPrivacyPolicyScreen(true);
                }
                break;
            }
        }
    };

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == screensList.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.INVISIBLE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


}