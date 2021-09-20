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

public class Sleep extends DataProcessor {
    private static final String TAG = Sleep.class.getName();

    @Override
    public boolean init(){
        try {
            Log.i(TAG, "#### Running processor Sociability");

            setNameProcessor("Sleep");

            List<String> listSensorsUtilities = new ArrayList();
            listSensorsUtilities.add("TouchScreen");
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


    public boolean end(){
        return true;
    }

    @Override
    public void inference(Message message){

        Object[] valor = message.getServiceValue();
        String mensagemRecebida = StringUtils.join(valor, ", ");

        Object[] finalValor = {getNameProcessor(),mensagemRecebida};
        Log.i(TAG,"#### VALOR: " + finalValor[0] + ", " + String.valueOf(finalValor[1]));

        Message msg = new Message();
        //msg.setAvailableAttributesList(new String[]{"Name processor"});
        //msg.setAvailableAttributes();
        msg.setServiceName(Topics.INFERENCE_TOPIC.toString());
        msg.setServiceValue(finalValor);
        msg.setTopic(Topics.INFERENCE_TOPIC.toString());
        Log.i(TAG,"#### MENSAGEM: " + msg);

        publishInference(msg);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
