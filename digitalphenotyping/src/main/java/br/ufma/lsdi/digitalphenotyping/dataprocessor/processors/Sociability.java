package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Situation;

public class Sociability extends DataProcessor {
    private static final String TAG = Sociability.class.getName();
    public Context context;
    List<String> listSensors = new ArrayList();

    @Override
    public void init() {
        try {
            Log.i(TAG, "#### Running processor Sociability.");

            setDataProcessorName("Sociability");

            listSensors.add("Call");
            listSensors.add("SMS");
            //onStartSensor("Audio");
            startSensor(listSensors);
        }catch (Exception e){
            Log.e(TAG, "Error: " + e.toString());
        }
    }


    @Override
    public void onSensorDataArrived(Message message) {
        inferencePhenotypingEvent(message);
    }


    @Override
    public void inferencePhenotypingEvent(Message message){
        Log.i(TAG,"#### MSG ORIGINAL SOCIABILITY: " + message);
        DigitalPhenotypeEvent digitalPhenotypeEvent = new DigitalPhenotypeEvent();
        digitalPhenotypeEvent.setDataProcessorName(getDataProcessorName());
        digitalPhenotypeEvent.setUid(CDDL.getInstance().getConnection().getClientId());

        Situation situation = new Situation();
        situation.setLabel("Socialization");
        situation.setDescription("User socialized.");
        digitalPhenotypeEvent.setSituation(situation);

        Object[] valor1 = message.getServiceValue();
        String mensagemRecebida1 = StringUtils.join(valor1, ", ");
        String[] listValues = mensagemRecebida1.split(",");

        String[] valor2 = message.getAvailableAttributesList();
        String mensagemRecebida2 = StringUtils.join(valor2, ", ");
        String[] listAttributes = mensagemRecebida2.split(",");

        /*if(!listAttrutes[0].isEmpty() && !listValues[0].isEmpty()) {
            digitalPhenotypeEvent.setAttributes(listAttrutes[0], listValues[0], "String", false);
        }*/
        if(!listAttributes[2].isEmpty() && !listValues[2].isEmpty()) {
            Log.i(TAG,"#### listAttributes[2]: " + listAttributes[2]);
            Log.i(TAG,"#### listValues[2]: " + listValues[2]);
            digitalPhenotypeEvent.setAttributes(listAttributes[2], listValues[2], "String", false);
        }
        if(!listAttributes[3].isEmpty() && !listValues[3].isEmpty()) {
            Log.i(TAG,"#### listAttributes[3]: " + listAttributes[3]);
            Log.i(TAG,"#### listValues[3]: " + listValues[3]);
            digitalPhenotypeEvent.setAttributes(listAttributes[3], listValues[2], "Date", false);
        }
        if(!listAttributes[4].isEmpty() && !listValues[4].isEmpty()) {
            Log.i(TAG,"#### listAttributes[4]: " + listAttributes[3]);
            Log.i(TAG,"#### listValues[4]: " + listValues[3]);
            digitalPhenotypeEvent.setAttributes(listAttributes[3], listValues[2], "Integer", false);
        }

        Log.i(TAG,"#### DigitalPhenotypeEvent: " + digitalPhenotypeEvent.toString());

        String json = toJson(digitalPhenotypeEvent);
        Message msg = new Message();
        msg.setServiceValue(json);
        sendProcessedData(msg);
    }


    @Override
    public void end() { }
}
