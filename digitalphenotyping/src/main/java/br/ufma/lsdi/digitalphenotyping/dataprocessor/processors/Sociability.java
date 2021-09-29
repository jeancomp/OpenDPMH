package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;

public class Sociability extends DataProcessor {
    private static final String TAG = Sociability.class.getName();
    public Context context;
    private String clientID;

//    List<String> listSensoresTESTE = new ArrayList();
//
//    public Sociability(listSensoresTESTE){
//        super(listSensoresTESTE);
//    }

    @Override
    public void init() {
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
    }


    @Override
    public void process(Message message){
        Object[] valor = message.getServiceValue();
        String mensagemRecebida = StringUtils.join(valor, ", ");
        Log.i(TAG, "#### " + mensagemRecebida);
        String[] separated = mensagemRecebida.split(",");

        int tamanhoMsg = (String.valueOf(separated[1])).length();
        Log.i(TAG,"#### Tam: " + tamanhoMsg);

        Object[] finalValor = {getNameProcessor(),mensagemRecebida};
        Log.i(TAG,"#### VALOR: " + finalValor[0] + ", " + String.valueOf(finalValor[1]));

        Message msg = new Message();
        msg.setServiceName(Topics.INFERENCE_TOPIC.toString());
        msg.setServiceValue(finalValor);
        msg.setTopic(Topics.INFERENCE_TOPIC.toString());
        Log.i(TAG,"#### MENSAGEM: " + msg);
        publishInference(msg);
    }


    @Override
    public void end() {
        onStopSensor("Call");
        onStopSensor("SMS");
        //onStopSensor("Audio");
    }
}
