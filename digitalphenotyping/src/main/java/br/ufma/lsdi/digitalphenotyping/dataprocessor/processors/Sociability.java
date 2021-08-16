package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.apache.commons.lang3.StringUtils;
import java.util.Date;
import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.MyMessage;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;

public class Sociability extends Service implements DataProcessor {
    private static final String TAG = Sociability.class.getName();
    public Context context;
    private String clientID;
    public String idProcessor;
    public String uid;
    public Date endtime;
    public double duration;
    public String sociabilityType;
    Subscriber subAudio;
    Subscriber subCall;
    Subscriber subSMS;
    Publisher publisher = PublisherFactory.createPublisher();


    @Override
    public void onCreate() {
        try {
            Log.i(TAG, "#### Running processor Sociability");

            context = this;

            this.clientID = CDDL.getInstance().getConnection().getClientId();

            publisher.addConnection(CDDL.getInstance().getConnection());

            subAudio = SubscriberFactory.createSubscriber();
            subAudio.addConnection(CDDL.getInstance().getConnection());

            subCall = SubscriberFactory.createSubscriber();
            subCall.addConnection(CDDL.getInstance().getConnection());

            subSMS = SubscriberFactory.createSubscriber();
            subSMS.addConnection(CDDL.getInstance().getConnection());

            //onStartSensor("Audio");
            onStartSensor("Call");
            onStartSensor("SMS");
        }catch (Exception e){
            Log.e(TAG, "Error: " + e.toString());
        }
    }


    public final IBinder mBinder = new Sociability.LocalBinder();


    public class LocalBinder extends Binder {
        public Sociability getService() {
            return Sociability.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        subscribeMessageAudio("Audio");

        subscribeMessageCall("Call");

        subscribeMessageSMS("SMS");

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    @Override
    public void onDestroy() {
        super.onDestroy();

        onStopSensor("Audio");
        onStopSensor("Call");
        onStopSensor("SMS");
    }


    public void subscribeMessageAudio(String serviceName) {
        subAudio.subscribeServiceByName(serviceName);
        subAudio.setSubscriberListener(subscriberAudio);
    }


    public void subscribeMessageCall(String serviceName) {
        subCall.subscribeServiceByName(serviceName);
        subCall.setSubscriberListener(subscriberCall);
    }


    public void subscribeMessageSMS(String serviceName) {
        subSMS.subscribeServiceByName(serviceName);
        subSMS.setSubscriberListener(subscriberSMS);
    }


    public ISubscriberListener subscriberAudio = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages (Audio):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);

//            if (isInternalSensor(atividade) || isVirtualSensor(atividade)) {
//                Log.d(TAG, "#### Start sensor monitoring->  " + atividade);
//                onStartSensor(atividade);
//            } else {
//                Log.d(TAG, "#### Invalid sensor name: " + atividade);
//            }
        }
    };


    public ISubscriberListener subscriberCall = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages (Call):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);

            inference(message);

//            if (isInternalSensor(atividade) || isVirtualSensor(atividade)) {
//                Log.d(TAG, "#### Start sensor monitoring->  " + atividade);
//                onStartSensor(atividade);
//            } else {
//                Log.d(TAG, "#### Invalid sensor name: " + atividade);
//            }
        }
    };


    public ISubscriberListener subscriberSMS = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages (SMS):  " + message);
            inference(message);
        }
    };


    public Boolean isValidSMS(Object[] valor){
        // O que seria um SMS inválido ???
        return true;
    }

// --- Código to DataProcessor ---------------------------------------------------------------------
    @Override
    public void onStartSensor(String nameSensor){
        publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), nameSensor);
    }


    @Override
    public void onStopSensor(String nameSensor){
        publishMessage(Topics.DEACTIVATE_SENSOR_TOPIC.toString(), nameSensor);
    }


    @Override
    public void setCommunicationTechnology(int number){

    }


    @Override
    public String subscribeRawData(Message message) {
        return null;
    }


    @Override
    public void inference(Message message) {
        Object[] valor = message.getServiceValue();
        String mensagemRecebida = StringUtils.join(valor, ", ");
        Log.d(TAG, "#### " + mensagemRecebida);
        String[] separated = mensagemRecebida.split(",");

        int tamanhoMsg = (String.valueOf(separated[1])).length();
        Log.i(TAG,"#### Tam: " + tamanhoMsg);

        if(isValidSMS(valor)){
            //MyMessage msg = (MyMessage) message;
            //msg.setServiceName("rawdatainference");
            //msg.setServiceByteArray(valor);
            MyMessage msg = new MyMessage();
            //msg.setServiceName(configurations.getInstance().RAW_DATA_INFERENCE_RESULT_TOPIC);
            //msg.setTopic(configurations.getInstance().RAW_DATA_INFERENCE_RESULT_TOPIC);
            //msg.setPublisherID("febfcfbccaeabda");
            msg.setServiceByteArray(message.getServiceValue());
            msg.setServiceName(Topics.INFERENCE_TOPIC.toString());
            msg.setTopic(Topics.INFERENCE_TOPIC.toString());
            Log.i(TAG,"#### MENSAGEM: " + msg);
            publishInference(msg);
        }
    }


    public void publishMessage(String service, String text) {
        MyMessage message = new MyMessage();
        message.setServiceName(service);
        message.setServiceValue(text);
        publisher.publish(message);
    }


    @Override
    public void publishInference(Message message) {
        //publisher.addConnection(CDDL.getInstance().getConnection());
        Log.i(TAG,"#### Data Publish to PhenotypeComposer");
        MyMessage msg = (MyMessage) message;
        publisher.publish(msg);
    }
}
