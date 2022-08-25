package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Situation;

public class Sleep extends DataProcessor {
    private static final String TAG = Sleep.class.getName();
    List<String> sensorList = new ArrayList();

    @Override
    public void init() {
        try {
            Log.i(TAG, "#### Running processor Sleep");

            setDataProcessorName("Sleep");

            sensorList.add("TouchScreen");
            //sensorList.add("Location");
            startSensor(sensorList);

        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }
    }


    @Override
    public void onSensorDataArrived(Message message) {
        inferencePhenotypingEvent(message);
    }


    @Override
    public void inferencePhenotypingEvent(Message message) {
        Log.i(TAG, "#### MSG ORIGINAL SLEEP: " + message);
        Situation digitalPhenotypeEvent = new Situation();
        digitalPhenotypeEvent.setDataProcessorName(getDataProcessorName());
        digitalPhenotypeEvent.setUid(CDDL.getInstance().getConnection().getClientId());

        digitalPhenotypeEvent.setLabel("Touch");
        digitalPhenotypeEvent.setDescription("Screen touch detection");

        Object[] valor1 = message.getServiceValue();
        String mensagemRecebida1 = StringUtils.join(valor1, ", ");
        String[] listValues = mensagemRecebida1.split(",");

        String[] valor2 = message.getAvailableAttributesList();
        String mensagemRecebida2 = StringUtils.join(valor2, ", ");
        String[] listAttributes = mensagemRecebida2.split(",");

        /*if(!listAttrutes[0].isEmpty() && !listValues[0].isEmpty()) {
            digitalPhenotypeEvent.setAttributes(listAttrutes[0], listValues[0], "String", false);
        }*/
        if (!listAttributes[2].isEmpty() && !listValues[2].isEmpty()) {
            Log.i(TAG, "#### listAttributes[2]: " + listAttributes[2]);
            Log.i(TAG, "#### listValues[2]: " + listValues[2]);
            digitalPhenotypeEvent.setAttributes(listAttributes[2], listValues[2], "Date", false);
        }

        Log.i(TAG, "#### Situation: " + digitalPhenotypeEvent);

        String json = toJson(digitalPhenotypeEvent);
        Message msg = new Message();
        //msg.setAvailableAttributesList(new [{"RawData"}]);
        msg.setServiceValue(json);
        sendProcessedData(msg);
        saveDigitalPhenotypeEvent(digitalPhenotypeEvent);
    } //inferencia do evento de fenotipagem


    @Override
    public void end() {
    }
}
