package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;

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
        sendProcessedData(message);
    }


    @Override
    public void end() { }
}
