package br.ufma.lsdi.digitalphenotyping;

import static br.ufma.lsdi.digitalphenotyping.CompositionMode.FREQUENCY;
import static br.ufma.lsdi.digitalphenotyping.CompositionMode.SEND_WHEN_IT_ARRIVES;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;

/**
 * DPInterface class is responsible for starting the framework's main service (MainService).
 */
public class DPManager implements DPInterface {
    private static final String TAG = DPManager.class.getName();
    private Publisher publisher = PublisherFactory.createPublisher();
    private String statusCon = "undefined";
    private static DPManager instance = null;
    private Context context;
    private Activity activity;
    private int communicationTechnology;
    private Boolean secure;
    private MainService myService;
    private boolean servicesStarted = false;
    private PropertyManager propertyManager;
    private Builder builderCopy;


    /**
     * Construtor do DPManager
     */
    public DPManager(){ }


    /**
     * Constructor responsible for creating an instance of DPManager, receiving as parameter the
     * Builder class with framework settings.
     * @param builder is a class that contains framework settings.
     */
    public DPManager(final Builder builder){
        this.activity = builder.activity;
        this.builderCopy = builder;
    }


    /**
     * Get an instance of DPManager
     * @return an instance of DPManager
     */
    public static DPManager getInstance() {
        if (instance == null) {
            instance = new DPManager();
        }
        return instance;
    }


