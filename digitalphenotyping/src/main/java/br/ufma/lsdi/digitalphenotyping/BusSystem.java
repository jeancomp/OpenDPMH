package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.util.List;
import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.listeners.IConnectionListener;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.network.SecurityService;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.inferenceprocessormanager.services.InferenceProcessorManager;

import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

// Extends SERVICE e que seja em primeiro plano
public class BusSystem extends Service {
    private CDDL cddl;
    private String clientID;
    private ConnectionImpl con;
    private Context context;
    private Activity activity;
    private TextView messageTextView;
    private String statusConnection = "";
    private int communicationTechnology = 4;
    private String nameCaCertificate = "rootCA.crt";
    private String nameClientCertificate = "client.crt";
    private static final String TAG = BusSystem.class.getName();
    //private static BusSystem instance = null;
    Subscriber subActive;
    Publisher publisher = PublisherFactory.createPublisher();
    // Constants
    private static final int ID_SERVICE = 101;
    private static final String HOST_DIGITALPHENOTYPNGMANAGER = "10.0.2.3";
    private static final String HOST_CONTEXTDATAPROVIDER = "10.0.2.2";


    @Override
    public void onCreate() {
        super.onCreate();
        //instance = this;
        context = this;
        messageTextView = new TextView(context);

        subActive = SubscriberFactory.createSubscriber();

        // Create the Foreground Service
        Log.i(TAG,"#### criando notificação");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Behavior Viewer")
                .setContentText("Behavior monitoring application")
                .setPriority(PRIORITY_MIN)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(ID_SERVICE, notification);
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "my_service_channelid";
        String channelName = "Service BusSystem";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        //channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        //channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        Log.i(TAG,"#### channelId: " + channelId);
        return channelId;
    }


    public final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public BusSystem getService() {
            //return mBinder;
            return BusSystem.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"#### onStartCommand BusSystem");

        if(intent != null){
            String clientID = intent.getStringExtra("clientID");
            int communicationTechnology = intent.getIntExtra("communicationTechnology", 0);

            Log.i(TAG,"#### clientID: " + clientID);
            Log.i(TAG,"#### communicationTechnology: " + communicationTechnology);

            setClientID(clientID);
            setCommunicationTechnology(communicationTechnology);

            initCDDL();
            configSubActive("activesensor");
        }

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        cddl.stopAllCommunicationTechnologies();
        cddl.stopService();
        con.disconnect();
        CDDL.stopMicroBroker();
        onDestroy();
    }


