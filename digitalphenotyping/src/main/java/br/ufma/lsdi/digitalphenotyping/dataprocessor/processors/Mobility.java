package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Situation;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.util.Alarm;

public class Mobility extends DataProcessor {
    private static final String TAG = Mobility.class.getName();
    private Alarm alarm = new Alarm();

    @Override
    public void init(){
        try {
            Log.i(TAG, "#### Running processor Mobility");

            setDataProcessorName("Mobility");

            /*List<String> listSensors = new ArrayList();
            listSensors.add("Tilt Detector");
            startSensor(listSensors);*/

            alarm.setAlarm(this);
        }catch (Exception e){
            Log.e(TAG, "Error: " + e.toString());
        }
    }


    @Override
    public void onSensorDataArrived(Message message){
        alarm.setAlarm(this);

        //Add processor name
        Object[] valor1 = message.getServiceValue();
        String mensagemRecebida1 = StringUtils.join(valor1, ", ");
        String[] finalValor1 = {getDataProcessorName(),mensagemRecebida1};

        message.setAvailableAttributes(11);
        String[] finalValor2 = {"Data Processor Name","Time","Provider","Network_type","Accuracy","Latitude","Longitude","Altitude","Bearing","Speed","TravelState"};

        message.setAvailableAttributesList(finalValor2);
        message.setServiceValue(finalValor1);

        inferencePhenotypingEvent(message);
    }


    @Override
    public void inferencePhenotypingEvent(Message message){
        Log.i(TAG,"#### MSG ORIGINAL MOBILITY: " + message);
        DigitalPhenotypeEvent digitalPhenotypeEvent = new DigitalPhenotypeEvent();
        digitalPhenotypeEvent.setDataProcessorName(getDataProcessorName());
        digitalPhenotypeEvent.setUid(CDDL.getInstance().getConnection().getClientId());

        Object[] valor1 = message.getServiceValue();
        String mensagemRecebida1 = StringUtils.join(valor1, ", ");
        String[] listValues = mensagemRecebida1.split(",");

        Object[] valor2 = message.getAvailableAttributesList();
        String mensagemRecebida2 = StringUtils.join(valor2, ", ");
        String[] listAttrutes = mensagemRecebida2.split(",");

        if(!listAttrutes[1].isEmpty() && !listValues[1].isEmpty()) {
            digitalPhenotypeEvent.setAttributes(listAttrutes[1], listValues[1], "Date", false);
        }
        if(!listAttrutes[10].isEmpty() && !listValues[10].isEmpty()) {
            Situation situation = new Situation();
            situation.setLabel(listValues[10]);
            situation.setDescription(listAttrutes[10]);
            digitalPhenotypeEvent.setSituation(situation);
        }

        String json = toJson(digitalPhenotypeEvent);
        Message msg = new Message();
        msg.setServiceValue(json);
        sendProcessedData(msg);
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
