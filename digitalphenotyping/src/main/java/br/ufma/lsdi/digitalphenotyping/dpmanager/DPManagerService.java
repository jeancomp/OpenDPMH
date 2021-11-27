package br.ufma.lsdi.digitalphenotyping.dpmanager;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static br.ufma.lsdi.digitalphenotyping.CompositionMode.FREQUENCY;
import static br.ufma.lsdi.digitalphenotyping.CompositionMode.SEND_WHEN_IT_ARRIVES;

import android.Manifest;
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
import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;

import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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
import br.ufma.lsdi.digitalphenotyping.CompositionMode;
import br.ufma.lsdi.digitalphenotyping.R;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.phenotypecomposer.PhenotypeComposer;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.ProcessorManager;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessorManager;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessorManager;
import br.ufma.lsdi.digitalphenotyping.rawdatacollector.RawDataCollector;

/**
 * System Bus to framework, DPManagerService
 */
public class DPManagerService extends Service {
    private static final String TAG = DPManagerService.class.getName();
    private ActiveDataProcessorManager activeDataProcessorManager;
    private ListDataProcessorManager listDataProcessorManager;
    private Publisher publisher = PublisherFactory.createPublisher();
    private CDDL cddl;
    private String hostServer = "";
    private String port = "";
    private String username = "username";
    private String password = "";
    private String clientID;
    private boolean isActiveRawDataCollector;
    private ConnectionImpl con;
    private Context context;
    private Activity activity;
    //private ActivityParcelable activityParcelable2;
    private Boolean secure = false;
    private TextView messageTextView;
    private String statusConnection = "";
    private int communicationTechnology = 4;
    private String nameCaCertificate = "rootCA.crt";
    private String nameClientCertificate = "client.crt";
    private static final int ID_SERVICE = 101;
    private boolean servicesStarted = false;
    private Subscriber subConfigurationInformation;
    private List<String> processors = null;
    private CompositionMode compositionmode = SEND_WHEN_IT_ARRIVES;
    private int frequency = 0;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @RequiresPermission(allOf = {
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED    // Verificar se ainda se vai ter esse recurso no framework
    })
    @Override
    public void onCreate() {
        try {
            super.onCreate();
            Log.i(TAG, "#### Starting service main service");
            context = this;
            messageTextView = new TextView(context);
            this.processors = new ArrayList();
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    public void foregroundAPP(){
        // Create the Foreground Service
            Log.i(TAG, "#### Notification create");
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
        String channelName = "Service OpenDPMH";
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
        public DPManagerService getService() {
            return DPManagerService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Log.i(TAG, "#### CONFIGURATION MAINSERVICE");
            if (intent != null) {
                clientID = intent.getStringExtra("clientid");
                isActiveRawDataCollector = intent.getBooleanExtra("activerawdatacollector", false);
                //activeDataProcessorManager = new ActiveDataProcessorManager(getContext());
                listDataProcessorManager = new ListDataProcessorManager(getContext());

                setCommunicationTechnology(this.communicationTechnology);
                setSecure(this.secure);
                //activityParcelable2 = new ActivityParcelable();
                //activityParcelable2 = (ActivityParcelable) intent.getParcelableExtra("activity");

                startCDDL();
                startServices();

                if (servicesStarted) {
                    //String clientID = intent.getStringExtra("clientID");
                    compositionmode = (CompositionMode) intent.getSerializableExtra("compositionmode");
                    hostServer = intent.getStringExtra("hostserver");
                    port = intent.getStringExtra("port");
                    username = intent.getStringExtra("username");
                    password = intent.getStringExtra("password");
                    frequency = 0;
                    if (compositionmode == FREQUENCY) {
                        frequency = intent.getIntExtra("frequency", 1);
                        Log.i(TAG, "#### MainService receives Frequency: " + frequency);
                    }
                    Log.i(TAG, "#### MainService receives CompositionMode: " + compositionmode.name().toString());
                }

                subConfigurationInformation = SubscriberFactory.createSubscriber();
                subConfigurationInformation.addConnection(CDDL.getInstance().getConnection());
                subscribeMessageConfigurationInformation(Topics.MAINSERVICE_CONFIGURATION_INFORMATION_TOPIC.toString());

                publishMessage(Topics.NOTIFICATION.toString(), "aliveMainService");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }


    public void subscribeMessageConfigurationInformation(String serviceName) {
        subConfigurationInformation.subscribeServiceByName(serviceName);
        subConfigurationInformation.setSubscriberListener(subscriberConfigurationInformation);
    }


    public ISubscriberListener subscriberConfigurationInformation = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
            Log.i(TAG, "#### Read messages (subscriber subscriberConfigurationInformation):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.i(TAG, "#### Who alive: " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);

            if (atividade.equals("alive")) {
                //publishMessage(Topics.COMPOSITIONMODE.toString(), String.valueOf(compositionmode.name().toString()), String.valueOf(frequency));
                publishMessage(Topics.CONFIGURATION_INFORMATION_TOPIC.toString(), hostServer, port, username, password, clientID);
                publishMessage(Topics.COMPOSITION_MODE_TOPIC.toString(), compositionmode, frequency);
            }
        }
    };


    @Override
    public void onDestroy() {
        cddl.stopAllCommunicationTechnologies();
        cddl.stopService();
        con.disconnect();
        CDDL.stopMicroBroker();
        stopServices();
        onDestroy();
    }


    public void startCDDL(){
        try {
            String host = CDDL.startMicroBroker();
            Log.i(TAG,"#### ENDEREÇO DO MICROBROKER: " + host);
            //val host = "broker.hivemq.com";
            con = ConnectionFactory.createConnection();
            con.setClientId(this.clientID);
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
            cddl.startAllCommunicationTechnologies();
            //cddl.startCommunicationTechnology(this.communicationTechnology);
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.getMessage());
        }
    }


    private void startServices(){
        try{
            if(!servicesStarted) {
                Log.i(TAG, "#### Starts all framework services.");
                Intent pm = new Intent(getContext(), ProcessorManager.class);
                //pm.putExtra("activity", (Parcelable) activityParcelable2);
                getContext().startService(pm);

                Intent pc = new Intent(getContext(), PhenotypeComposer.class);
                getContext().startService(pc);

                if(isActiveRawDataCollector){
                    Intent rdc = new Intent(getContext(), RawDataCollector.class);
                    getContext().startService(rdc);
                }

                servicesStarted = true;
            }
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    private void stopServices(){
        try {
            if(servicesStarted) {
                Intent ipm = new Intent(getContext(), ProcessorManager.class);
                getContext().stopService(ipm);

                Intent pc = new Intent(getContext(), PhenotypeComposer.class);
                getContext().startService(pc);

                if(isActiveRawDataCollector){
                    Intent rdc = new Intent(getContext(), RawDataCollector.class);
                    getContext().startService(rdc);
                }

                servicesStarted = false;
            }
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }


    public void initSecureCDDL(){
        try {
            //String host = CDDL.startMicroBroker();
            String host = CDDL.startSecureMicroBroker(getContext(), true);
            //val host = "broker.hivemq.com";
            con = ConnectionFactory.createConnection();
            con.setClientId(this.clientID);
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
            Log.e(TAG,"#### Error: " + e.getMessage());
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


    /**
     *
     * @return
     */
    public List<String> getProcessors(){
        return this.processors;
    }


    /**
     * Publish framework configuration information, e.g., external server address, port number, user login,
     *  password, and the user identifier.
     * @param service
     * @param hostServer
     * @param port
     * @param username
     * @param password
     * @param clientID
     */
    public void publishMessage(String service, String hostServer, String port, String username, String password, String clientID){
        publisher.addConnection(CDDL.getInstance().getConnection());
        Object[] valor = {hostServer, port, username, password, clientID};

        Message message = new Message();

        message.setAvailableAttributes(5);
        String[] finalValor2 = {"External server address", "Port", "Username", "Password", "ClientID"};
        message.setAvailableAttributesList(finalValor2);

        message.setServiceName(service);
        message.setServiceValue(valor);
        publisher.publish(message);
    }


    /**
     *
     * @param service
     * @param compositionmode
     * @param frequency
     */
    public void publishMessage(String service, CompositionMode compositionmode, int frequency) {
        publisher.addConnection(CDDL.getInstance().getConnection());
        Object[] valor = {compositionmode, frequency};

        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(valor);
        publisher.publish(message);
    }


    public void publishMessage(String service, String text) {
        publisher.addConnection(CDDL.getInstance().getConnection());

        Message message = new Message();
        message.setServiceName(service);
        message.setTopic(service);
        message.setServiceValue(text);
        publisher.publish(message);
    }
}
