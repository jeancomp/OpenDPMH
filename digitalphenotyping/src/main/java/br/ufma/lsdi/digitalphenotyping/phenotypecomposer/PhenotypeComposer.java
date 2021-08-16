package br.ufma.lsdi.digitalphenotyping.phenotypecomposer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.listeners.IConnectionListener;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.MyMessage;
import br.ufma.lsdi.digitalphenotyping.Topics;

public class PhenotypeComposer extends Service {
    private static final String TAG = PhenotypeComposer.class.getName();
    Subscriber subRawDataInferenceResult;
    private Context context;
    Publisher publisher = PublisherFactory.createPublisher();

    //Data SERVIDOR
    protected CDDL cddl;
    private ConnectionImpl connectionBroker;
    private TextView messageTextView;
    private String statusConnection = "";
    private String host = "broker.hivemq.com";
    private int port = 1883;
    private String clientID="febfcfbccaeabda";
    private String username;
    private String password;
    private String topic;

    //enum compositionMode;

    // instance WorkManager;

    @Override
    public void onCreate() {
        try {
            Log.i(TAG, "#### Started PhenotypeComposer Service");
            context = this;

            subRawDataInferenceResult = SubscriberFactory.createSubscriber();
            subRawDataInferenceResult.addConnection(CDDL.getInstance().getConnection());

            messageTextView = new TextView(context);

            //this.clientID = configurations.getInstance().CDDLGetInstance().getConnection().getClientId();
        }catch (Exception e){
            Log.e(TAG,"Error: " + e.toString());
        }
    }


    public final IBinder mBinder = new PhenotypeComposer.LocalBinder();


    public class LocalBinder extends Binder {
        public PhenotypeComposer getService() {
            return PhenotypeComposer.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        subscribeMessageRawDataInferenceResult(Topics.INFERENCE_TOPIC.toString());

        startBroker();

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void startBroker(){
        try {
            String host = "broker.hivemq.com";
            Log.i(TAG,"#### ENDEREÇO DO BROKER: " + host);
            connectionBroker = ConnectionFactory.createConnection();
            connectionBroker.setClientId("febfcfbccaeabda");
            Log.i(TAG,"#### clientID:  " + this.clientID);
            Log.i(TAG,"#### clientID CDDLLLLLLLLLLLLLLLLLLLLL:  " + CDDL.getInstance().getConnection().getClientId());
            connectionBroker.setHost(host);
            connectionBroker.setPort("1883");
            connectionBroker.addConnectionListener(connectionListener);

            long automaticReconnectionTime = 1000L;
            int connectionTimeout = 30;
            int keepAliveInterval = 60;
            boolean automaticReconnection = true;
            boolean publishConnectionChangedStatus = false;
            int maxInflightMessages = 10;
            int mqttVersion =3;

            connectionBroker.connect("tcp",host,"1883", automaticReconnection,automaticReconnectionTime,false,connectionTimeout,
                    keepAliveInterval,publishConnectionChangedStatus,maxInflightMessages,"","",mqttVersion);

            Log.i(TAG,"#### CONECTADO: " + connectionBroker.isConnected());
            if(!connectionBroker.isConnected()){
                Log.i(TAG,"#### RECONNECT BROKER...");
                connectionBroker.reconnect();
            }

            //cddl.setConnection(con);
            //cddl.setContext(getContext());
            //cddl.setContext(this.context);
            //cddl.startService();

            // Para todas as tecnologias, para entao iniciar apenas a que temos interresse
            //cddl.stopAllCommunicationTechnologies();

            // Para todas os sensores, para entao iniciar apenas a que temos interresse
            //cddl.stopAllSensors();

            //cddl.startCommunicationTechnology(CDDL.INTERNAL_TECHNOLOGY_VIRTUAL_ID);
            //cddl.startAllCommunicationTechnologies();
            //cddl.startCommunicationTechnology(this.communicationTechnology);
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.getMessage());
        }
    }


    private IConnectionListener connectionListener = new IConnectionListener() {
        @Override
        public void onConnectionEstablished() {
            statusConnection = "Established connection.";
            messageTextView.setText("Conexão estabelecida.");
            Log.i(TAG,"#### Status CDDL: " + statusConnection);
        }

        @Override
        public void onConnectionEstablishmentFailed() {
            statusConnection = "Failed connection.";
            messageTextView.setText("Falha na conexão.");
            Log.i(TAG,"#### Status MQTT: " + statusConnection);
        }

        @Override
        public void onConnectionLost() {
            statusConnection = "Lost connection.";
            messageTextView.setText("Conexão perdida.");
            Log.i(TAG,"#### Status MQTT: " + statusConnection);
        }

        @Override
        public void onDisconnectedNormally() {
            statusConnection = "A normal disconnect has occurred.";
            messageTextView.setText("Uma disconexão normal ocorreu.");
            Log.i(TAG,"#### Status MQTT: " + statusConnection);
        }
    };


    public void subscribeMessageRawDataInferenceResult(String serviceName) {
        subRawDataInferenceResult.subscribeServiceByName(serviceName);
        subRawDataInferenceResult.setSubscriberListener(subscriberRawDataInferenceResultListener);
        subRawDataInferenceResult.subscribeTopic(Topics.INFERENCE_TOPIC.toString());
    }


    public ISubscriberListener subscriberRawDataInferenceResultListener = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.i(TAG, "#### Read messages (RawDataInferenceResultListener):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);

            publishPhenotypeComposer(message);

//            if (isInternalSensor(atividade) || isVirtualSensor(atividade)) {
//                Log.d(TAG, "#### Start sensor monitoring->  " + atividade);
//                onStartSensor(atividade);
//            } else {
//                Log.d(TAG, "#### Invalid sensor name: " + atividade);
//            }
        }
    };


    public void publishPhenotypeComposer(Message message) {
        Log.i(TAG,"#### Data Publish to Server");
        publisher.addConnection(connectionBroker);

        MyMessage msg = new MyMessage();
        msg.setServiceName("inference");
        msg.setTopic("inference");
        msg.setServiceValue(message.getServiceValue());

        Log.i(TAG,"#### Data: " + msg);

        publisher.publish(msg);

        //MyMessage msg = (MyMessage) message;
        //publisher.publish(msg);
    }
}
