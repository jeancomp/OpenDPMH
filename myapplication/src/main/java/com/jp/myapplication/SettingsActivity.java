package com.jp.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = SettingsActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // below line is to change
        // the title of our action bar.
        getSupportActionBar().setTitle("Settings");

        // Display the fragment as the main content.
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager
                .beginTransaction();
        SettingsFragment settingsFragment = new SettingsFragment();
        mFragmentTransaction.replace(R.id.nav_host_fragment, settingsFragment);
        mFragmentTransaction.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}