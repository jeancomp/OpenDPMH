package br.ufma.lsdi.digitalphenotyping.dataprocessor.base;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.IBinder;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dp.handlingexceptions.InvalidDataProcessorNameException;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.handlingexceptions.InvalidSensorNameException;

public abstract class DataProcessor extends Service {
    private Context context;
    private String clientID;
    private String dataProcessorName = null;
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

        if(getDataProcessorName() == null) {
            try {
                throw new InvalidDataProcessorNameException("#### Error: invalid dataProcessorName, cannot be null.");
            } catch (InvalidDataProcessorNameException e) {
                e.printStackTrace();
            }
        }
        if(listUsedSensors.size()>0){
            dpUtilities.configSubscribers();
        }
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
    public void onDestroy() {
        end();
        try {
            if(!listUsedSensors.isEmpty()) {
                stopSensor(listUsedSensors);
            }
            if(!listUsedSensorsAux.isEmpty()) {
                stopSensor(listUsedSensorsAux);
            }
        } catch (InvalidSensorNameException e) {
            e.printStackTrace();
        }
    }


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


    public void setDataProcessorName(String dataProcessorName) throws InvalidDataProcessorNameException {
        if(dataProcessorName == null){
            throw new InvalidDataProcessorNameException("#### Error: dataprocessorName cannot be null.");
        }
        if(dataProcessorName.isEmpty()){
            throw new InvalidDataProcessorNameException("#### Error: dataprocessorName cannot be empty.");
        }
        else if(dataProcessorName.length() > 100){
            throw new InvalidDataProcessorNameException("#### Error: dataprocessorName too long.");
        }
        this.dataProcessorName = dataProcessorName;
    }


    public String getDataProcessorName(){ return this.dataProcessorName; }


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


    public void startSensor(List<String> sensorList) throws InvalidSensorNameException {
        if(sensorList.isEmpty()){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be empty.");
        }
        else if(sensorList == null){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be null.");
        }
        for(int i=0; i<sensorList.size(); i++) {
            publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), sensorList.get(i).toString());

            listUsedSensors.add(sensorList.get(i).toString());
        }
        dpUtilities = new DPUtilities(listUsedSensors);
    }


    public void startSensor(List<String> sensorList, List<Integer> samplingRateList) throws InvalidSensorNameException {
        if(sensorList.size() != samplingRateList.size()){
            throw new InvalidSensorNameException("#### Error: the sensor list must be the same size as the sample rate list.");
        }
        if(sensorList.isEmpty()){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be empty.");
        }
        else if(sensorList == null){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be null.");
        }
        if(samplingRateList.isEmpty()){
            throw new InvalidSensorNameException("#### Error: Sample rate list cannot be empty.");
        }
        else if(samplingRateList == null){
            throw new InvalidSensorNameException("#### Error: Sample rate list cannot be null.");
        }
        for(int i=0; i < sensorList.size(); i++) {
            publishMessage(Topics.ACTIVE_SENSOR_TOPIC.toString(), sensorList.get(i).toString(), samplingRateList.get(i));

            listUsedSensorsAux.add(sensorList.get(i).toString());
        }
        dpUtilitiesAux = new DPUtilities(listUsedSensorsAux);
    }


    private void stopSensor(List<String> sensorList) throws InvalidSensorNameException {
        if(sensorList.isEmpty()){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be empty.");
        }
        else if(sensorList == null){
            throw new InvalidSensorNameException("#### Error: Sensor list cannot be null.");
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


    private void publishMessage(String service, String text) {
        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(text);
        message.setAvailableAttributes(1);
        publisher.publish(message);
    }


    private void publishMessage(String service, String text, int samplingRate) {
        int total = 2;
        Object[] value = {text, samplingRate};
        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(value);
        message.setAvailableAttributes(Integer.valueOf(total));
        publisher.publish(message);
        //Log.i(TAG,"#### RRRRRRRRRRR: " + message.getAvailableAttributes());
    }


    private void publishInference(Message message){
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
                    //Add processor name
                    Object[] valor1 = message.getServiceValue();
                    String mensagemRecebida1 = StringUtils.join(valor1, ", ");
                    Object[] finalValor1 = {getDataProcessorName(),mensagemRecebida1};

                    message.setAvailableAttributes(message.getAvailableAttributes() + 1);
                    String[] valor2 = message.getAvailableAttributesList();
                    String mensagemRecebida2 = StringUtils.join(valor2, ", ");
                    String[] finalValor2 = {"Processor Name",mensagemRecebida2};

                    message.setAvailableAttributesList(finalValor2);
                    message.setServiceValue(finalValor1);

                    onSensorDataArrived(message);
                }
            };
        }
    }
    //public class ProcessedInformation extends Message{ }


    public static class DigitalPhenotypeEvent {
        private static DigitalPhenotypeEvent instance = null;
        private String uid = "";
        private Situation situation = null;
        private LocalDateTime startDateTime= null;
        private LocalDateTime endDateTime = null;
        private List<Attribute> attributes = new ArrayList();

        public DigitalPhenotypeEvent(){ }

        public static DigitalPhenotypeEvent getInstance() {
            if (instance == null) {
                instance = new DigitalPhenotypeEvent();
            }
            return instance;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public Situation getSituation() {
            return situation;
        }

        public void setSituation(Situation situation) {
            this.situation = situation;
        }

        public LocalDateTime getStartDateTime() {
            return startDateTime;
        }

        public void setStartDateTime(LocalDateTime startDateTime) {
            this.startDateTime = startDateTime;
        }

        public LocalDateTime getEndDateTime() {
            return endDateTime;
        }

        public void setEndDateTime(LocalDateTime endDateTime) {
            this.endDateTime = endDateTime;
        }

        public List<Attribute> getAttributes() {
            return attributes;
        }

        public void setAttributes(String label, String value, String type, boolean qualityAttribute) {
            Attribute attribute = new Attribute();
            attribute.setLabel(label);
            attribute.setValue(value);
            attribute.setType(type);
            attribute.setQualityAttribute(qualityAttribute);
            this.attributes.add(attribute);
        }

        public void destroy(){
            setUid(null);
            setSituation(null);
            setStartDateTime(null);
            setEndDateTime(null);
            setAttributes("","","",false);
            instance = null;
        }
    }


    public class Situation{
        private String label = "";  // (e.g., Estacionário, Correndo, Andando)
        private String description = "";

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }


    public static class Attribute{
        private String label = "";
        private String value = "";
        private String type = "";
        private boolean qualityAttribute = false;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean isQualityAttribute() {
            return qualityAttribute;
        }

        public void setQualityAttribute(boolean qualityAttribute) {
            this.qualityAttribute = qualityAttribute;
        }
    }
}
