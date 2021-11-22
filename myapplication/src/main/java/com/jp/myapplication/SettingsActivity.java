package com.jp.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Settings");

        // Display the fragment as the main content.
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        SettingsFragment settingsFragment = new SettingsFragment();
        mFragmentTransaction.replace(R.id.nav_host_fragment_settings, settingsFragment);
        mFragmentTransaction.commit();
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG,"#### g1");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG,"#### g2");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"#### g3");
    }
}