package br.ufma.lsdi.digitalphenotyping.dataprocessor.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Mobility;

public class Alarm extends BroadcastReceiver {
    Message message;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent){
        this.context = context;
        readData("/storage/emulated/0/Download/gps_u00.csv");
    }


    public void setAlarm(Context context) {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 1, pi); // Millisec * Second * Minute
    }


    // Método ler arquivo CSV linha por linha
    public void readData(String path) {
        /*try{
            InputStream is = new FileInputStream(Environment.DIRECTORY_PICTURES + "/gps_u00.csv");
            BufferedReader reader = new BufferedReader( new InputStreamReader(is, StandardCharsets.UTF_8));
            String line = "";
            try {
                reader.readLine();
                if ((line = reader.readLine()) != null) {
                    Log.i("Alarm", line);

                    Message msg = new Message();
                    msg.setServiceValue(line);
                    message = msg;

                    conectMobility();
                }
                is.close();
            } catch (IOException e) {
                Log.wtf("Alarm", "Erro ao ler arquivo" + line, e);
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }*/
        File fileDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File fileToGet = new File(fileDirectory,"gps_u00.csv");
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileToGet));
            String line;
            while ((line = br.readLine()) !=null) {
                Log.i("Alarm","#### Values: " + line);
            }
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }


    public void conectMobility(){
        try{
            Intent intent = new Intent(this.context, Mobility.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
            else {
                this.context.startService(intent);
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