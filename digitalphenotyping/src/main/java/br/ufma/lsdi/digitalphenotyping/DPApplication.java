package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.listeners.IConnectionListener;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;

public class DPApplication extends Application {
    private static CDDL cddl;
    private ConnectionImpl con;
    private String clientID;
    private int communicationTechnology = 4;
    private Boolean secure;
    private static DPApplication instance = null;
    private String statusConnection = "";
    private TextView messageTextView;
    private Context context;
    private Activity activity;
    Subscriber subActive;
    Publisher publisher = PublisherFactory.createPublisher();
    private static final String TAG = DPApplication.class.getName();


    public DPApplication(){ }


    public DPApplication(Activity a){
        this.activity = a;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        context = this;

        messageTextView = new TextView(context);
    }


    public static DPApplication getInstance() {
        if (instance == null) {
            instance = new DPApplication();
        }
        return instance;
    }


    public static CDDL CDDLGetInstance(){
        if(cddl == null){
            cddl = CDDL.getInstance();
        }
        return cddl;
    }

    public void initCDDL(String newHost){
        try {
            //String host = CDDL.startMicroBroker();
            String host = newHost;
            Log.i(TAG,"#### ENDEREÇO DO BROKER: " + host);
            //val host = "broker.hivemq.com";
            con = ConnectionFactory.createConnection();
            con.setClientId(getClientID());
            con.setHost(host);
            con.addConnectionListener(connectionListener);
            con.connect();
            //cddl = CDDL.getInstance();
            cddl = DPApplication.getInstance().CDDLGetInstance();
            cddl.setConnection(con);
            //cddl.setContext(getContext());
            cddl.setContext(getContext());
            cddl.startService();

            // Para todas as tecnologias, para entao iniciar apenas a que temos interresse
            cddl.stopAllCommunicationTechnologies();

            // Para todas os sensores, para entao iniciar apenas a que temos interresse
            cddl.stopAllSensors();

            //cddl.startCommunicationTechnology(CDDL.INTERNAL_TECHNOLOGY_VIRTUAL_ID);
            cddl.startCommunicationTechnology(this.communicationTechnology);
        }catch (Exception e){
            Log.i(TAG,"#### Error: " + e.getMessage());
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


    public String getClientID(){
        return this.clientID;
    }


    public Context getContext(){
        return context;
    }


    public Activity getActivity(){
        return this.activity;
    }


    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public Boolean getSecure() {
        return this.secure;
    }


    public void publishMessage(String service, String text) {
        publisher.addConnection(cddl.getInstance().getConnection());

        MyMessage message = new MyMessage();
        message.setServiceName(service);
        message.setServiceByteArray(text);
        publisher.publish(message);
    }
}
