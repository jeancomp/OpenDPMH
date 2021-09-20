package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;

public class Sociability extends DataProcessor {
    private static final String TAG = Sociability.class.getName();
    public Context context;
    private String clientID;
    public String idProcessor;
    public String uid;
    public Date endtime;
    public double duration;
    public String sociabilityType;

    @Override
    public boolean init() {
        try {
            Log.i(TAG, "#### Running processor Sociability");

            setNameProcessor("Sociability");

            List<String> listSensorsUtilities = new ArrayList();
            listSensorsUtilities.add("Call");
            listSensorsUtilities.add("SMS");
            //onStartSensor("Audio");
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
    public boolean end() {
        onStopSensor("Call");
        onStopSensor("SMS");
        //onStopSensor("Audio");
        return true;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public Boolean isValidSMS(Object[] valor){
        // O que seria um SMS inválido ???
        return true;
    }


    @Override
    public void inference(Message message) {
        Object[] valor = message.getServiceValue();
        String mensagemRecebida = StringUtils.join(valor, ", ");
        Log.i(TAG, "#### " + mensagemRecebida);
        String[] separated = mensagemRecebida.split(",");

        int tamanhoMsg = (String.valueOf(separated[1])).length();
        Log.i(TAG,"#### Tam: " + tamanhoMsg);

        if(isValidSMS(valor)){
            //Message msg = (Message) message;
            //msg.setServiceName("rawdatainference");
            //msg.setServiceByteArray(valor);
            //msg.setServiceName(configurations.getInstance().RAW_DATA_INFERENCE_RESULT_TOPIC);
            //msg.setTopic(configurations.getInstance().RAW_DATA_INFERENCE_RESULT_TOPIC);
            //msg.setPublisherID("febfcfbccaeabda");

            Object[] finalValor = {getNameProcessor(),mensagemRecebida};
            Log.i(TAG,"#### VALOR: " + finalValor[0] + ", " + String.valueOf(finalValor[1]));

            Message msg = new Message();
            //msg.setServiceByteArray(message.getServiceValue());
            msg.setServiceName(Topics.INFERENCE_TOPIC.toString());
            msg.setServiceValue(finalValor);
            msg.setTopic(Topics.INFERENCE_TOPIC.toString());
            Log.i(TAG,"#### MENSAGEM: " + msg);
            publishInference(msg);
        }
    }
}
