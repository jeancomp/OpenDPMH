package com.jp.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedReceiver.class.getName();

    public void onReceive(Context context, Intent intent) {
        //SharedPreferences sharedpreferences;
        //sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final String MyPREFERENCES = "pref_main" ;
        final String Host = "hostKey";
        final String Port = "portKey";
        final String Clientid = "clientidKey";
        final String Compositionmode = "compositioModeKey";
        final String Frequency = "frequencyKey";

        String host = "not set";
        String port = "not set" ;
        String clientid = "not set" ;
        String compositionmode = "not set";
        String frequency = null;

        if (intent.getAction() == null) {
            return;
        }

        //if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) { }
        Log.i(TAG,"#### Ativando o framework");
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, MainActivity2.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            /*Intent serviceIntent = new Intent(context, DPManagerService.class);
            context.startService(serviceIntent);*/
        }
    }
}