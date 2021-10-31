package br.ufma.lsdi.digitalphenotyping.rawdatacollector.base;

import static br.ufma.lsdi.digitalphenotyping.CompositionMode.FREQUENCY;
import static br.ufma.lsdi.digitalphenotyping.CompositionMode.GROUP_ALL;
import static br.ufma.lsdi.digitalphenotyping.CompositionMode.SEND_WHEN_IT_ARRIVES;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.room.Room;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.listeners.IConnectionListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.digitalphenotyping.CompositionMode;
import br.ufma.lsdi.digitalphenotyping.rawdatacollector.DataSource;
import br.ufma.lsdi.digitalphenotyping.rawdatacollector.database.AppDatabaseRD;
import br.ufma.lsdi.digitalphenotyping.rawdatacollector.database.RawData;

public class RawDataComposer {
    private static final String TAG = RawDataComposer.class.getName();
    private Publisher publisher = PublisherFactory.createPublisher();
    private static RawDataComposer instance;
    private static Context context;
    private PublishRawData publishRawData;
    private TextView messageTextView;
    private ConnectionImpl connectionBroker;
    private String statusConnection = "";
    private CompositionMode lastCompositionMode = SEND_WHEN_IT_ARRIVES;
    private Integer lastFrequency = 15;
    private String topic="";
    private List<String> nameSensorList = new ArrayList();
    private List<Boolean> valueSensorList = new ArrayList();
    private AppDatabaseRD db;
    private WorkManager workManager;

    //public RawDataComposer() {}

    public RawDataComposer(Context context) {
        try {
            Log.i(TAG, "#### Started RawDataComposer");
            this.context = context;

            messageTextView = new TextView(context);

            db = Room.databaseBuilder(context, AppDatabaseRD.class, "database-rawdata").build();
        }catch (Exception e){
            Log.e(TAG,"Error: " + e.toString());
        }
    }


    public static RawDataComposer getInstance() {
        if (instance == null) {
            instance = new RawDataComposer(context);
        }
        return instance;
    }


