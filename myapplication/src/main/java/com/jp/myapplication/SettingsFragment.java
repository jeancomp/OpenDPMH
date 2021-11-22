package com.jp.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String TAG = SettingsFragment.class.getName();

    private boolean flag_on_off;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor = null;
    private static final String MyPREFERENCES = "pref_main" ;
    private static final String Host = "hostKey";
    private static final String Port = "portKey";
    private static final String Compositionmode = "compositioModeKey";
    private static final String Frequency = "frequencyKey";

    private EditTextPreference hostPreference;
    private EditTextPreference portPreference;
    private Preference compositionModePreference;
    private Preference frequencyPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_main);
        //setPreferencesFromResource(R.xml.pref_main, rootKey);

        //flag_on_off = getIntent().getBooleanExtra("flag_on_off",flag_on_off);
        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        hostPreference = (EditTextPreference) findPreference("host");
        portPreference = (EditTextPreference) findPreference("port");
        compositionModePreference = (ListPreference) findPreference("compositionmode");
        frequencyPreference = (ListPreference) findPreference("frequency");

        hostPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //hostPreference.setSummary(hostPreference.getText());
                hostPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
                editor.putString(Host, (String) hostPreference.getSummary());
                editor.commit();
                return true;
            }
        });

        portPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //portPreference.setSummary(portPreference.getText());
                portPreference.setSummaryProvider(EditTextPreference.SimpleSummaryProvider.getInstance());
                editor.putString(Port, (String) portPreference.getSummary());
                editor.commit();
                return true;
            }
        });

        compositionModePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                compositionModePreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
                editor.putString(Compositionmode, (String) compositionModePreference.getSummary());
                editor.commit();
                return true;
            }
        });

        frequencyPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                frequencyPreference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());
                editor.putString(Frequency, (String) frequencyPreference.getSummary());
                editor.commit();
                return true;
            }
        });

        /*SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Host, n);
        editor.putString(Port, ph);
        editor.putString(CompositionMode, e);
        editor.commit();*/
    }
}
