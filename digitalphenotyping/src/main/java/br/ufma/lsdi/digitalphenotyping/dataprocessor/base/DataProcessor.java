package br.ufma.lsdi.digitalphenotyping.dataprocessor.base;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.HandlingExceptions;
import br.ufma.lsdi.digitalphenotyping.Topics;

public abstract class DataProcessor extends Service {
    private Context context;
    private String clientID;
    private String nameProcessor = "";
    private List<String> listSensors = new ArrayList();
    private List<String> listUsedSensors = new ArrayList();
    private List<String> listUsedSensorsAux = new ArrayList();
    private Publisher publisher = PublisherFactory.createPublisher();
    private DPUtilities dpUtilities;
    private DPUtilities dpUtilitiesAux;

    @Override
    public void onCreate() {
        context = this;

        this.clientID = CDDL.getInstance().getConnection().getClientId();

        publisher.addConnection(CDDL.getInstance().getConnection());

        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        dpUtilities.configSubscribers();
        if(listUsedSensorsAux.size()>0) {
            dpUtilitiesAux.configSubscribers();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() { end(); }


    public void init(){ }


    public void onSensorDataArrived(Message message){}


    public void processedDataMessage(Message message){ }


    public void sendProcessedData(Message message){
        message.setServiceName(Topics.INFERENCE_TOPIC.toString());
        message.setTopic(Topics.INFERENCE_TOPIC.toString());
        publishInference(message);
    }


    public void end(){ }


    public String toJson(Object o){
        Gson gson = new Gson();
        String json = gson.toJson(o);
        return json;
    }


    public Context getContext(){ return this.context; }


    public void setDataProcessorName(String dataProcessorName){
        if(dataProcessorName == null){
            throw new HandlingExceptions("#### Error: dataprocessorName cannot be null.");
        }
        if(dataProcessorName.isEmpty()){
            throw new HandlingExceptions("#### Error: dataprocessorName cannot be empty.");
        }
        else if(dataProcessorName.length() > 100){
            throw new HandlingExceptions("#### Error: dataprocessorName too long.");
        }
        this.nameProcessor = dataProcessorName;
    }


    public String getDataProcessorName(){ return this.nameProcessor; }


    public List<String> getSensors(){
        listSensors = CDDL.getInstance().getSensorVirtualList();
        List<Sensor> sensorInternal = CDDL.getInstance().getInternalSensorList();

        if (sensorInternal.size() != 0) {
            for (int i = 0; i < sensorInternal.size(); i++) {
                listSensors.add(sensorInternal.get(i).getName());
            }
        }
        return listSensors;
    }


    public void startSensor(List<String> sensorList){
        if(sensorList.isEmpty()){
            throw new HandlingExceptions("#### Error: Sensor list cannot be empty.");
        }
        else if(sensorList == null){
            throw new HandlingExceptions("#### Error: Sensor list cannot be null.");
        }
        for(int i=0; i<sensorList.size(); i++) {
            publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), sensorList.get(i).toString());

            listUsedSensors.add(sensorList.get(i).toString());
        }
        dpUtilities = new DPUtilities(listUsedSensors);
    }


    public void startSensor(List<String> sensorList, List<Integer> samplingRateList){
        if(sensorList.size() != samplingRateList.size()){
            throw new HandlingExceptions("#### Error: the sensor list must be the same size as the sample rate list.");
        }
        if(sensorList.isEmpty()){
            throw new HandlingExceptions("#### Error: Sensor list cannot be empty.");
        }
        else if(sensorList == null){
            throw new HandlingExceptions("#### Error: Sensor list cannot be null.");
        }
        if(samplingRateList.isEmpty()){
            throw new HandlingExceptions("#### Error: Sample rate list cannot be empty.");
        }
        else if(samplingRateList == null){
            throw new HandlingExceptions("#### Error: Sample rate list cannot be null.");
        }
        for(int i=0; i < sensorList.size(); i++) {
            publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), sensorList.get(i).toString(), samplingRateList.get(i));

            listUsedSensorsAux.add(sensorList.get(i).toString());
        }
        dpUtilitiesAux = new DPUtilities(listUsedSensorsAux);
    }


    public void stopSensor(List<String> sensorList){
        if(sensorList.isEmpty()){
            throw new HandlingExceptions("#### Error: Sensor list cannot be empty.");
        }
        else if(sensorList == null){
            throw new HandlingExceptions("#### Error: Sensor list cannot be null.");
        }
        for(int i=0; i<sensorList.size(); i++) {
            publishMessage(Topics.DEACTIVATE_SENSOR_TOPIC.toString(), sensorList.get(i).toString());

            if(listUsedSensors.contains(sensorList.get(i).toString())){
                listUsedSensors.remove(sensorList.get(i).toString());
            }
            else{
                listUsedSensorsAux.remove(sensorList.get(i).toString());
            }
        }
    }


    public void publishMessage(String service, String text) {
        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(text);
        message.setAvailableAttributes(1);
        publisher.publish(message);
    }


    public void publishMessage(String service, String text, int samplingRate) {
        int total = 2;
        Object[] value = {text, samplingRate};
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
                    Object[] valor = message.getServiceValue();
                    String mensagemRecebida = StringUtils.join(valor, ", ");
                    Object[] finalValor = {getDataProcessorName(),mensagemRecebida};
                    Log.i("TESTE","#### VALOR: " + finalValor[0] + ", " + String.valueOf(finalValor[1]));
                    message.setServiceValue(finalValor);

                    onSensorDataArrived(message);
                }
            };
        }
    }
    //public class ProcessedInformation extends Message{ }
}
