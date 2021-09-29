package br.ufma.lsdi.digitalphenotyping.processormanager.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.net.Uri;
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
import br.ufma.lsdi.digitalphenotyping.ActivityParcelable;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Mobility;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Sleep;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Sociability;

public class ProcessorManager extends Service {
    private static final String TAG = ProcessorManager.class.getName();
    private ActivityParcelable activityParcelable;
    private Activity activity;
    private Context context;
    private Publisher publisher = PublisherFactory.createPublisher();
    private List<String> listActiveProcessors = null;
    private List<String> listProcessors = null;
    private HashMap<String, Integer> listActiveSensor = new HashMap<>();
    private List<String> listSensors = new ArrayList();
    private List<String> listPlugin = new ArrayList();
    private Subscriber subStartProcessor;
    private Subscriber subStopProcessor;
    private Subscriber subActiveSensor;
    private Subscriber subDeactiveSensor;
    private Subscriber subListSensors;
    private Subscriber subAddPlugin;
    private String statusCon = "undefined";
    private String clientID;
    private int communicationTechnology = 4;


    @Override
    public void onCreate() {
        try {
            Log.i(TAG,"#### Starting ProcessorManager Service");

            context = this;

            subStartProcessor = SubscriberFactory.createSubscriber();
            subStartProcessor.addConnection(CDDL.getInstance().getConnection());

            subStopProcessor = SubscriberFactory.createSubscriber();
            subStopProcessor.addConnection(CDDL.getInstance().getConnection());

            startProcessorsList();

            subActiveSensor = SubscriberFactory.createSubscriber();
            subActiveSensor.addConnection(CDDL.getInstance().getConnection());

            subDeactiveSensor = SubscriberFactory.createSubscriber();
            subDeactiveSensor.addConnection(CDDL.getInstance().getConnection());

            subListSensors = SubscriberFactory.createSubscriber();
            subListSensors.addConnection(CDDL.getInstance().getConnection());

            subAddPlugin = SubscriberFactory.createSubscriber();
            subAddPlugin.addConnection(CDDL.getInstance().getConnection());

            activityParcelable = new ActivityParcelable();
        }catch (Exception e){
            Log.e(TAG,"Error: " + e.toString());
        }
    }


