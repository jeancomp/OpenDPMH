package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.util.Alarm;

public class Mobility extends DataProcessor {
    private static final String TAG = Mobility.class.getName();
    private Alarm alarm = new Alarm();

    @Override
    public void init(){
        try {
            Log.i(TAG, "#### Running processor Mobility");

            //Atualizar o nome da Message() para DigitalPhenotypeEvent
            setDataProcessorName("Mobility");

            List<String> listSensors = new ArrayList();
            listSensors.add("Tilt Detector");
            startSensor(listSensors);  //Retirar palavra "on"

            alarm.setAlarm(this);
        }catch (Exception e){
            Log.e(TAG, "Error: " + e.toString());
        }
    }


    @Override
    public void onSensorDataArrived(Message message){
        alarm.setAlarm(this);
        processedDataMessage(message);
    }


    @Override
    public void processedDataMessage(Message message){
        sendProcessedData(message);
    }


    @Override
    public void end(){ }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public final IBinder mBinder = new Mobility.LocalBinder();


    public class LocalBinder extends Binder {
        public Mobility getService() {
            return Mobility.this;
        }
    }
}
