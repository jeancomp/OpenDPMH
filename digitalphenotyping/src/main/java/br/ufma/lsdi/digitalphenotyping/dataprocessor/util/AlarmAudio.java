package br.ufma.lsdi.digitalphenotyping.dataprocessor.util;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.sql.Timestamp;
import java.util.Date;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Voice;

public class AlarmAudio extends BroadcastReceiver {
    private final String TAG = AlarmAudio.class.getName();
    private Context context;
    private Voice voice = Voice.getInstance();

    @Override
    public void onReceive(Context context, Intent intent) {
        try{
            this.context = context;

            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            Date date = new Date(stamp.getTime());
            Log.i("ALARM", "#### 11: " + date);

            voice.getInstance().start();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    voice.getInstance().stop();
                    Timestamp stamp = new Timestamp(System.currentTimeMillis());
                    Date date = new Date(stamp.getTime());
                    Log.i("ALARM", "#### 12: " + date);
                }
            }, 60000);
        }catch (Exception e){
            Log.e(TAG,"#### Error: " +e.getMessage());
        }
    }

    public void setAlarm(Context context, long frequency) {
        Log.i("ALARM", "#### Alarme ativado");
        this.context = context;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmAudio.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), frequency * 60 * 1, pi); // Millisec * Second * Minute
    }

    public void desableAlarm(){
        voice.getInstance().stop();

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmAudio.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
    }
}