    public synchronized void startProcessor(String nameProcessor) {
        try {
            if(nameProcessor.equalsIgnoreCase("Sociability")) {
                Intent s = new Intent(context, Sociability.class);
                context.startService(s);
                Log.i(TAG, "#### Starting inference services: Sociability");
            }
            else if(nameProcessor.equalsIgnoreCase("Mobility")) {
                Intent s = new Intent(context, Mobility.class);
                context.startService(s);
                Log.i(TAG, "#### Starting inference services: Mobility");
            }
            else if(nameProcessor.equalsIgnoreCase("Sleep")) {
                Intent s = new Intent(context, Sleep.class);
                context.startService(s);
                Log.i(TAG, "#### Starting inference services: Sleep");
            }
            publishMessage(Topics.ACTIVE_PROCESSOR_TOPIC.toString(),nameProcessor);
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    public synchronized void stopProcessor(String nameProcessor) {
        try {
            if(nameProcessor.equalsIgnoreCase("Sociability")) {
                Intent s = new Intent(context, Sociability.class);
                context.stopService(s);
                Log.i(TAG, "#### Stopping inference services");
            }
            else if(nameProcessor.equalsIgnoreCase("Mobility")) {
                Intent s = new Intent(context, Mobility.class);
                context.stopService(s);
                Log.i(TAG, "#### Stopping inference services");
            }
            publishMessage(Topics.DEACTIVATE_PROCESSOR_TOPIC.toString(),nameProcessor);
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

        activityParcelable = (ActivityParcelable) intent.getParcelableExtra("activity");
        activity = activityParcelable.getActivity();

        subscribeMessageStartProcessor(Topics.START_PROCESSOR_TOPIC.toString());
        subscribeMessageStopProcessor(Topics.STOP_PROCESSOR_TOPIC.toString());

        subscribeMessageActiveSensor(Topics.ACTIVE_SENSOR_TOPIC.toString());
        subscribeMessageDeactiveSensor(Topics.DEACTIVATE_SENSOR_TOPIC.toString());
        subscribeMessageListSensors(Topics.LIST_SENSORS_TOPIC.toString());

        subscribeMessageAddPlugin(Topics.ADD_PLUGIN_TOPIC.toString());

        listSensors.addAll(listInternalSensor());
        listSensors.addAll(listVirtualSensor());

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void startProcessorsList() {
        this.listProcessors = new ArrayList();
        this.listProcessors.add("Sociability");
        this.listProcessors.add("Mobility");
        this.listProcessors.add("Sleep");
    }


    public List<String> getProcessor() {
        return this.listProcessors;
    }


    public void subscribeMessageStartProcessor(String serviceName) {
        subStartProcessor.subscribeServiceByName(serviceName);
        subStartProcessor.setSubscriberListener(subscriberStartProcessors);
    }


    public void subscribeMessageStopProcessor(String serviceName) {
        subStopProcessor.subscribeServiceByName(serviceName);
        subStopProcessor.setSubscriberListener(subscriberStopProcessors);
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


    public ISubscriberListener subscriberStartProcessors = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
            Log.i(TAG, "#### Read messages (started Processor):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);


            if (isProcessor(atividade)) {
                Log.i(TAG, "#### Start processor monitoring->  " + atividade);
                startProcessor(atividade);
            } else {
                Log.i(TAG, "#### Invalid processor name: " + atividade);
            }
        }
    };


    public ISubscriberListener subscriberStopProcessors = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.i(TAG, "#### Read messages (stop):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);


            if (isProcessor(atividade)) {
                Log.i(TAG, "#### Stop processor monitoring->  " + atividade);
                stopProcessor(atividade);
            } else {
                Log.i(TAG, "#### Invalid processor name: " + atividade);
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
            String atividade = String.valueOf(separated[0]);

            if (isInternalSensor(atividade) || isVirtualSensor(atividade)) {
                Log.i(TAG, "#### Start sensor monitoring->  " + atividade);

                Log.i(TAG, "#### Total getAvailableAttributes: " + message.getAvailableAttributes());
                if(message.getAvailableAttributes() >= 2){
                    Double n = (Double) valor[1];
                    int delay = n.intValue();
                    Log.i(TAG, "#### Delay: " + delay);
                    startSensor(atividade, delay);
                }
                else {
                    startSensor(atividade);
                }
            } else {
                Log.i(TAG, "#### Invalid sensor name: " + atividade);
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
            String atividade = String.valueOf(separated[0]);


            if (isInternalSensor(atividade) || isVirtualSensor(atividade)) {
                Log.i(TAG, "#### Stop sensor monitoring->  " + atividade);
                stopSensor(atividade);
            } else {
                Log.i(TAG, "#### Invalid sensor name: " + atividade);
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
                Object[] list = listSensors.toArray();
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
            String atividade = String.valueOf(separated[0]);

            if (!listPlugin.contains(atividade)) {
                listPlugin.add(atividade);
            } else {
                Log.e(TAG, "#### Processor already exists: " + atividade);
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
            return s;
        }
        return s;
    }


    public void startSensor(String nameSensor) {
        try {
            if(checkSensorUsageforStart(nameSensor)) {
                if (nameSensor.equalsIgnoreCase("TouchScreen")) {
                    Log.i(TAG,"#### Aquiiiiiiiiiiiiiii");
                    // Solicita permissão de desenhar (canDrawOverlays) para Toque de Tela: a permissão no smartphone é sobreposição,
                    // entra na configuração do aplicativo e ativa a opção "Sobreposição a outros aplicativos".
                    // Existe um mode de configurar isso ao usar o sensor de Toque de tela.
                    checkDrawOverlayPermission();
                    CDDL.getInstance().startSensor(nameSensor, 0);
                } else {
                    initPermissions(nameSensor);
                    CDDL.getInstance().startSensor(nameSensor, 0);
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
     * @param nameSensor Name of the sensor to be listened to
     * @param delay
     */
    public void startSensor(String nameSensor, int delay) {
        try {
            if (checkSensorUsageforStart(nameSensor)) {
                if (nameSensor.equalsIgnoreCase("TouchScreen")) {
                    // Solicita permissão de desenhar (canDrawOverlays) para Toque de Tela
                    checkDrawOverlayPermission();
                    CDDL.getInstance().startSensor(nameSensor, 0);
                } else {
                    initPermissions(nameSensor);
                    CDDL.getInstance().startSensor(nameSensor, delay);
                }
            }
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
        //cddl.onStartSensor("SMS",0);
        //cddl.onStartSensor("Call",0);
        //cddl.onStartSensor("ScreenOnOff",0);
    }


    public void stopSensor(String nameSensor) {
        if(checkSensorUsageforStop(nameSensor)) {
            CDDL.getInstance().stopSensor(nameSensor);
        }
    }


    /**
     *
     * @param nameSensor
     * @return
     */
    public boolean checkSensorUsageforStart(String nameSensor){
        try{
            if(!listActiveSensor.containsKey(nameSensor)){
                listActiveSensor.put(nameSensor, 1);
                return true;
            }
            else{ // Already have processors using the sensor.
                Integer value = listActiveSensor.get(nameSensor);
                value = value + 1;
                listActiveSensor.put(nameSensor, value);
            }
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
        return false;
    }


    public boolean checkSensorUsageforStop(String nameSensor){
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


    private Boolean isProcessor(String nameProcessor) {
        if (listProcessors().contains(nameProcessor)) {
            return true;
        }
        return false;
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


    public List<String> listProcessors() {
        List<String> s = getProcessor();

        Log.i(TAG, "\n #### Available Data Processors, Size: \n" + s.size());
        for (int i = 0; i < s.size(); i++) {
            Log.i(TAG, "#### (" + i + "): " + s.get(i).toString());
        }
        return s;
    }


    //Implementar
    public void startAllProcessors() {
    }


    public Context getContext(){
        return this.context;
    }
}
