package com.example.recoverdeletedmessages.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.recoverdeletedmessages.R;
import com.example.recoverdeletedmessages.constants.Constant;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class FragmentBase extends Fragment {


    @Override
    public void onStart() {
        super.onStart();
//        checkStoragePermission();
    }

    public void getBackToWhatsAppFragment() {
        Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.acMain_fragments_container);
        if (f instanceof FragmentWhatsApp) {
            getActivity().finish();
        } else {
           /* FragmentWhatsApp fragmentWhatsApp = new FragmentWhatsApp();
            BottomNavigationView navigationView = getActivity().findViewById(R.id.bottom_navigation_tab);
            navigationView.getMenu().getItem(0).setChecked(true);
            openFragment(fragmentWhatsApp);*/
        }

    }

    private void openFragment(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.acMain_fragments_container, fragment)
                .commit();
    }

    public void setUpStatusBar(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    public void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    Constant.REQUEST_STORAGE_PERMISSION);
            return;
        }
    }

    public boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else
                return true;
        } else return true;
    }
}
