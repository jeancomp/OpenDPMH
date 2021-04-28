package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;

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

public class BusSystem extends Application {
    private CDDL cddl;
    private String clientID;
    private ConnectionImpl con;
//    public Subscriber subscriber;
//    public Handler handler = new Handler();
    private Context context;
    private Activity activity;
    private TextView messageTextView;
    private String statusConnection = "";
    private int communicationTechnology = 4;
    private String nameCaCertificate = "rootCA.crt";
    private String nameClientCertificate = "client.crt";
    private static final String TAG = BusSystem.class.getName();
    private static BusSystem instance = null;
    //public List<String> listViewMessages;
    //public ListViewAdapter listViewAdapter;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public static BusSystem getInstance() {
        if (instance == null) {
            instance = new BusSystem();
        }
        return instance;
    }


    public void start(Context context, Activity activity, String clientID, int communicationTechnology){
        this.context = context;
        this.activity = activity;
        this.clientID = clientID;
        this.communicationTechnology = communicationTechnology;

        messageTextView = new TextView(context);
    }


    public void initCDDL(){
        try {
            String host = CDDL.startMicroBroker();
            //val host = "broker.hivemq.com";
            con = ConnectionFactory.createConnection();
            con.setClientId(getClientID());
            con.setHost(host);
            con.addConnectionListener(connectionListener);
            con.connect();
            cddl = CDDL.getInstance();
            cddl.setConnection(con);
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


    public CDDL getInstanceCDDL(){
        return cddl.getInstance();
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


    public void setClientID(String clientID){
        this.clientID = clientID;
    }


    public String getClientID(){
        return clientID;
    }


    public void setActivity(Activity a){
        activity = a;
    }


    public Activity getActivity(){
        return activity;
    }


    public void onDestroy() {
        //cddl.stopLocationSensor();
        cddl.stopAllCommunicationTechnologies();
        cddl.stopService();
        con.disconnect();
        CDDL.stopMicroBroker();
    }

//    public void configSubscrbe(){
//        subscriber = SubscriberFactory.createSubscriber();
//        subscriber.addConnection(cddl.getConnection());
//
//        subscriber.subscribeServiceByName("Location");
//        subscriber.setSubscriberListener(this::onMessage);
//    }

//    public void onMessage(Message message) {
//        handler.post(() -> {
//            Object[] valor = message.getServiceValue();
//            Log.i(TAG,"#### Mensagem chegou: " + valor[0]);
//            //listViewMessages.add(StringUtils.join(valor[0] , ", " + valor[1]));
//            listViewAdapter.notifyDataSetChanged();
//        });
//    }

    public Object subscribeMessageCDP(final String serviceName) {
        final Object[] text = new String[1];
        try {
            Subscriber sub = SubscriberFactory.createSubscriber();
            sub.addConnection(cddl.getInstance().getConnection());
            sub.subscribeServiceByName(serviceName);
            //sub.subscribeServiceByName("Location");

            sub.setSubscriberListener(new ISubscriberListener() {
                @Override
                public void onMessageArrived(Message message) {
                    if (message.getServiceName().equalsIgnoreCase(serviceName)) {
                        Log.d(TAG, ">>> #### READ MESSAGES: " + message);
                        text[0] = message.getServiceValue();
                    }
                }
            });
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.getMessage());
        }

        return text;
    }

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
        Publisher publisher = PublisherFactory.createPublisher();
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
                android.Manifest.permission.ACCESS_FINE_LOCATION
        };

        if (!hasPermissions(getActivity(), PERMISSIONS)) {
            Log.i(TAG,"##### Permissão Ativada");
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
    }
}
