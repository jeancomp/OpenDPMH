package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.SaveActivity;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Situation;

public class Online_Sociability extends DataProcessor{
    private static final String TAG = Online_Sociability.class.getName();
    private SaveActivity saveActivity = SaveActivity.getInstance();
    private List<String> listSensors = new ArrayList();

    @Override
    public void init() {
        try {
            Log.i(TAG, "#### Running processor Online_Sociability.");

            setDataProcessorName("Online_Sociability");

            listSensors.add("Call");
            listSensors.add("SMS");
            startSensor(listSensors);
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.toString());
        }
    }


    @Override
    public void onSensorDataArrived(Message message) {
        inferencePhenotypingEvent(message);
    }


    @Override
    public void inferencePhenotypingEvent(Message message){
        try {
            Log.i(TAG, "#### MSG ORIGINAL ONLINE_SOCIABILITY: " + message);
            DigitalPhenotypeEvent digitalPhenotypeEvent = new DigitalPhenotypeEvent();
            digitalPhenotypeEvent.setDataProcessorName(getDataProcessorName());
            digitalPhenotypeEvent.setUid(CDDL.getInstance().getConnection().getClientId());

            Object[] valor1 = message.getServiceValue();
            String mensagemRecebida1 = StringUtils.join(valor1, ", ");
            String[] listServiceValue = mensagemRecebida1.split(",");

            String[] valor2 = message.getAvailableAttributesList();
            String mensagemRecebida2 = StringUtils.join(valor2, ", ");
            String[] listAttributes = mensagemRecebida2.split(",");

            if (message.getServiceName().equals("Call")) {
                Situation situation = new Situation();
                situation.setLabel("Online_Sociability");
                situation.setDescription("We identify Online_Sociability by the user through the phone call.");
                digitalPhenotypeEvent.setSituation(situation);

                if (!listAttributes[2].isEmpty() && !listServiceValue[2].isEmpty()) {
                    digitalPhenotypeEvent.setAttributes(listAttributes[2], listServiceValue[2], "String", false);
                }
                if (!listAttributes[3].isEmpty() && !listServiceValue[3].isEmpty()) {
                    digitalPhenotypeEvent.setAttributes(listAttributes[3], listServiceValue[3], "Date", false);
                }
                if (!listAttributes[4].isEmpty() && !listServiceValue[4].isEmpty()) {
                    digitalPhenotypeEvent.setAttributes(listAttributes[4], listServiceValue[4], "Integer", false);
                }
            }
            else if (message.getServiceName().equals("SMS")) {
                Situation situation = new Situation();
                situation.setLabel("Online_Sociability");
                situation.setDescription("We identify Online_Sociability by the user through the SMS.");
                digitalPhenotypeEvent.setSituation(situation);

                if (!listAttributes[2].isEmpty() && !listServiceValue[2].isEmpty()) {
                    digitalPhenotypeEvent.setAttributes(listAttributes[2], listServiceValue[2], "String", false);
                }
                if (!listAttributes[3].isEmpty() && !listServiceValue[3].isEmpty()) {
                    digitalPhenotypeEvent.setAttributes(listAttributes[3], listServiceValue[3], "Date", false);
                }
                if (!listAttributes[4].isEmpty() && !listServiceValue[4].isEmpty()) {
                    digitalPhenotypeEvent.setAttributes(listAttributes[4], listServiceValue[4], "Integer", false);
                }
            }

            Log.i(TAG, "#### DigitalPhenotypeEvent: " + digitalPhenotypeEvent.toString());

            String json = toJson(digitalPhenotypeEvent);
            Message msg = new Message();
            msg.setServiceValue(json);
            sendProcessedData(msg);
            saveDigitalPhenotypeEvent(digitalPhenotypeEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void end(){}


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public final IBinder mBinder = new Online_Sociability.LocalBinder();


    public class LocalBinder extends Binder {
        public Online_Sociability getService() {
            return Online_Sociability.this;
        }
    }
}
