package com.jp.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsFragment.class.getName();
    EditTextPreference hostPreference;
    EditTextPreference portPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_main);
        //setPreferencesFromResource(R.xml.pref_main, rootKey);

        hostPreference = (EditTextPreference) findPreference("host");
        portPreference = (EditTextPreference) findPreference("port");
        SharedPreferences getPref = getContext().getSharedPreferences("pref_main", Context.MODE_PRIVATE);
        Log.i(TAG,"#### HOST PREFERENCE: " + hostPreference.getText());
        Log.i(TAG,"#### PORT PREFERENCE: " + portPreference.getText());
    }
}