//    public static BusSystem getInstance() {
//        if (instance == null) {
//            instance = new BusSystem();
//        }
//        return instance;
//    }


    public void configSubActive(String serviceName){
        Log.i(TAG,"#### Subscribe: " + serviceName);
        subActive.addConnection(DPApplication.getInstance().CDDLGetInstance().getConnection());
        subActive.subscribeServiceByName(serviceName);
        subActive.setSubscriberListener(subscriberStart);
    }


    public ISubscriberListener subscriberStart = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages:  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);
        }
    };


    public void start(Context context, Activity activity, String clientID, int communicationTechnology){
        this.context = context;
        this.activity = activity;
        this.clientID = clientID;
        this.communicationTechnology = communicationTechnology;

        messageTextView = new TextView(context);
    }


    public void initCDDL(){
        try {
            //String host = CDDL.startMicroBroker();
            String host = "10.0.2.3";
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


    public void initSecureCDDL(){
        try {
            //String host = CDDL.startMicroBroker();
            String host = CDDL.startSecureMicroBroker(getContext(), true);
            //val host = "broker.hivemq.com";
            con = ConnectionFactory.createConnection();
            con.setClientId(getClientID());
            con.setHost(host);
            con.addConnectionListener(connectionListener);
            //con.connect();
            con.secureConnect(getContext());
            cddl = CDDL.getInstance();
            cddl.setConnection(con);
            cddl.setContext(getContext());
            cddl.startService();

            cddl.startCommunicationTechnology(CDDL.INTERNAL_TECHNOLOGY_VIRTUAL_ID);
        }catch (Exception e){
            Log.i(TAG,"#### Error: " + e.getMessage());
        }
    }


    public void initComunication(Context context, Activity activity, String clientID, int communicationTechnology, Boolean secure){
        Log.i(TAG,"#### Iniciando a configuração do CDDL novamente.");
        start(context, activity, clientID, communicationTechnology);
        if(secure) {
            initSecureCDDL();
            Log.i(TAG,"#### Iniciando busSystem com criptografia.");
        }
        else{
            initCDDL();
            Log.i(TAG,"#### Iniciando busSystem sem criptografia.");
        }
        initAllPermissions();
    }


    public void initSecure(){
        // Android 9-10
        // Versão 26
        //Parte de segurança - Certificados Digitais
        // Senha da Chave privada: 123456
        SecurityService securityService = new SecurityService(getContext());
        //securityService.generateCSR("jean","LSDi","ufma","slz","ma","br");
        try {
            securityService.setCaCertificate(nameCaCertificate);
            securityService.setCertificate(nameClientCertificate);

            securityService.grantReadPermissionByCDDLTopic("lcmuniz@gmail.com", SecurityService.ALL_TOPICS);
            securityService.grantWritePermissionByCDDLTopic("lcmuniz@gmail.com", SecurityService.ALL_TOPICS);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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


    public List<String> listInternalSensor(){
        List<String> s = null;
        List<Sensor> sensorInternal = cddl.getInternalSensorList();

        Log.i(TAG,"\n #### Sensores internos disponíveis: \n");
        for(int i=0; i < sensorInternal.size(); i++){
            s.add(sensorInternal.get(i).getName());
            Log.i(TAG,"#### (" + i + "): " + sensorInternal.get(i).toString());
        }
        return s;
    }


    public void startVirtualSensor(String sensor){
        if(sensor.equalsIgnoreCase("TouchScreen")) {
            // Solicita permissão de desenhar (canDrawOverlays) para Toque de Tela
            checkDrawOverlayPermission();
            cddl.startSensor(sensor, 0);
        }
        else{
            initPermissions(sensor);
            cddl.startSensor(sensor, 0);
        }
        //cddl.startSensor("SMS",0);
        //cddl.startSensor("Call",0);
        //cddl.startSensor("ScreenOnOff",0);
    }

    public void startAllVirtualSensors(){
        // solicita permissão ao  usuário
        initAllPermissions();

        //Start sensores virtuais pelo nome e delay
        cddl.startSensor("SMS",0);
        cddl.startSensor("Call",0);
        cddl.startSensor("ScreenOnOff",0);

        // Solicita permissão de desenhar (canDrawOverlays) para Toque de Tela
        checkDrawOverlayPermission();
        cddl.startSensor("TouchScreen", 0);
    }


    public CDDL getInstanceCDDL(){
//        if(true){
//            Log.i(TAG,"***************************");
//            initComunication(this, (Activity) getActivity(), "l", 4, false);
//        }
        return DPApplication.getInstance().CDDLGetInstance();
    }


    public Context getContext() {
        return context;
    }


    public void setContext(Context context) {
        this.context= context;
    }


    public String getStatusCon(){ return statusConnection; }


    public void setNameCaCertificate(String name){
        this.nameCaCertificate = name;
    }


    public String getNameCaCertificate(){ return nameCaCertificate; }


    public void setnameClientCertificate(String name){
        this.nameClientCertificate = name;
    }


    public String getNameClientCertificate(){ return nameClientCertificate;}


    public void setClientID(String clientID){
        this.clientID = clientID;
    }


    public String getClientID(){
        return clientID;
    }


    public void setActivity(Activity a){
        this.activity = a;
    }


    public Activity getActivity(){
        return activity;
    }


    public void setCommunicationTechnology(int communicationTechnology){
        this.communicationTechnology = communicationTechnology;
    }


    public int getCommunicationTechnology(){
        return communicationTechnology;
    }


//    public void onDestroy() {
//        cddl.stopAllCommunicationTechnologies();
//        cddl.stopService();
//        con.disconnect();
//        CDDL.stopMicroBroker();
//    }

//    public void configSubscrbe(){
//        subscriber = SubscriberFactory.createSubscriber();
//        subscriber.addConnection(cddl.getConnection());
//
//        subscriber.subscribeServiceByName("Location");
//        subscriber.setSubscriberListener(this::onMessage);
//    }

//    public ISubscriberListener subscriberStartSensor = new ISubscriberListener() {
//        @Override
//        public void onMessageArrived(Message message) {
////                    if (message.getServiceName().equals("Meu serviço")) {
////                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
////                    }
//            Log.d(TAG, "#### Read messages -----" + message);
////                    cddl.getInstance().startSensor(message.getServiceValue().toString());
//        }
//    };

//    public void subscribeMessage(String serviceName) {
//        try {
//            Subscriber sub = SubscriberFactory.createSubscriber();
//            sub.addConnection(cddl.getConnection());
//            sub.subscribeServiceByName(serviceName);
//            //sub.subscribeServiceByName("Location");
//
//            sub.setSubscriberListener(new ISubscriberListener() {
//                @Override
//                public void onMessageArrived(Message message) {
////                    if (message.getServiceName().equals("Meu serviço")) {
////                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
////                    }
//                    Log.d(TAG, "#### Read messages -----" + message);
////                    cddl.getInstance().startSensor(message.getServiceValue().toString());
//                }
//            });
//        }catch (Exception e){
//            Log.e(TAG,"#### Error: " + e.getMessage());
//        }
//
//    }


    public void publishMessage(String service, String text) {
        publisher.addConnection(cddl.getInstance().getConnection());

        MyMessage message = new MyMessage();
        message.setServiceName(service);
        message.setServiceByteArray(text);
        publisher.publish(message);
    }


    private void checkDrawOverlayPermission() {
        Log.i(TAG, "#### Permissao para o sensor TouchScreen");
        // check if we already  have permission to draw over other apps
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getContext()) ){
                // if not construct intent to request permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getContext().getPackageName()));
                //ac.startService(intent);
                // request permission via start activity for result
                Log.i(TAG, "#### permissao dada pelo usuário");

                getActivity().startActivityForResult(intent, 1);
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

        if(sensor.equalsIgnoreCase("SMS")){
            String[] PERMISSIONS = {
                    // SMS entrada
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.RECEIVE_SMS,
                    android.Manifest.permission.READ_SMS,
                    // SMS saída
                    android.Manifest.permission.READ_EXTERNAL_STORAGE};

            if (!hasPermissions(getActivity(), PERMISSIONS)) {
                Log.i(TAG,"##### Permission enabled for the sensor: " + sensor);
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
            }
        }
        else if(sensor.equalsIgnoreCase("Call")){
            String[] PERMISSIONS = {
                    //Call
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.READ_CALL_LOG,
                    android.Manifest.permission.WRITE_CALL_LOG,
                    android.Manifest.permission.ADD_VOICEMAIL};

            if (!hasPermissions(getActivity(), PERMISSIONS)) {
                Log.i(TAG,"##### Permission enabled for the sensor: " + sensor);
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
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


    public void initAllPermissions() {
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

        if (!hasPermissions(getActivity(), PERMISSIONS)) {
            Log.i(TAG,"##### Permissão Ativada");
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
    }
}
