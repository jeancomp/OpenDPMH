package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import java.io.FileNotFoundException;
import java.util.List;
import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.listeners.IConnectionListener;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.cddl.network.SecurityService;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

/**
 * System Bus to framework
 */
public class Bus extends Service {
    private CDDL cddl;
    private String clientID;
    private ConnectionImpl con;
    private Context context;
    private Activity activity;
    private Boolean secure;
    private TextView messageTextView;
    private String statusConnection = "";
    private int communicationTechnology = 4;
    private String nameCaCertificate = "rootCA.crt";
    private String nameClientCertificate = "client.crt";
    private static final String TAG = Bus.class.getName();
    Publisher publisher = PublisherFactory.createPublisher();
    private static final int ID_SERVICE = 101;
    DPApplication dpApplication = DPApplication.getInstance();


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"#### Starting service BusSystem");
        context = this;
        messageTextView = new TextView(context);

        // Create the Foreground Service
        Log.i(TAG,"#### Criando notificação");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("OpenDPMH")
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
        public Bus getService() {
            //return mBinder;
            return Bus.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String clientID = intent.getStringExtra("clientID");
            int communicationTechnology = intent.getIntExtra("communicationTechnology", 0);
            Boolean secure = intent.getBooleanExtra("secure", false);

            Log.i(TAG,"#### clientID: " + clientID);
            Log.i(TAG,"#### communicationTechnology: " + communicationTechnology);

            setClientID(clientID);
            setCommunicationTechnology(communicationTechnology);
            setSecure(secure);

            initCDDL();
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


    public void initCDDL(){
        try {
            String host = CDDL.startMicroBroker();
            //String host = "10.0.2.3";
            Log.i(TAG,"#### ENDEREÇO DO BROKER: " + host);
            //val host = "broker.hivemq.com";
            con = ConnectionFactory.createConnection();
            con.setClientId(getClientID());
            con.setHost(host);
            con.addConnectionListener(connectionListener);
            con.connect();
            //cddl = CDDL.getInstance();
            cddl = dpApplication.getInstance().CDDLGetInstance();
            cddl.setConnection(con);
            //cddl.setContext(getContext());
            cddl.setContext(getContext());
            cddl.startService();

            // Para todas as tecnologias, para entao iniciar apenas a que temos interresse
            cddl.stopAllCommunicationTechnologies();

            // Para todas os sensores, para entao iniciar apenas a que temos interresse
            cddl.stopAllSensors();

            //cddl.startCommunicationTechnology(CDDL.INTERNAL_TECHNOLOGY_VIRTUAL_ID);
            cddl.startAllCommunicationTechnologies();
            //cddl.startCommunicationTechnology(this.communicationTechnology);
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
        //Configuração para executar com secure
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


    public CDDL getInstanceCDDL(){
        return dpApplication.getInstance().CDDLGetInstance();
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


    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public Boolean getSecure() {
        return this.secure;
    }


    public void setCommunicationTechnology(int communicationTechnology){
        this.communicationTechnology = communicationTechnology;
    }


    /**
     *
     * @return
     */
    public int getCommunicationTechnology(){
        return communicationTechnology;
    }
}