    /**
     * Starts the framework giving start service on the main services of the framework.
     */
    @Override
    public void start(){
        try{
            Log.i(TAG, "#### INICIANDO FRAMEWORK");

            //this.activity = activity;
            this.context = (Context) activity;
            //configurations.getInstance().setActivity(activity);

            propertyManager = new PropertyManager("configuration.properties", this.context);
            saveExternalServerAddress(builderCopy.hostServer, builderCopy.port, builderCopy.username, builderCopy.password, builderCopy.topic, builderCopy.compositionMode);

            this.communicationTechnology = 4;
            this.secure = false;

            initPermissionsRequired();

            if(!servicesStarted) {
                Log.i(TAG, "#### Started framework MainService.");
                Intent intent = new Intent(this.activity, MainService.class);

                ActivityParcelable activityParcelable = new ActivityParcelable();
                activityParcelable.setActivity(this.activity);
                intent.putExtra("activity", (Parcelable) activityParcelable);

                intent.putExtra("compositionmode", builderCopy.compositionMode);
                if(builderCopy.compositionMode == FREQUENCY) {
                    intent.putExtra("frequency", builderCopy.frequency);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getContext().startForegroundService(intent);
                } else {
                    getContext().startService(intent);
                }

                servicesStarted = true;
            }
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    /**
     * Stops the framework.
     */
    @Override
    public void stop(){
        Log.i(TAG,"#### Stopped framework MainService.");
        // PARA O SERVICE PRIMEIRO PLANO
        myService.stopForeground(true);

        try {
            if(servicesStarted) {
                Intent intent = new Intent(getContext(), MainService.class);
                getContext().stopService(intent);

                servicesStarted = false;
            }
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }
    }


    /**
     * Start framework processors passing a list of processors as parameters.
     * @param nameProcessors a list of processors.
     */
    @Override
    public void startDataProcessors(List<String> nameProcessors){
        if(!servicesStarted) {
            Log.i(TAG, "#### Started processors");
            if (!nameProcessors.isEmpty()) {
                for (int i = 0; i < nameProcessors.size(); i++) {
                    publishMessage(Topics.START_PROCESSOR_TOPIC.toString(), nameProcessors.get(i).toString());
                }
            }
        }
        else{
            Log.e(TAG,"#### Error: Started MainService Service");
        }
    }


    /**
     * Stops framework processors passing a list of processors as parameters.
     * @param nameProcessors a list of processors.
     */
    @Override
    public void stopDataProcessors(List<String> nameProcessors){
        if(servicesStarted) {
            Log.i(TAG, "#### Stopped processors");
            if (!nameProcessors.isEmpty()) {
                for (int i = 0; i < nameProcessors.size(); i++) {
                    publishMessage(Topics.STOP_PROCESSOR_TOPIC.toString(), nameProcessors.get(i).toString());
                }
            }
        }
        else{
            Log.e(TAG,"#### Error: Started MainService Service");
        }
    }


    /**
     * Search all processors available in the framework
     * @return a list of processors.
     */
    @Override
    public List<String> getDataProcessorsList(){
        Log.i(TAG, "#### Processors list");
        List<String> processors = null;

        processors = myService.getProcessors();
        Log.i(TAG, "#### Processors list: " + processors);
        return processors;
    }


    /**
     * Search for all active processors that are running in the framework.
     * @return a list of active processors.
     */
    @Override
    public List<String> getActiveDataProcessorsList(){
        List<String> processors = null;
        List<String> activeProcessors = null;

        return activeProcessors;
    }


    /**
     * It saves the data necessary for the PhenotypeComposer to connect to a remote server, sending
     * the identified digital phenotypes to the external server.
     * @param hostServer remote server name.
     * @param port server port number for the framework to send the identified digital phenotypes.
     * @param username port number of the remote server.
     * @param password remote server password.
     * @param topic name of the topic for which the digital phenotypes will be received.
     * @param compositionMode how the framework works (e.g., SEND_WHEN_IT_ARRIVES, GROUP_ALL and FREQUENCY).
     */
    @Override
    public void saveExternalServerAddress(String hostServer, int port, String username, String password, String topic, CompositionMode compositionMode){
        propertyManager.setProperty("hostServer",hostServer);
        propertyManager.setProperty("port", String.valueOf(port));
        propertyManager.setProperty("username",username);
        propertyManager.setProperty("password",password);
        propertyManager.setProperty("topic",topic);
        propertyManager.setProperty("compositionMode", String.valueOf(compositionMode));
    }


    /**
     * Searches the external server address through a string vector.
     * @return returns a string vector.
     */
    public String[] getExternalServerAddress(){
        String str[] = new String[6];
        str[0] = propertyManager.getProperty("hostServer");
        str[1] = propertyManager.getProperty("port");
        str[2] = propertyManager.getProperty("clientID");
        str[3] = propertyManager.getProperty("username");
        str[4] = propertyManager.getProperty("password");
        str[5] = propertyManager.getProperty("topic");
        str[6] = propertyManager.getProperty("compositionMode");
        return str;
    }


    /**
     * Search the application context.
     * @return the context.
     */
    public Context getContext() {
        return context;
    }


    /**
     * Set the application context.
     * @param context the context of the application.
     */
    public void setContext(Context context) {
        this.context = context;
    }


    /**
     * Set application activity.
     * @param activity there is application activity.
     */
    public void setActivity(Activity activity){
        this.activity = activity;
    }


    /**
     * Search the application activity...
     * @return the activity
     */
    public Activity getActivity(){
        return activity;
    }


    /**
     *
     * @param secure
     */
    public void setSecure(Boolean secure) {
        this.secure = secure;
    }


    /**
     *
     * @return
     */
    public Boolean getSecure() {
        return this.secure;
    }


    /**
     *
     * @param statusCon
     */
    public void setStatusCon(String statusCon){
        this.statusCon = statusCon;
    }


    /**
     *
     * @return
     */
    public String getStatusCon(){
        return statusCon;
    }


    /**
     *
     * @param mainService
     */
    public void setMainService(MainService mainService){
        this.myService = mainService;
    }


    /**
     *
     * @return
     */
    public MainService getMainService(){
        return myService;
    }


    /**
     *
     * @param service
     * @param text
     */
    public void publishMessage(String service, String text) {
        publisher.addConnection(CDDL.getInstance().getConnection());

        Message message = new Message();
        message.setServiceName(service);
        message.setServiceValue(text);
        publisher.publish(message);
    }


    /**
     *
     * @param context
     * @param permissions
     * @return
     */
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


    /**
     *
     */
    public void initPermissionsRequired() {
        // Check permissions to run the Framework
        int PERMISSION_ALL = 1;

        String[] PERMISSIONS = {
                // Service location
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.RECORD_AUDIO

                // Outros services
        };


        if (!hasPermissions(getContext(), PERMISSIONS)) {
            Log.i(TAG, "##### Permission enabled for framework");
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
    }


    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "DPManager{" +
                "hostServer=" + builderCopy.hostServer +
                ", port='" + builderCopy.port + '\'' +
                ", username=" + builderCopy.username +
                ", password='" + builderCopy.password + '\'' +
                ", topic='" + builderCopy.topic + '\'' +
                ", compositionMode='" + builderCopy.compositionMode + '\'' +
                '}';
    }


    /**
     *
     */
    public static class Builder{
        private static final String TAG = Builder.class.getName();
        private Activity activity=null;
        private String hostServer = "";
        private int port=0;
        private String username = "";
        private String password = "";
        private String topic = "";
        private CompositionMode compositionMode = SEND_WHEN_IT_ARRIVES;
        private int frequency = 0;

        public Builder(Activity activity){
            this.activity = activity;
        }

        public Builder setExternalServer(final String host, final int port){
            this.hostServer = host;
            this.port = port;
            return this;
        }

        public Builder setCompositionMode(@NonNull CompositionMode compositionMode){
            try {
                this.compositionMode = compositionMode;
            }catch (Exception e){
                Log.e(TAG,"#### The options for the composition mode are [SEND_WHEN_IT_ARRIVES, GROUP_ALL, FREQUENCY].");
            }
            return this;
        }

        public Builder setFrequency(@NonNull int frequency){
            this.frequency = frequency;
            return this;
        }

        public Builder setUsername(@NonNull final String username){
            this.username = username;
            return this;
        }

        public Builder setPassword(@NonNull final String password){
            this.password = password;
            return this;
        }

        public Builder setHost(@NonNull final String host){
            this.hostServer = host;
            return this;
        }

        public Builder setPort(@NonNull final int port){
            this.port = port;
            return this;
        }

        public Builder setTopic(@NonNull final String topic){
            this.topic = topic;
            return this;
        }

        public DPManager build(){
            try{
                if(this.hostServer.isEmpty()){
                    Log.e(TAG,"#### Error: The hostname is required.");
                }
                else if(this.port == 0){
                    Log.e(TAG,"#### Error: The port number is required.");
                }
                else if(this.activity == null){
                    Log.e(TAG,"#### Error: The activity is mandatory.");
                }
            }catch (Exception e){
                Log.e(TAG,"#### Error: " + e.toString());
            }
            return new DPManager(this);
        }
    }
}
