package br.ufma.lsdi.digitalphenotyping.dataprocessor.util;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Voice;

public class AlarmAudio extends BroadcastReceiver {
    private final String TAG = AlarmAudio.class.getName();
    private Context context;
    private Voice voice = Voice.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            this.context = context;

            voice.getInstance().start();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    voice.getInstance().stop();
                }
            }, 60000);
        }catch (Exception e){
            Log.e(TAG,"#### Error: " +e.getMessage());
        }
    }

    /**
     *  Method responsible for configuring the alarm, it receives the context and frequency in milliseconds.
     * @param context application context.
     * @param frequency how often the alarm will be triggered, in milliseconds.
     */
    public void setAlarm(Context context, long frequency) {
        this.context = context;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmAudio.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), frequency * 60 * 1, pi); // Millisec * Second * Minute
    }

    public void desableAlarm(){
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmAudio.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
    }
}

//Backup
/*
*  Timestamp stamp = new Timestamp(System.currentTimeMillis());
   Date date = new Date(stamp.getTime());
   Log.i("ALARM", "#### 12: " + date);
*
* */