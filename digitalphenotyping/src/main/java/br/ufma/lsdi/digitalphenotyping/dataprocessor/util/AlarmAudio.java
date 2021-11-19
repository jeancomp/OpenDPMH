package br.ufma.lsdi.digitalphenotyping.dataprocessor.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Voice;

public class AlarmAudio extends BroadcastReceiver {
    private long frequency = 1000;
    private Context context;
    private Voice voice;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ALARM", "#### 1");
        this.context = context;
        voice.startVoice();

        //stopVoice();
    }


    public void stopVoice(){
        final long tempoDeEspera = this.frequency * 60 * 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(tempoDeEspera);
                    voice.stopVoice();
                }
            }).start();
    }


    public void setAlarm(Context context, long frequency) {
        Log.i("ALARM", "#### 2");
        this.context = context;
        this.frequency = frequency;
        if(voice == null) {
            //voice = new Voice(this.context);
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Voice.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), this.frequency * 60 * 1, pi); // Millisec * Second * Minute
    }
}