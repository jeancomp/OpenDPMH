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
        processedDataMessage(message);
    }


    @Override
    public void processedDataMessage(Message message){
        Log.i(TAG,"#### MSG ORIGINAL SOCIABILITY: " + message);
        DigitalPhenotypeEvent digitalPhenotypeEvent = new DigitalPhenotypeEvent();
        digitalPhenotypeEvent.setUid(CDDL.getInstance().getConnection().getClientId());

        Situation situation = new Situation();
        situation.setLabel("Socialized");
        situation.setDescription("Individual socialized.");
        //digitalPhenotypeEvent.setSituation(situation);

        Object[] valor1 = message.getServiceValue();
        String mensagemRecebida1 = StringUtils.join(valor1, ", ");
        String[] listValues = mensagemRecebida1.split(",");

        Object[] valor2 = message.getAvailableAttributesList();
        String mensagemRecebida2 = StringUtils.join(valor2, ", ");
        String[] listAttrutes = mensagemRecebida2.split(",");

        /*if(!listAttrutes[0].isEmpty() && !listValues[0].isEmpty()) {
            digitalPhenotypeEvent.setAttributes(listAttrutes[0], listValues[0], "String", false);
        }*/
        if(!listAttrutes[1].isEmpty() && !listValues[1].isEmpty()) {
            //digitalPhenotypeEvent.setAttributes(listAttrutes[1], listValues[1], "Date", false);
        }

        String json = toJson(digitalPhenotypeEvent);
        Message msg = new Message();
        msg.setServiceValue(json);
        sendProcessedData(msg);
    }


    @Override
    public void end() { }
}
