package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.DPApplication;
import br.ufma.lsdi.digitalphenotyping.Topic;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.ProcessorsModel;

public class Sociability extends Service implements ProcessorsModel {
    private static final String TAG = Sociability.class.getName();
    public Context context;
    public String idProcessor;
    public String uid;
    public Date endtime;
    public double duration;
    public String sociabilityType;
    Subscriber subAudio;
    Subscriber subCall;
    Subscriber subSMS;
    Topic topic = Topic.J;
    DPApplication dpApplication = DPApplication.getInstance();


    @Override
    public void onCreate() {
        try {
            Log.i(TAG, "#### Running processor Sociability");

            context = dpApplication.getInstance().getContext();

            subAudio = SubscriberFactory.createSubscriber();
            subAudio.addConnection(dpApplication.getInstance().CDDLGetInstance().getConnection());

            subCall = SubscriberFactory.createSubscriber();
            subCall.addConnection(dpApplication.getInstance().CDDLGetInstance().getConnection());

            subSMS = SubscriberFactory.createSubscriber();
            subSMS.addConnection(dpApplication.getInstance().CDDLGetInstance().getConnection());

            //startSensor("Audio");
            startSensor("Call");
            //startSensor("SMS");

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
        super.onStartCommand(intent, flags, startId);

        subscribeMessageAudio("mhub/" + dpApplication.getInstance().getClientID() + topic.SUB_AUDIO);

        subscribeMessageCall(topic.SUB_CALL);

        subscribeMessageSMS("mhub/" + dpApplication.getInstance().getClientID() + topic.SUB_SMS);

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    @Override
    public void onDestroy() {
        super.onDestroy();
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
//                startSensor(atividade);
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

//            if (isInternalSensor(atividade) || isVirtualSensor(atividade)) {
//                Log.d(TAG, "#### Start sensor monitoring->  " + atividade);
//                startSensor(atividade);
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

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);

//            if (isInternalSensor(atividade) || isVirtualSensor(atividade)) {
//                Log.d(TAG, "#### Start sensor monitoring->  " + atividade);
//                startSensor(atividade);
//            } else {
//                Log.d(TAG, "#### Invalid sensor name: " + atividade);
//            }
        }
    };

// --- Código ProcessorsModel ----------------------------------------------------------------------

    @Override
    public void publish(Message message) {
        dpApplication.getInstance().publish(message);
    }


    @Override
    public String subscribe() {
        return null;
    }


    @Override
    public Boolean isValid() {
        return null;
    }


    @Override
    public void startSensor(String nameSensor){
        dpApplication.getInstance().publishMessage(topic.ACTIVE_SENSOR, nameSensor);
    }


    @Override
    public void stopSensor(String nameSensor){
        dpApplication.getInstance().publishMessage(topic.DEACTIVATE_SENSOR, nameSensor);
    }


    @Override
    public void inference() { }
}
