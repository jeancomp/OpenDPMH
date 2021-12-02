package br.ufma.lsdi.digitalphenotyping.rawdatacollector;

import android.content.Context;
import android.hardware.Sensor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.IMonitorListener;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.cddl.qos.TimeBasedFilterQoS;
import br.ufma.lsdi.digitalphenotyping.CompositionMode;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.handlingexceptions.InvalidSensorNameException;
import br.ufma.lsdi.digitalphenotyping.rawdatacollector.base.RawDataComposer;

public class RawDataCollectorUtil {
    private static final String TAG = RawDataCollectorUtil.class.getName();
    private Publisher publisherCore = PublisherFactory.createPublisher();
    private List<String> listUsedSensors = new ArrayList();
    private List<String> sensorList = new ArrayList();
    private List<DataSource> dataSourceList = new ArrayList();
    private CompositionMode compositionMode = CompositionMode.SEND_WHEN_IT_ARRIVES;
    private Subscriber[] subscribers;
    private RawDataComposer rawDataComposer;
    private Integer frequency=15;
    private Context context;
    private int numSensors=0;
    private String topic="";


    public RawDataCollectorUtil(Context context, List<DataSource> dataSourceList, CompositionMode compositionMode) {
        try {
            this.context = context;
            this.dataSourceList = dataSourceList;
            this.compositionMode = compositionMode;
            numSensors = dataSourceList.size();
            subscribers = new Subscriber[numSensors];
            rawDataComposer = new RawDataComposer(context);
            rawDataComposer.getInstance().setCompositionMode(compositionMode, 0);
            rawDataComposer.getInstance().setNumberSensor(dataSourceList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public RawDataCollectorUtil(Context context, List<DataSource> dataSourceList, CompositionMode compositionMode, Integer frequency) {
        try {
            this.context = context;
            this.dataSourceList = dataSourceList;
            this.compositionMode = compositionMode;
            this.frequency = frequency;
            numSensors = dataSourceList.size();
            subscribers = new Subscriber[numSensors];
            rawDataComposer = new RawDataComposer(context);
            rawDataComposer.getInstance().setCompositionMode(compositionMode, frequency);
            rawDataComposer.getInstance().setNumberSensor(dataSourceList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void configSubscribers(){
        for(int i=0; i<numSensors; i++) {
            subscribers[i] = SubscriberFactory.createSubscriber();
            subscribers[i].addConnection(CDDL.getInstance().getConnection());

            if(dataSourceList.get(i).getSamplingRate() != 0) {
                TimeBasedFilterQoS timeBasedFilterQoS = new TimeBasedFilterQoS();
                timeBasedFilterQoS.setMinSeparation(dataSourceList.get(i).getSamplingRate());
                subscribers[i].setTimeBasedFilterQoS(timeBasedFilterQoS);
            }

            // Add EPL : Link CEP https://esper.espertech.com/release-5.1.0/solution_patterns.php
            subscribers[i].getMonitor().addRule(dataSourceList.get(i).getEpl(), new IMonitorListener() {
                @Override
                public void onEvent(Message message) {
                    Log.i(TAG,"#### Event detected: " + message.getServiceName());
                }
            });

            subscribeMessage(i, dataSourceList.get(i).getName());
        }
    }

    public void subscribeMessage(int position, String serviceName) {
        subscribers[position].subscribeServiceByName(serviceName);
        createSubscriberVector();
        subscribers[position].setSubscriberListener(createSubcriber(position));
    }

    ISubscriberListener[] subscriberListener;
    public void createSubscriberVector(){
        subscriberListener = new ISubscriberListener[numSensors];
    }

    public ISubscriberListener createSubcriber(int position){
        return subscriberListener[position] = new ISubscriberListener() {
            @Override
            public void onMessageArrived(Message message) {
                try {
                    rawDataComposer.getInstance().rawDataPreProcessed(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    /*if (rawDataComposer.getInstance().getDB() != null && rawDataComposer.getInstance().getDB().isOpen()){
                        rawDataComposer.getInstance().getDB().close();
                    }*/
                }
            }
        };
    }


    public void connectionBroker(String hostServer, String port, String username, String password, String clientID, String topic) throws InvalidSensorNameException {
        rawDataComposer.getInstance().configBroker(hostServer,port,username,password,clientID,topic);
        startSensor(dataSourceList);
        configSubscribers();
    }


    public List<String> getSensors(){
        sensorList = CDDL.getInstance().getSensorVirtualList();
        List<Sensor> sensorInternal = CDDL.getInstance().getInternalSensorList();

        if (sensorInternal.size() != 0) {
            for (int i = 0; i < sensorInternal.size(); i++) {
                sensorList.add(sensorInternal.get(i).getName());
            }
        }
        sensorList.add("Location");
        return sensorList;
    }


    public void startSensor(List<DataSource> sensorList) throws InvalidSensorNameException {
        if(sensorList.isEmpty()){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be empty.");
        }
        else if(sensorList == null){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be null.");
        }
        for(int i=0; i<sensorList.size(); i++) {
            if(sensorList.get(i).getSamplingRate() == 0){
                publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), sensorList.get(i).getName());
            }
            else{
                if(sensorList.get(i).getSamplingRate() < 0){
                    throw new InvalidSensorNameException("#### Error: The The sampling rates cannot be value less than zero.");
                }
                publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), sensorList.get(i).getName(), sensorList.get(i).getSamplingRate());
            }

            listUsedSensors.add(sensorList.get(i).getName());
        }
    }


    public void stopSensor(List<DataSource> sensorList) throws InvalidSensorNameException {
        if(sensorList.isEmpty()){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be empty.");
        }
        else if(sensorList == null){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be null.");
        }
        for(int i=0; i<sensorList.size(); i++) {
            publishMessage(Topics.DEACTIVATE_SENSOR_TOPIC.toString(), sensorList.get(i).getName());
            listUsedSensors.remove(sensorList.get(i).getName());
        }
    }


    private void publishMessage(String service, String text) {
        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(text);
        message.setAvailableAttributes(1);
        publisherCore.addConnection(CDDL.getInstance().getConnection());
        publisherCore.publish(message);
    }


    private void publishMessage(String service, String text, int samplingRate) {
        int total = 2;
        Object[] value = {text, samplingRate};
        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(value);
        message.setAvailableAttributes(Integer.valueOf(total));
        publisherCore.addConnection(CDDL.getInstance().getConnection());
        publisherCore.publish(message);
    }
}
