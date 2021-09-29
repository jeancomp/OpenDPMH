package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

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
    public void init(){
        try {
            Log.i(TAG, "#### Running processor Sleep");

            setNameProcessor("Sleep");

            List<String> listSensorsUtilities = new ArrayList();
            listSensorsUtilities.add("TouchScreen");
            onStartSensor(listSensorsUtilities);
        }catch (Exception e){
            Log.e(TAG, "Error: " + e.toString());
        }
    }


    @Override
    public void process(Message message){
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
    public void end(){ }
}
