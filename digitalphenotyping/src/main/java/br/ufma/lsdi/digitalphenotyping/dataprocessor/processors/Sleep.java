package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;

public class Sleep extends DataProcessor {
    private static final String TAG = Sleep.class.getName();
    int i=0;

    @Override
    public void init(){
        try {
            Log.i(TAG, "#### Running processor Sleep");

            setDataProcessorName("Sleep");

            List<String> sensorList = new ArrayList();
            sensorList.add("TouchScreen");
            sensorList.add("SMS");
            startSensor(sensorList);
        }catch (Exception e){
            Log.e(TAG, "Error: " + e.toString());
        }
    }


    @Override
    public void onSensorDataArrived(Message message){
        processedDataMessage(message);
    }


    @Override
    public void processedDataMessage(Message message){
        sendProcessedData(message);
    }


    @Override
    public void end(){ }
}
