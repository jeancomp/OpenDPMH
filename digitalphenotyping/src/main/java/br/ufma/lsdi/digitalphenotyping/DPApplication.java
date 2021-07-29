package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;
import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.listeners.IConnectionListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;

public class DPApplication extends Application{
    //TOPIC
    public static String NEW_PROCESSOR_TOPIC = "newprocessor";
    public static String REMOVE_PROCESSOR_TOPIC = "removeprocessor";
    public static String START_PROCESSOR_TOPIC = "startprocessor";
    public static String STOP_PROCESSOR_TOPIC = "stoprocessor";
    public static String ACTIVE_SENSOR_TOPIC = "activesensor";
    public static String DEACTIVATE_SENSOR_TOPIC = "deactivatesensor";
    public List<String> SUBSCRIBER_SENSOR_TOPIC = null;
    public static String SUB_AUDIO_TOPIC = "Audio";
    public static String SUB_CALL_TOPIC = "Call";
    public static String SUB_SMS_TOPIC = "SMS";
    public static String DATA_COMPOSER_TOPIC;

    private static CDDL cddl;
    private ConnectionImpl con;
    //private String clientID = UUID.randomUUID().toString();
    private String clientID = "l";
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

        this.context = this;

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


    public String getClientID(){
        return this.clientID;
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