    public void configBroker(String hostServer, String port, String username, String password, String clientID, String topic){
        try {
            Log.i(TAG,"#### Configuration Broker Server");
            Log.i(TAG,"#### HostServer:" + hostServer);
            Log.i(TAG,"#### Port:" + port);
            Log.i(TAG,"#### Username: " + username);
            Log.i(TAG,"#### Password: " + password);
            Log.i(TAG,"#### ClientID: " + clientID);
            Log.i(TAG,"#### Topic: " + topic);

            this.topic = topic;

            //String host = "broker.hivemq.com";
            //String host = "192.168.0.7";
            connectionBroker = ConnectionFactory.createConnection();
            connectionBroker.setClientId(clientID);
            connectionBroker.setHost(hostServer);
            connectionBroker.setPort(port);
            connectionBroker.addConnectionListener(connectionListener);

            long automaticReconnectionTime = 1000L;
            int connectionTimeout = 30;
            int keepAliveInterval = 60;
            boolean automaticReconnection = true;
            boolean publishConnectionChangedStatus = false;
            int maxInflightMessages = 10;
            int mqttVersion =3;

            if(username.equals("username")) {
                connectionBroker.connect("tcp", hostServer, port, automaticReconnection, automaticReconnectionTime, false, connectionTimeout,
                        keepAliveInterval, publishConnectionChangedStatus, maxInflightMessages, "", "", mqttVersion);
            }
            else{
                connectionBroker.connect("tcp", hostServer, port, automaticReconnection, automaticReconnectionTime, false, connectionTimeout,
                        keepAliveInterval, publishConnectionChangedStatus, maxInflightMessages, username, password, mqttVersion);
            }

            Log.i(TAG,"#### CONECTADO: " + connectionBroker.isConnected());
            if(!connectionBroker.isConnected()){
                Log.i(TAG,"#### RECONNECT BROKER...");
                connectionBroker.reconnect();
            }

            publishRawData = new PublishRawData(context, connectionBroker, topic);
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.getMessage());
        }
    }


    public void manager(CompositionMode compositionMode, int frequency){
        Log.i(TAG,"#### Manager RawDataComposer");
        if(lastCompositionModeDifferent()){
            if(compositionMode == SEND_WHEN_IT_ARRIVES){

            }
            else if(compositionMode == GROUP_ALL){

            }
            else if(compositionMode == FREQUENCY){
                startWorkManager();
            }
        }
    }


    public boolean lastCompositionModeDifferent(){
        if(false){
            return false;
        }
        return true;
    }


    public void startWorkManager(){
        Log.i(TAG,"#### Started WorkManager");
        // Adicionamos restrições ao Work: 1 - esteja conectado a internet,
        //                                  2 - o nível de baterial não pode estar baixa.
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .build();

        // O Work executa periodicamente, caso uma das exigências não for atendida, tenta executar (exponencial ou linear) novamente o Work.
        PeriodicWorkRequest saveRequest =
                new PeriodicWorkRequest.Builder(DistributeRawDataWork.class, this.lastFrequency, TimeUnit.MINUTES)
                        .addTag("distributerawdatawork")
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                                TimeUnit.SECONDS)
                        .build();

        WorkManager.getInstance(this.context)
                .enqueue(saveRequest);

        workManager = WorkManager.getInstance(this.context);
    }


    public void stopWorkManager(){
        workManager.cancelAllWorkByTag("distributerawdatawork");
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


    public void rawDataPreProcessed(Message message){
        String nameTopic = message.getTopic();
        String[] separated = nameTopic.split("/");
        int tam = separated.length;
        String sensorName = separated[tam-1];

        if(lastCompositionMode == SEND_WHEN_IT_ARRIVES){
            publishRawData.getInstance().publishRawDataComposer(message);
        }
        else if(lastCompositionMode == GROUP_ALL){
            int position = nameSensorList.indexOf(sensorName);
            valueSensorList.set(position,true);
            RawData rawData = new RawData();

            if(valueSensorList.size() != 0) {
                if (!valueSensorList.isEmpty()) {
                    boolean all = true;
                    for (int i = 0; i <= valueSensorList.size(); i++) {
                        if (!valueSensorList.get(i).booleanValue()) {
                            if (!valueSensorList.contains(false)) {
                                all = true;
                            } else {
                                all = false;
                            }
                            break;
                        }
                        break;
                    }
                    if (all) {
                        publishRawData.getInstance().publishRawDataComposer(message);

                        // Retrieve information
                        rawData = db.rawDataDAO().findByRawDataAll();
                        while (rawData != null) {
                            String stringRawData = rawData.getRawdata();
                            Message msg = rawData.getObjectFromString(stringRawData);

                            // Publish the information
                            publishRawData.getInstance().publishRawDataComposer(msg);

                            // Remove from database
                            db.rawDataDAO().delete(rawData);

                            rawData = db.rawDataDAO().findByRawDataAll();
                        }
                        for (int j = 0; j < valueSensorList.size(); j++) {
                            valueSensorList.set(j, false);
                        }
                    } else {
                        //Save rawdata
                        rawData.stringFromObject(message);
                        db.rawDataDAO().insertAll(rawData);
                    }
                }
            }
            else{
                publishRawData.getInstance().publishRawDataComposer(message);
            }
        }
        else if(lastCompositionMode == FREQUENCY){
            //Save rawdata
            RawData rawData = new RawData();
            rawData.stringFromObject(message);
            db.rawDataDAO().insertAll(rawData);
        }
    }


    public void setCompositionMode(CompositionMode compositionMode, Integer frequency){
        lastCompositionMode = compositionMode;
        if(frequency != 0) {
            lastFrequency = frequency;
        }
        Log.i(TAG, "#### Value lastCompositionMode: " + lastCompositionMode.name().toString() + ", Value frequency: " + lastFrequency);
        manager(lastCompositionMode, lastFrequency);
    }


    public void setNumberSensor(List<DataSource> dataSourceList){
        for(int i=0; i < dataSourceList.size(); i++){
            nameSensorList.add(dataSourceList.get(i).getName());
            valueSensorList.add(false);
        }
    }


    public void publishMessage(String service, String text) {
        publisher.addConnection(CDDL.getInstance().getConnection());

        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(text);
        publisher.publish(message);
    }

    public String stringFromObject(Message msg){
        Gson gson = new Gson();
        String jsonString = gson.toJson(msg);
        return jsonString;
    }
}
