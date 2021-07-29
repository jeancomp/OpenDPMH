package br.ufma.lsdi.digitalphenotyping.processormanager.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.DPApplication;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Sociability;

public class InferenceProcessorManager extends Service {
    private static final String TAG = InferenceProcessorManager.class.getName();
    Context context;
    List<String> processors = null;
    Subscriber subStartProcessor;
    Subscriber subStopProcessor;
    DPApplication dpApplication = DPApplication.getInstance();

    @Override
    public void onCreate() {
        try {
            Log.i(TAG,"#### Starting InferenceProcessorManager");

            context = dpApplication.getInstance().getContext();

            subStartProcessor = SubscriberFactory.createSubscriber();

            subStartProcessor.addConnection(dpApplication.getInstance().CDDLGetInstance().getConnection());

            subStopProcessor = SubscriberFactory.createSubscriber();

            subStopProcessor.addConnection(dpApplication.getInstance().CDDLGetInstance().getConnection());

            initProcessor();
        }catch (Exception e){
            Log.e(TAG,"Error: " + e.toString());
        }
    }


    public synchronized void startProcessor(String nameProcessor) {
        try {
            if(nameProcessor.equalsIgnoreCase("Sociability")) {
                Intent s = new Intent(context, Sociability.class);
                context.startService(s);
                Log.i(TAG, "#### Starting inference services: Sociability");
            }
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    public synchronized void stopProcessor(String nameProcessor) {
        try {
            if(nameProcessor.equalsIgnoreCase("Sociability")) {
                Intent s = new Intent(context, Sociability.class);
                context.stopService(s);
                Log.i(TAG, "#### Stopping inference services");
            }
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    public final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public InferenceProcessorManager getService() {
            return InferenceProcessorManager.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        subscribeMessageStartProcessor(dpApplication.getInstance().START_PROCESSOR_TOPIC);

        subscribeMessageStopProcessor(dpApplication.getInstance().STOP_PROCESSOR_TOPIC);

        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void initProcessor() {
        this.processors = new ArrayList();
        this.processors.add("Sociability");
    }


    public List<String> getProcessor() {
        return this.processors;
    }


    public void subscribeMessageStartProcessor(String serviceName) {
        subStartProcessor.subscribeServiceByName(serviceName);
        subStartProcessor.setSubscriberListener(subscriberStart);
    }

    public void subscribeMessageStopProcessor(String serviceName) {
        subStopProcessor.subscribeServiceByName(serviceName);
        subStopProcessor.setSubscriberListener(subscriberStop);
    }


    public ISubscriberListener subscriberStart = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages (Sociability):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);


            if (isProcessor(atividade)) {
                Log.d(TAG, "#### Start processor monitoring->  " + atividade);
                startProcessor(atividade);
            } else {
                Log.d(TAG, "#### Invalid processor name: " + atividade);
            }
        }
    };


    public ISubscriberListener subscriberStop = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages (stop):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);


            if (isProcessor(atividade)) {
                Log.d(TAG, "#### Stop processor monitoring->  " + atividade);
                stopProcessor(atividade);
            } else {
                Log.d(TAG, "#### Invalid processor name: " + atividade);
            }
        }
    };


    public ISubscriberListener subscriberPlugins = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### >>>>>>>>>>>>>>>>>>> Read messages processor:  " + message);

//            Object[] valor = message.getServiceValue();
//            String mensagemRecebida = StringUtils.join(valor, ", ");
//            Log.d(TAG, "#### " + mensagemRecebida);
//            String[] separated = mensagemRecebida.split(",");
//            String atividade = String.valueOf(separated[0]);
        }
    };


    public synchronized void publishMessage(String service, String text) {
        dpApplication.getInstance().publishMessage(service, text);
    }


    private Boolean isProcessor(String nameProcessor) {
        if (listProcessors().contains(nameProcessor)) {
            return true;
        }
        return false;
    }


    public List<String> listProcessors() {
        List<String> s = getProcessor();

        Log.i(TAG, "\n #### Processadores de dados disponíveis, Tamanho: \n" + s.size());
        for (int i = 0; i < s.size(); i++) {
            Log.i(TAG, "#### (" + i + "): " + s.get(i).toString());
        }
        return s;
    }


    public void startAllProcessors() {
    }


    public Context getContext(){
        return this.context;
    }
}
