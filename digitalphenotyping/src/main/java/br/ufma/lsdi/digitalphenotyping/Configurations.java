package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.TextView;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;

public class Configurations extends Application{
    //ALL TOPIC
    public static String ADD_PLUGIN_TOPIC = "addplugin";
    public static String REMOVE_PLUGIN_TOPIC = "removeplugin";
    public static String START_PROCESSOR_TOPIC = "startprocessor";
    public static String STOP_PROCESSOR_TOPIC = "stoprocessor";
    public static String DATA_PROCESSORS_LIST_TOPIC[] = {"dataprocessorlist","ok"};
    public static String ACTIVE_DATA_PROCESSORS_LIST_TOPIC = "activedataprocessorlist";
    public static String ACTIVE_SENSOR_TOPIC = "activesensor";
    public static String DEACTIVATE_SENSOR_TOPIC = "deactivatesensor";
    public static String DATA_COMPOSER_TOPIC = "data_composer";

    //External Server Address for PhenotypeComposer
    private String hostServer;
    private int port;
    private String username;
    private String password;
    private String topic;

    private static CDDL cddl;
    private ConnectionImpl con;
    private String clientID;
    private int communicationTechnology = 4;
    private Boolean secure;
    private static Configurations instance = null;
    private String statusConnection = "";
    private TextView messageTextView;
    private Context context;
    private Activity activity;
    Subscriber subActive;
    Publisher publisher = PublisherFactory.createPublisher();
    private static final String TAG = Configurations.class.getName();


    public Configurations(){ }


    public Configurations(Activity a){
        this.activity = a;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        this.context = this;

        messageTextView = new TextView(context);
    }


    public static Configurations getInstance() {
        if (instance == null) {
            instance = new Configurations();
        }
        return instance;
    }


    public static CDDL CDDLGetInstance(){
        if(cddl == null){
            cddl = CDDL.getInstance();
        }
        return cddl;
    }


    public String getClientID(){
        return this.clientID;
    }


    public void setClientID(String clientID){
        this.clientID = clientID;
    }


    public Context getContext(){
        return context;
    }


    public Activity getActivity(){
        return this.activity;
    }


    public void setActivity(Activity activity){
        this.activity = activity;
    }


    public void setSecure(Boolean secure) {
        this.secure = secure;
    }


    public Boolean getSecure() {
        return this.secure;
    }


    public void setCommunicationTechnology(int communicationTechnology) {
        this.communicationTechnology = communicationTechnology;
    }


    public int getCommunicationTechnology() {
        return communicationTechnology;
    }


    public void setExternalServer(String hostServer, int port, String username, String password, String topic){
        this.hostServer = hostServer;
        this.port = port;
        this.username = username;
        this.password = password;
        this.topic = topic;
    }


    public void publishMessage(String service, String text) {
        publisher.addConnection(cddl.getInstance().getConnection());

        MyMessage message = new MyMessage();
        message.setServiceName(service);
        message.setServiceByteArray(text);
        publisher.publish(message);
    }

    public void publish(Message message) {
        publisher.addConnection(cddl.getInstance().getConnection());

        MyMessage msg = (MyMessage) message;
        publisher.publish(msg);
    }
}
