package br.ufma.lsdi.digitalphenotyping.dataprocessor.base;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.Topics;

public abstract class DataProcessor extends Service {
    private Context context;
    private String clientID;
    private String idProcessor;
    private String uid;
    private Date endtime;
    private double duration;
    private String nameProcessor = "";
    private List<String> listSensors = null;
    private Publisher publisher = PublisherFactory.createPublisher();

    DPUtilities dpUtilities;

    @Override
    public void onCreate() {
        context = this;

        this.clientID = CDDL.getInstance().getConnection().getClientId();

        publisher.addConnection(CDDL.getInstance().getConnection());

        listSensors = new ArrayList();

        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        dpUtilities.configSubscribers();

        dO();
        return START_STICKY;
    }

    @Override
    public void onDestroy() { end(); }


    public boolean init(){
        return true;
    }


    public boolean dO(){
        return true;
    }


    public boolean end(){
        return true;
    }


    public void setNameProcessor(String nameProcessor){ this.nameProcessor = nameProcessor; }


    public String getNameProcessor(){ return this.nameProcessor; }


    public void onStartSensor(List<String> listSensor){
        for(int i=0; i<listSensor.size(); i++) {
            publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), listSensor.get(i).toString());

            listSensors.add(listSensor.get(i).toString());
        }

        dpUtilities = new DPUtilities(listSensors);
    }

    public void onStartSensor(String nameSensor,int delay){
        publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), nameSensor, delay);
    }

    public void onStopSensor(String nameSensor){
        publishMessage(Topics.DEACTIVATE_SENSOR_TOPIC.toString(), nameSensor);
    }

    public void inference(Message message){}

    public void publishMessage(String service, String text) {
        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(text);
        message.setAvailableAttributes(1);
        publisher.publish(message);
    }

    public void publishMessage(String service, String text, int delay) {
        int total = 2;
        Object[] value = {text, delay};
        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(value);
        message.setAvailableAttributes(Integer.valueOf(total));
        publisher.publish(message);
        //Log.i(TAG,"#### RRRRRRRRRRR: " + message.getAvailableAttributes());
    }

    public void publishInference(Message message){
        publisher.publish(message);
    }

    //=============================================================================

    /**
     * For each started Sensor, a Subcriber is signed.
     */
    public class DPUtilities {
        List<String> nameSensors = new ArrayList();
        Subscriber[] subscribers;
        int numSensors = 0;

        public DPUtilities(List<String> listSensors){
            numSensors = listSensors.size();
            nameSensors = listSensors;
            subscribers = new Subscriber[numSensors];
        }

        public void configSubscribers(){
//        for(int i=0; i<numSensors; i++){
//            if(!this.nameSensors.contains(listSensors.get(i).toString())){
//                Log.i("DPUtilities","#### Sensor Add: " + listSensors.get(i).toString());
//                this.nameSensors.add(listSensors.get(i).toString());
//            }
//        }

            for(int i=0; i<numSensors; i++) {
                subscribers[i] = SubscriberFactory.createSubscriber();
                subscribers[i].addConnection(CDDL.getInstance().getConnection());
                subscribeMessage(i, this.nameSensors.get(i).toString());
            }
        }

        public void subscribeMessage(int position, String serviceName) {
            subscribers[position].subscribeServiceByName(serviceName);
            testSubcriber();
            subscribers[position].setSubscriberListener(createSubcriber(position));
        }

        ISubscriberListener[] subscriberListener;
        public void testSubcriber(){
            subscriberListener = new ISubscriberListener[numSensors];
        }

        public ISubscriberListener createSubcriber(int position){
            return subscriberListener[position] = new ISubscriberListener() {
                @Override
                public void onMessageArrived(Message message) {
                    inference(message);
                }
            };
        }
    }
}
