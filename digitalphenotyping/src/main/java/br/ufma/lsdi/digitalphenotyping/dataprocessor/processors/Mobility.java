package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;

public class Mobility extends DataProcessor {
    private static final String TAG = Mobility.class.getName();

    @Override
    public boolean init(){
        try {
            Log.i(TAG, "#### Running processor Mobility");

            setNameProcessor("Mobility");

            List<String> listSensorsUtilities = new ArrayList();
            listSensorsUtilities.add("Tilt Detector");
            onStartSensor(listSensorsUtilities);
        }catch (Exception e){
            Log.e(TAG, "Error: " + e.toString());
        }
        return true;
    }


    @Override
    public boolean dO(){
        return true;
    }

    @Override
    public boolean end(){
        onStopSensor("Tilt Detector");
        return true;
    }


    @Override
    public void inference(Message message) {
        Object[] valor = message.getServiceValue();
        String mensagemRecebida = StringUtils.join(valor, ", ");
        String[] separated = mensagemRecebida.split(",");

        if(isValidTilt(valor)){
            //Message msg = (Message) message;
            //msg.setServiceName("rawdatainference");
            //msg.setServiceByteArray(valor);
            //msg.setServiceName(configurations.getInstance().RAW_DATA_INFERENCE_RESULT_TOPIC);
            //msg.setTopic(configurations.getInstance().RAW_DATA_INFERENCE_RESULT_TOPIC);
            //msg.setPublisherID("febfcfbccaeabda");

            Object[] finalValor = {getNameProcessor(),mensagemRecebida};
            Log.i(TAG,"#### VALOR: " + finalValor[0] + ", " + String.valueOf(finalValor[1]));

            Message msg = new Message();

            //msg.setAvailableAttributesList(new String[]{"Tilt","Acceleration"});

            msg.setServiceName(Topics.INFERENCE_TOPIC.toString());
            msg.setServiceValue(finalValor);
            msg.setTopic(Topics.INFERENCE_TOPIC.toString());
            Log.i(TAG,"#### MENSAGEM: " + msg);
            publishInference(msg);
        }
    }


    public Boolean isValidTilt(Object[] valor){
        // O que seria um Tilt inválido ???
        return true;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
