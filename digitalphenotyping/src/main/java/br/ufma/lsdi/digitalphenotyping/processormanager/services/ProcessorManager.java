package br.ufma.lsdi.digitalphenotyping.processormanager.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.SaveActivity;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Mobility;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Sleep;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Sociability;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidDataProcessorNameException;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessorManager;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessorManager;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.handlingexceptions.InvalidPluginException;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.handlingexceptions.InvalidSensorNameException;
import br.ufma.lsdi.digitalphenotyping.rawdatacollector.RawDataCollector;


/*
Observação:
Criar HashMap<String, String> sensorXDataprocessor.
Essa tabela conterá os nomes dos sensores versos os dataprocessor, para quando der uma exceção
em tempo de execução de um start sensor ou um stop sensor, mostrar quem são os dataprocessor que
estão compartilhando o mesmo sensor.
* */

public class ProcessorManager extends Service {
    private static final String TAG = ProcessorManager.class.getName();
    //private ActivityParcelable activityParcelable;
    private Publisher publisher = PublisherFactory.createPublisher();
    private List<String> listDataProcessors = new ArrayList();
    private List<String> listActiveDataProcessors = new ArrayList();
    private HashMap<String, Integer> listActiveSensor = new HashMap<>();
    private SaveActivity saveActivity = SaveActivity.getInstance();
    private ActiveDataProcessorManager activeDataProcessorManager = ActiveDataProcessorManager.getInstance();
    private ListDataProcessorManager listDataProcessorManager = ListDataProcessorManager.getInstance();
    private List<String> sensorList = new ArrayList();
    private List<String> pluginList = new ArrayList();
    private Subscriber subStartDataProcessor;
    private Subscriber subStopDataProcessor;
    private Subscriber subActiveSensor;
    private Subscriber subDeactiveSensor;
    private Subscriber subListSensors;
    private Subscriber subAddPlugin;
    private Activity activity;
    private Context context;


    @Override
    public void onCreate() {
        try {
            Log.i(TAG,"#### Starting ProcessorManager Service");

            context = this;

            subStartDataProcessor = SubscriberFactory.createSubscriber();
            subStartDataProcessor.addConnection(CDDL.getInstance().getConnection());

            subStopDataProcessor = SubscriberFactory.createSubscriber();
            subStopDataProcessor.addConnection(CDDL.getInstance().getConnection());

            startDataProcessorsList();

            subActiveSensor = SubscriberFactory.createSubscriber();
            subActiveSensor.addConnection(CDDL.getInstance().getConnection());

            subDeactiveSensor = SubscriberFactory.createSubscriber();
            subDeactiveSensor.addConnection(CDDL.getInstance().getConnection());

            subListSensors = SubscriberFactory.createSubscriber();
            subListSensors.addConnection(CDDL.getInstance().getConnection());

            subAddPlugin = SubscriberFactory.createSubscriber();
            subAddPlugin.addConnection(CDDL.getInstance().getConnection());

            //activeDataProcessorManager = new ActiveDataProcessorManager(context);
            //listDataProcessorManager = new ListDataProcessorManager(context);

            //activityParcelable = new ActivityParcelable();
        }catch (Exception e){
            Log.e(TAG,"Error: " + e.toString());
        }
    }


