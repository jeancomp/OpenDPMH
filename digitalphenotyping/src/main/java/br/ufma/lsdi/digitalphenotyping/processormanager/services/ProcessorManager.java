package br.ufma.lsdi.digitalphenotyping.processormanager.services;

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
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.Configurations;
import br.ufma.lsdi.digitalphenotyping.MyMessage;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Sociability;

public class ProcessorManager extends Service {
    private static final String TAG = ProcessorManager.class.getName();
    Configurations configurations = Configurations.getInstance();
    Context context;
    Publisher publisher = PublisherFactory.createPublisher();

    // Variáveis dos processors
    List<String> activeProcessors = null;
    List<String> processors = null;
    Subscriber subStartProcessor;
    Subscriber subStopProcessor;

    // Variáveis dos sensores
    private String statusCon = "undefined";
    Subscriber subActive;
    Subscriber subDeactive;
    String clientID;
    int communicationTechnology = 4;
    List<String> activeSensor = null;


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
            copyProcessorsList();

            subActive = SubscriberFactory.createSubscriber();
            subActive.addConnection(CDDL.getInstance().getConnection());

            subDeactive = SubscriberFactory.createSubscriber();
            subDeactive.addConnection(CDDL.getInstance().getConnection());
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
        subscribeMessageStartProcessor(Topics.START_PROCESSOR_TOPIC.toString());
        subscribeMessageStopProcessor(Topics.STOP_PROCESSOR_TOPIC.toString());

        subscribeMessageActive(Topics.ACTIVE_SENSOR_TOPIC.toString());
        subscribeMessageDeactive(Topics.DEACTIVATE_SENSOR_TOPIC.toString().toString());

        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void startProcessorsList() {
        this.processors = new ArrayList();
        this.processors.add("Sociability");
    }


    public void copyProcessorsList(){
        Log.i(TAG, "#### Copy of processors sent to MainService");
        if(!processors.isEmpty()) {
            for (int i = 0; i < processors.size(); i++) {
                publishMessage(Topics.ADD_PLUGIN_TOPIC.toString(), processors.get(i).toString());
            }
        }
    }


    public List<String> getProcessor() {
        return this.processors;
    }


    public void subscribeMessageStartProcessor(String serviceName) {
        subStartProcessor.subscribeServiceByName(serviceName);
        subStartProcessor.setSubscriberListener(subscriberStartProcessors);
    }


    public void subscribeMessageStopProcessor(String serviceName) {
        subStopProcessor.subscribeServiceByName(serviceName);
        subStopProcessor.setSubscriberListener(subscriberStopProcessors);
    }


    public ISubscriberListener subscriberStartProcessors = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages (started Processor):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);


            if (isProcessor(atividade)) {
                Log.d(TAG, "#### Start processor monitoring->  " + atividade);
                startProcessor(atividade);
            } else {
                Log.d(TAG, "#### Invalid processor name: " + atividade);
            }
        }
    };


    public ISubscriberListener subscriberStopProcessors = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages (stop):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
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


    public ISubscriberListener subscriberPlugins = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### >>>>>>>>>>>>>>>>>>> Read messages processor:  " + message);

//            Object[] valor = message.getServiceValue();
//            String mensagemRecebida = StringUtils.join(valor, ", ");
//            Log.d(TAG, "#### " + mensagemRecebida);
//            String[] separated = mensagemRecebida.split(",");
//            String atividade = String.valueOf(separated[0]);
        }
    };


    public void subscribeMessageActive(String serviceName) {
        subActive.subscribeServiceByName(serviceName);
        subActive.setSubscriberListener(subscriberStartSensors);
    }

    public void subscribeMessageDeactive(String serviceName) {
        subDeactive.subscribeServiceByName(serviceName);
        subDeactive.setSubscriberListener(subscriberStopSensors);
    }



    public ISubscriberListener subscriberStartSensors = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages (Sensors start):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);

            if (isInternalSensor(atividade) || isVirtualSensor(atividade)) {
                Log.d(TAG, "#### Start sensor monitoring->  " + atividade);
                startSensor(atividade);
            } else {
                Log.d(TAG, "#### Invalid sensor name: " + atividade);
            }
        }
    };


    public ISubscriberListener subscriberStopSensors = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages (Sensors stop):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);


            if (isInternalSensor(atividade) || isVirtualSensor(atividade)) {
                Log.d(TAG, "#### Stop sensor monitoring->  " + atividade);
                stopSensor(atividade);
            } else {
                Log.d(TAG, "#### Invalid sensor name: " + atividade);
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

        Log.i(TAG, "\n #### Sensores virtuais disponíveis, Tamanho: \n" + s.size());
        for (int i = 0; i < s.size(); i++) {
            Log.i(TAG, "#### (" + i + "): " + s.get(i).toString());
        }
        return s;
    }


    public List<String> listInternalSensor() {
        List<String> s = new ArrayList();
        List<Sensor> sensorInternal = CDDL.getInstance().getInternalSensorList();

        Log.i(TAG, "\n #### Sensores internos disponíveis, Tamanho: \n" + sensorInternal.size());
        if (sensorInternal.size() != 0) {
            for (int i = 0; i < sensorInternal.size(); i++) {
                s.add(sensorInternal.get(i).getName());
                Log.i(TAG, "#### (" + i + "): " + sensorInternal.get(i).getName());
            }
            return s;
        }
        return s;
    }


    public void startSensor(String sensor) {
        if (sensor.equalsIgnoreCase("TouchScreen")) {
            // Solicita permissão de desenhar (canDrawOverlays) para Toque de Tela
            checkDrawOverlayPermission();
            CDDL.getInstance().startSensor(sensor, 0);
        } else {
            initPermissions(sensor);
            CDDL.getInstance().startSensor(sensor, 0);
        }
        //cddl.onStartSensor("SMS",0);
        //cddl.onStartSensor("Call",0);
        //cddl.onStartSensor("ScreenOnOff",0);
    }


    public void stopSensor(String sensor) {
        CDDL.getInstance().stopSensor(sensor);
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

        MyMessage message = new MyMessage();
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

                configurations.getInstance().getActivity().startActivityForResult(intent, 1);
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

            if (!hasPermissions(configurations.getInstance().getActivity(), PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for the sensor: " + sensor);
                ActivityCompat.requestPermissions(configurations.getInstance().getActivity(), PERMISSIONS, PERMISSION_ALL);
            }
        } else if (sensor.equalsIgnoreCase("Call")) {
            String[] PERMISSIONS = {
                    //Call
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.READ_CALL_LOG,
                    android.Manifest.permission.WRITE_CALL_LOG,
                    android.Manifest.permission.ADD_VOICEMAIL};

            if (!hasPermissions(configurations.getInstance().getActivity(), PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for the sensor: " + sensor);
                ActivityCompat.requestPermissions(configurations.getInstance().getActivity(), PERMISSIONS, PERMISSION_ALL);
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

        if (!hasPermissions(configurations.getInstance().getActivity(), PERMISSIONS)) {
            Log.i(TAG, "##### Permissão Ativada");
            ActivityCompat.requestPermissions(configurations.getInstance().getActivity(), PERMISSIONS, PERMISSION_ALL);
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

            if (!hasPermissions(configurations.getInstance().getActivity(), PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for framework");
                ActivityCompat.requestPermissions(configurations.getInstance().getActivity(), PERMISSIONS, PERMISSION_ALL);
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
