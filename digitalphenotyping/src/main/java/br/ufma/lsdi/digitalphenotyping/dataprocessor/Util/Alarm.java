package br.ufma.lsdi.digitalphenotyping.dataprocessor.Util;

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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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
        Log.i("BroadcastReceiveAux","#### Alarm: reading studentlife dataset");
        try{
            InputStream is = new FileInputStream(path);
            BufferedReader reader = new BufferedReader( new InputStreamReader(is, StandardCharsets.UTF_8)
            );
            String line = "";
            try {
                reader.readLine();
                if ((line = reader.readLine()) != null) {
                    Log.i("BroadcastReceiveAux", line);

                    DigitalPhenotypeEvent digitalPhenotypeEvent = new DigitalPhenotypeEvent();
                    Situation situation = new Situation();
                    situation.setDescription(line);
                    digitalPhenotypeEvent.setSituation(situation);

                    Gson gson = new Gson();
                    String json = gson.toJson(digitalPhenotypeEvent);

                    Message msg = new Message();
                    msg.setServiceValue(json);
                    message = msg;

                    conectMobility();
                }
                is.close();
            } catch (IOException e) {
                Log.wtf("BroadcastReceiveAux", "Erro ao ler arquivo" + line, e);
                e.printStackTrace();
            }
        }
        catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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
            Log.e("BroadcastReceiveAux", "#### Error: " + e.getMessage());
        }
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("BroadcastReceiveAux","#### Connection service MainService");
            Mobility.LocalBinder binder = (Mobility.LocalBinder) iBinder;
            Mobility myService = binder.getService();

            myService.process(message);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("BroadcastReceiveAux","#### Disconnection service MainService");
        }
    };


    public class DigitalPhenotypeEvent{
        private String uid;
        private Situation situation;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private Coordinates location;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public Situation getSituation() {
            return situation;
        }

        public void setSituation(Situation situation) {
            this.situation = situation;
        }

        public LocalDateTime getStartDateTime() {
            return startDateTime;
        }

        public void setStartDateTime(LocalDateTime startDateTime) {
            this.startDateTime = startDateTime;
        }

        public LocalDateTime getEndDateTime() {
            return endDateTime;
        }

        public void setEndDateTime(LocalDateTime endDateTime) {
            this.endDateTime = endDateTime;
        }

        public Coordinates getLocation() {
            return location;
        }

        public void setLocation(Coordinates location) {
            this.location = location;
        }
    }


    public class Situation{
        private String label;  // e.g., Monday, Tuesday
        private String description;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }


    public class Coordinates{
        private double latitude;
        private double longitude;

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
    }
}