    public synchronized void startDataProcessor(String dataProcessorName) {
        try {
            if(dataProcessorName.equalsIgnoreCase("RawDataCollector")) {
                Intent s = new Intent(context, RawDataCollector.class);
                context.startService(s);
                Log.i(TAG, "#### Starting services: RawDataCollector");
            }
            else if(dataProcessorName.equalsIgnoreCase("Sociability")) {
                Intent s = new Intent(context, Sociability.class);
                context.startService(s);
                Log.i(TAG, "#### Starting inference services: Sociability");
            }
            else if(dataProcessorName.equalsIgnoreCase("Mobility")) {
                Intent s = new Intent(context, Mobility.class);
                context.startService(s);
                Log.i(TAG, "#### Starting inference services: Mobility");
            }
            else if(dataProcessorName.equalsIgnoreCase("Sleep")) {
                Intent s = new Intent(context, Sleep.class);
                context.startService(s);
                Log.i(TAG, "#### Starting inference services: Sleep");
            }
            activeDataProcessorManager.getInstance().insert(dataProcessorName);
            publishMessage("aliveNewDataProcessor");
            listActiveDataProcessors.add(dataProcessorName); // Update active DataProcessor list
            publishMessage(Topics.ACTIVE_DATAPROCESSOR_TOPIC.toString(),dataProcessorName); //PhenotypeComposer recebe informação que o processor foi ativado.
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    public synchronized void stopDataProcessor(String dataProcessorName) {
        try {
            if(dataProcessorName.equalsIgnoreCase("RawDataCollector")) {
                Intent s = new Intent(context, RawDataCollector.class);
                context.stopService(s);
                Log.i(TAG, "#### Stopping services");
            }
            else if(dataProcessorName.equalsIgnoreCase("Sociability")) {
                Intent s = new Intent(context, Sociability.class);
                context.stopService(s);
                Log.i(TAG, "#### Stopping inference services");
            }
            else if(dataProcessorName.equalsIgnoreCase("Mobility")) {
                Intent s = new Intent(context, Mobility.class);
                context.stopService(s);
                Log.i(TAG, "#### Stopping inference services");
            }
            else if(dataProcessorName.equalsIgnoreCase("Sleep")) {
                Intent s = new Intent(context, Sleep.class);
                context.stopService(s);
                Log.i(TAG, "#### Stopping inference services: Sleep");
            }
            activeDataProcessorManager.getInstance().delete(dataProcessorName);
            publishMessage("aliveRemoveDataProcessor");
            listActiveDataProcessors.remove(dataProcessorName);
            publishMessage(Topics.DEACTIVATE_DATAPROCESSOR_TOPIC.toString(),dataProcessorName);
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    public final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public ProcessorManager getService() {
            return ProcessorManager.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "#### CONFIGURATION PROCESSORMANAGER SERVICE");
        super.onStartCommand(intent, flags, startId);

        //activityParcelable = (ActivityParcelable) intent.getParcelableExtra("activity");
        //activity = activityParcelable.getActivity();
        activity = saveActivity.getActivity();

        subscribeMessageStartDataProcessor(Topics.START_DATAPROCESSOR_TOPIC.toString());
        subscribeMessageStopDataProcessor(Topics.STOP_DATAPROCESSOR_TOPIC.toString());

        subscribeMessageActiveSensor(Topics.ACTIVE_SENSOR_TOPIC.toString());
        subscribeMessageDeactiveSensor(Topics.DEACTIVATE_SENSOR_TOPIC.toString());
        subscribeMessageListSensors(Topics.LIST_SENSORS_TOPIC.toString());

        subscribeMessageAddPlugin(Topics.ADD_PLUGIN_TOPIC.toString());

        sensorList.addAll(listInternalSensor());
        sensorList.addAll(listVirtualSensor());

        publishMessage(Topics.MAINSERVICE_CONFIGURATION_INFORMATION_TOPIC.toString(), "alive");

        saveDatabaseDataProcessorList();

        return START_STICKY;
    }


    public void saveDatabaseDataProcessorList(){
        List<String> backupDataProcessorList = getDataProcessors();
        /*Thread thread = new Thread() {
            @Override
            public void run() {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();*/

        Log.i(TAG,"#### TTTT: " + backupDataProcessorList.size());
        //Save in database
        for(int i=0; i < backupDataProcessorList.size(); i++){
            //listDataProcessorManager.getInstance().insert(backupDataProcessorList.get(i).toString());
            new AddItemTask().execute(backupDataProcessorList.get(i).toString());
        }
    }


    /*public void addNewItemToDatabase(String item){
        new AddItemTask().execute(item);
    }*/

    private class AddItemTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... item) {
            listDataProcessorManager.getInstance().insert(item[0]);
            Log.i(TAG,"#### w: " + listDataProcessorManager.getInstance().totalRecords() + ", name: " + item[0]);
            return null;
        }
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void subscribeMessageStartDataProcessor(String serviceName) {
        subStartDataProcessor.subscribeServiceByName(serviceName);
        subStartDataProcessor.setSubscriberListener(subscriberStartDataProcessors);
    }


    public void subscribeMessageStopDataProcessor(String serviceName) {
        subStopDataProcessor.subscribeServiceByName(serviceName);
        subStopDataProcessor.setSubscriberListener(subscriberStopDataProcessors);
    }


    public void subscribeMessageActiveSensor(String serviceName) {
        subActiveSensor.subscribeServiceByName(serviceName);
        subActiveSensor.setSubscriberListener(subscriberStartSensors);
    }


    public void subscribeMessageDeactiveSensor(String serviceName) {
        subDeactiveSensor.subscribeServiceByName(serviceName);
        subDeactiveSensor.setSubscriberListener(subscriberStopSensors);
    }


    public void subscribeMessageListSensors(String serviceName) {
        subListSensors.subscribeServiceByName(serviceName);
        subListSensors.setSubscriberListener(subscriberListSensors);
    }


    public void subscribeMessageAddPlugin(String serviceName) {
        subAddPlugin.subscribeServiceByName(serviceName);
        subAddPlugin.setSubscriberListener(subscriberAddPlugin);
    }


    public ISubscriberListener subscriberStartDataProcessors = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
            Log.i(TAG, "#### Read messages (started Processor):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            String[] separated = mensagemRecebida.split(",");
            String dataProcessorName = String.valueOf(separated[0]);

            if (isDataProcessor(dataProcessorName)) {
                if(isDataProcessorActive(dataProcessorName)) {
                    Log.i(TAG, "#### Start processor monitoring->  " + dataProcessorName);
                    startDataProcessor(dataProcessorName);
                }
                else{
                    try {
                        throw new InvalidDataProcessorNameException("#### Error: dataProcessorName is already activated.");
                    } catch (InvalidDataProcessorNameException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    throw new InvalidDataProcessorNameException("#### Error: Invalid processor name.");
                } catch (InvalidDataProcessorNameException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public ISubscriberListener subscriberStopDataProcessors = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.i(TAG, "#### Read messages (stop):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            String[] separated = mensagemRecebida.split(",");
            String dataProcessorName = String.valueOf(separated[0]);


            if (isDataProcessor(dataProcessorName)) {
                if(!isDataProcessorActive(dataProcessorName)) {
                    Log.i(TAG, "#### Stop processor monitoring->  " + dataProcessorName);
                    stopDataProcessor(dataProcessorName);
                }
                else{
                    try {
                        throw new InvalidDataProcessorNameException("#### Error: dataProcessorName is already deactivated.");
                    } catch (InvalidDataProcessorNameException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    throw new InvalidDataProcessorNameException("#### Error: Invalid processor name.");
                } catch (InvalidDataProcessorNameException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public ISubscriberListener subscriberStartSensors = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
            Log.i(TAG, "#### Read messages (Sensors start):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            String[] separated = mensagemRecebida.split(",");
            String nameSensor = String.valueOf(separated[0]);

            if (isInternalSensor(nameSensor) || isVirtualSensor(nameSensor)) {
                Log.i(TAG, "#### Start sensor monitoring->  " + nameSensor);

                Log.i(TAG, "#### Total getAvailableAttributes: " + message.getAvailableAttributes());
                if(message.getAvailableAttributes() >= 2){
                    Double n = (Double) valor[1];
                    int delay = n.intValue();
                    Log.i(TAG, "#### Sampling rate: " + delay);
                    startSensor(nameSensor, delay);
                }
                else {
                    startSensor(nameSensor);
                }
            } else {
                try {
                    throw new InvalidSensorNameException("#### Error: Invalid sensor name: " + nameSensor);
                } catch (InvalidSensorNameException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public ISubscriberListener subscriberStopSensors = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
            Log.i(TAG, "#### Read messages (Sensors stop):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            String[] separated = mensagemRecebida.split(",");
            String nameSensor = String.valueOf(separated[0]);


            if (isInternalSensor(nameSensor) || isVirtualSensor(nameSensor)) {
                Log.i(TAG, "#### Stop sensor monitoring->  " + nameSensor);
                stopSensor(nameSensor);
            } else {
                try {
                    throw new InvalidSensorNameException("#### Warning: Invalid sensor name: " + nameSensor);
                } catch (InvalidSensorNameException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public ISubscriberListener subscriberListSensors = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);
            if(atividade.equals("alive")) {
                //Send processor list to Dataprocessor
                Object[] list = sensorList.toArray();
                publisher.addConnection(CDDL.getInstance().getConnection());
                Message msg = new Message();
                msg.setServiceName("listsensors");
                msg.setServiceValue(list);
                publisher.publish(msg);
            }
        }
    };


    public ISubscriberListener subscriberAddPlugin = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
            Log.i(TAG, "#### Read messages (Add Plugin):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            String[] separated = mensagemRecebida.split(",");
            String pluginName = String.valueOf(separated[0]);

            if (!pluginList.contains(pluginName)) {
                pluginList.add(pluginName);
            }
            else {
                try {
                    throw new InvalidPluginException("#### Error: Processor already exists.");
                } catch (InvalidPluginException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private Boolean isInternalSensor(String sensor) {
        if (listInternalSensor().contains(sensor)) {
            return true;
        }
        return false;
    }


    private Boolean isVirtualSensor(String sensor) {
        if (listVirtualSensor().contains(sensor)) {
            return true;
        }
        return false;
    }


    public List<String> listVirtualSensor() {
        List<String> s = CDDL.getInstance().getSensorVirtualList();
        return s;
    }


    public List<String> listInternalSensor() {
        List<String> s = new ArrayList();
        List<Sensor> sensorInternal = CDDL.getInstance().getInternalSensorList();

        if (sensorInternal.size() != 0) {
            for (int i = 0; i < sensorInternal.size(); i++) {
                s.add(sensorInternal.get(i).getName());
            }
            //return s;
        }
        s.add("Location");
        return s;
    }


    public void startSensor(String sensorName) {
        try {
            addSensorUsage(sensorName);
                if (sensorName.equalsIgnoreCase("TouchScreen")) {
                    // Solicita permissão de desenhar (canDrawOverlays) para Toque de Tela: a permissão no smartphone é sobreposição,
                    // entra na configuração do aplicativo e ativa a opção "Sobreposição a outros aplicativos".
                    // Existe um mode de configurar isso ao usar o sensor de Toque de tela.
                    checkDrawOverlayPermission();
                    CDDL.getInstance().startSensor(sensorName, 0);
                } else {
                    initPermissions(sensorName);
                    if(sensorName.equals("Location")){
                        CDDL.getInstance().startLocationSensor();
                    }
                    else {
                        CDDL.getInstance().startSensor(sensorName, 0);
                    }
                }
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    /**
     * Starts listening for the specified sensor but specifying a type of delay.
     * The type of delay can be:
     *   Fastest delay = 0,
     *   Game delay = 1,
     *   UI delay = 2,
     *   Normal delay = 3
     * @param sensorName Name of the sensor to be listened to
     * @param samplingRate
     */
    public void startSensor(String sensorName, int samplingRate) {
        try {
            addSensorUsage(sensorName);
                if (sensorName.equalsIgnoreCase("TouchScreen")) {
                    // Solicita permissão de desenhar (canDrawOverlays) para Toque de Tela
                    checkDrawOverlayPermission();
                    CDDL.getInstance().startSensor(sensorName, 0);
                } else {
                    initPermissions(sensorName);
                    if(sensorName.equals("Location")){
                        CDDL.getInstance().startLocationSensor(samplingRate);
                    }
                    else {
                        CDDL.getInstance().startSensor(sensorName, samplingRate);
                    }
                }
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
        //cddl.onStartSensor("SMS",0);
        //cddl.onStartSensor("Call",0);
        //cddl.onStartSensor("ScreenOnOff",0);
    }


    public void stopSensor(String sensorName) {
        if(removeSensorUsageforStop(sensorName)) {
            if(sensorName.equals("Location")){
                CDDL.getInstance().stopLocationSensor();
            }
            else {
                CDDL.getInstance().stopSensor(sensorName);
            }
        }
    }


    /**
     * Contains all data processor modules available for use.
     */
    public void startDataProcessorsList() {
        this.listDataProcessors.add("RawDataCollector");
        this.listDataProcessors.add("Sociability");
        this.listDataProcessors.add("Mobility");
        this.listDataProcessors.add("Sleep");
    }


    public List<String> getDataProcessors() {
        return listDataProcessors;
    }


    public List<String> getListActiveDataProcessors(){
        return listActiveDataProcessors;
    }


    /**
     * Check if dataProcessorName is in the list of available processors.
     * @param dataProcessorName
     * @return true or false
     */
    private Boolean isDataProcessor(String dataProcessorName) {
        if (listDataProcessors.contains(dataProcessorName)) {
            return true;
        }
        return false;
    }


    /**
     * Returns false if dataProcessor is active, if not, returns true.
     * @param dataProcessorName
     * @return true or false
     */
    public boolean isDataProcessorActive(String dataProcessorName){
        if(listActiveDataProcessors.contains(dataProcessorName)){
            return false;
        }
        return true;
    }


    /**
     * Manages the list of active sensors, if nameSensor is being used by another dataProcessor,
     * returns true and increment by 1, otherwise returns false.
     * @param nameSensor
     * @return true or false
     */
    public void addSensorUsage(String nameSensor){
            if(!listActiveSensor.containsKey(nameSensor)){
                listActiveSensor.put(nameSensor, 1);
                //return true;
            }
            else{ // Already have processors using the sensor.
                Integer value = listActiveSensor.get(nameSensor);
                value = value + 1;
                listActiveSensor.put(nameSensor, value);
            }
    }


    /**
     * Manages the list of active sensors, if nameSensor is being used by another dataProcessor,
     * returns true and decrements by 1, otherwise return false and arrow to 0, indicating that no
     * dataProcessor is using the nameSensor.
     * @param nameSensor
     * @return true or false
     */
    public boolean removeSensorUsageforStop(String nameSensor){
        try {
            if (!listActiveSensor.isEmpty()) {
                if (listActiveSensor.containsKey(nameSensor)) {
                    Integer value = listActiveSensor.get(nameSensor);
                    if (value == 1) { // Only one processor is using.
                        listActiveSensor.remove(nameSensor);
                        return true;
                    } else { // More than one processor is using the sensor.
                        value = value - 1;
                        listActiveSensor.put(nameSensor, value);
                    }
                }
            }
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
        return false;
    }


    public void startAllVirtualSensors() {
        // solicita permissão ao  usuário
        initAllPermissions();

        //Start sensores virtuais pelo nome e delay
        CDDL.getInstance().startSensor("SMS", 0);
        CDDL.getInstance().startSensor("Call", 0);
        CDDL.getInstance().startSensor("ScreenOnOff", 0);

        // Solicita permissão de desenhar (canDrawOverlays) para Toque de Tela
        checkDrawOverlayPermission();
        CDDL.getInstance().startSensor("TouchScreen", 0);
    }


    public void publishMessage(String service, String text) {
        publisher.addConnection(CDDL.getInstance().getConnection());

        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(text);
        publisher.publish(message);
    }


    public void publishMessage(String text) {
        publisher.addConnection(CDDL.getInstance().getConnection());

        Message message = new Message();
        message.setServiceName(Topics.NOTIFICATION.toString());
        message.setTopic(Topics.NOTIFICATION.toString());
        message.setServiceValue(text);
        publisher.publish(message);
    }


    private void checkDrawOverlayPermission() {
        Log.i(TAG, "#### Permissao para o sensor TouchScreen");
        // check if we already  have permission to draw over other apps
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this.context)) {
                // if not construct intent to request permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + this.context.getPackageName()));
                //startService(intent);
                // request permission via start activity for result
                Log.i(TAG, "#### permissao dada pelo usuário");

                activity.startActivityForResult(intent, 1);
                //configurations.getInstance().getActivity().startActivityForResult(intent, 1);
            }
        }
    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void initPermissions(String sensor) {
        // Checa as permissões para rodar os sensores virtuais
        int PERMISSION_ALL = 1;

        if (sensor.equalsIgnoreCase("SMS")) {
            String[] PERMISSIONS = {
                    // SMS entrada
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.RECEIVE_SMS,
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.WRITE_CONTACTS,
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.GET_ACCOUNTS,
                    // SMS saída
                    android.Manifest.permission.READ_EXTERNAL_STORAGE};

            if (!hasPermissions(activity, PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for the sensor: " + sensor);
                ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
            }
        } else if (sensor.equalsIgnoreCase("Call")) {
            String[] PERMISSIONS = {
                    //Call
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.READ_CALL_LOG,
                    android.Manifest.permission.WRITE_CALL_LOG,
                    android.Manifest.permission.ADD_VOICEMAIL};

            if (!hasPermissions(activity, PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for the sensor: " + sensor);
                ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
            }
        } else if (sensor.equalsIgnoreCase("Location")) {
            String[] PERMISSIONS = {
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION};

            if (!hasPermissions(activity, PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for the sensor: " + sensor);
                ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
            }
        }
//        String[] PERMISSIONS = {
//                // SMS entrada
//                android.Manifest.permission.SEND_SMS,
//                android.Manifest.permission.RECEIVE_SMS,
//                android.Manifest.permission.READ_SMS,
//
//                // SMS saída
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
//
//                //Call
//                android.Manifest.permission.READ_PHONE_STATE,
//                android.Manifest.permission.CALL_PHONE,
//                android.Manifest.permission.READ_CALL_LOG,
//                android.Manifest.permission.WRITE_CALL_LOG,
//                android.Manifest.permission.ADD_VOICEMAIL,
//
//                // Escrita no storage Certificado Digital
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//
//                // Para usar o GPS
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//        };
    }


    private void initAllPermissions() {
        // Checa as permissões para rodar os sensores virtuais
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                // SMS
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_SMS,

                //Call
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.WRITE_CALL_LOG,
                android.Manifest.permission.ADD_VOICEMAIL,

                // SMS saída
                android.Manifest.permission.READ_EXTERNAL_STORAGE,

                // Escrita no storage Certificado Digital
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,

                // Para usar o GPS
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (!hasPermissions(activity, PERMISSIONS)) {
            Log.i(TAG, "##### Permissão Ativada");
            ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
        }
    }


    private void initPermissionsRequired() {
        // Checa as permissões para rodar os sensores virtuais
        int PERMISSION_ALL = 1;

        if (true) {
            String[] PERMISSIONS = {
                    // Service location
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION

                    // Outros services
            };

            if (!hasPermissions(activity, PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for framework");
                ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
            }
        }
    }


    //Implementar
    public void startAllDataProcessors() {
    }


    public Context getContext(){
        return this.context;
    }
}
