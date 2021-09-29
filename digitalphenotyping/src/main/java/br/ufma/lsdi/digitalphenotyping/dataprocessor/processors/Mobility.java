package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.Util.Alarm;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;

public class Mobility extends DataProcessor {
    private static final String TAG = Mobility.class.getName();
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    Alarm alarm = new Alarm();

    @Override
    public void init(){
        try {
            Log.i(TAG, "#### Running processor Mobility");

            setNameProcessor("Mobility");

            List<String> listSensorsUtilities = new ArrayList();
            listSensorsUtilities.add("Tilt Detector");
            onStartSensor(listSensorsUtilities);

            //alarm.setAlarm(this);
        }catch (Exception e){
            Log.e(TAG, "Error: " + e.toString());
        }
    }


    @Override
    public void process(Message message){
        //alarm.setAlarm(this);

        Object[] valor = message.getServiceValue();
        String mensagemRecebida = StringUtils.join(valor, ", ");
        String[] separated = mensagemRecebida.split(",");

        Object[] finalValor = {getNameProcessor(),mensagemRecebida};
        Log.i(TAG,"#### VALOR: " + finalValor[0] + ", " + String.valueOf(finalValor[1]));

        Message msg = new Message();
        msg.setServiceName(Topics.INFERENCE_TOPIC.toString());
        msg.setServiceValue(finalValor);
        msg.setTopic(Topics.INFERENCE_TOPIC.toString());
        Log.i(TAG,"#### MENSAGEM: " + msg);

        publishInference(msg);
    }


    @Override
    public void end(){
        onStopSensor("Tilt Detector");
    }


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
