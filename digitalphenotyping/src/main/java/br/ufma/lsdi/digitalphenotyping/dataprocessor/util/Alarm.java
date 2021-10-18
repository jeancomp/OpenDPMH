package br.ufma.lsdi.digitalphenotyping.dataprocessor.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Mobility;

public class Alarm extends BroadcastReceiver {
    Context context;
    Send send;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        readData();
    }


    public void setAlarm(Context context) {
        send = new Send();
        send.getInstance().setContext(context);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 1, pi); // Millisec * Second * Minute
    }


    // Método ler arquivo CSV linha por linha
    public void readData() {
        try {
            InputStream is = context.getAssets().open("gps_u00.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line = "";
            try {
                reader.readLine();
                if ((line = reader.readLine()) != null) {
                    Log.i("Alarm", line);

                    Message msg = new Message();
                    msg.setServiceValue(line);

                    send.getInstance().conectMobility(msg);
                }
                is.close();
            } catch (IOException e) {
                Log.wtf("Alarm", "Erro ao ler arquivo" + line, e);
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

    class Send{
        Context context;
        Message message = new Message();
        private static Send instance = null;

        public Send(){}

        public static Send getInstance() {
            if (instance == null) {
                instance = new Send();
            }
            return instance;
        }

        public void setContext(Context c){
            context = c;
        }

        public void conectMobility(Message m){
            try{
                message = m;
                Intent intent = new Intent(context, Mobility.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                }
                else {
                    context.startService(intent);
                }
            }catch (Exception e){
                Log.e("Alarm", "#### Error: " + e.getMessage());
            }
        }


        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i("Alarm","#### Connection service MainService");
                Mobility.LocalBinder binder = (Mobility.LocalBinder) iBinder;
                Mobility myService = binder.getService();

                myService.onSensorDataArrived(message);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i("Alarm","#### Disconnection service MainService");
            }
        };
}