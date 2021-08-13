package br.ufma.lsdi.digitalphenotyping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedReceiver.class.getName();

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"#### BootCompletedReceiver");
        if (intent.getAction() == null) {
            return;
        }

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//            boolean startupAfterBoot = prefs.getBoolean("serenity_boot_startup", false);
//            if (startupAfterBoot) {
//                Intent i = new Intent(context, Bus.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
//            }

            Intent i = new Intent(context, MainService.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
