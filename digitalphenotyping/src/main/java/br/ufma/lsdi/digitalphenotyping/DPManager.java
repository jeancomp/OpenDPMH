package br.ufma.lsdi.digitalphenotyping;

import static br.ufma.lsdi.digitalphenotyping.CompositionMode.FREQUENCY;
import static br.ufma.lsdi.digitalphenotyping.CompositionMode.GROUP_ALL;
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
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
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
    public DPManager() {
    }


    /**
     * Constructor responsible for creating an instance of DPManager, receiving as parameter the
     * Builder class with framework settings.
     * @param builder is a class that contains framework settings.
     */
    public DPManager(final Builder builder) {
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
    public void start() {
        try {
            Log.i(TAG, "#### INICIANDO FRAMEWORK");

            //this.activity = activity;
            this.context = (Context) builderCopy.activity;

            //ActivityCompat.requestPermissions(this.activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},101);

            propertyManager = new PropertyManager("configuration.properties", this.context);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveExternalServerAddress(builderCopy.hostServer, builderCopy.port, builderCopy.username, builderCopy.password);
            }

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
        // Stop the foreground service
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
     * @param dataProcessorsName a list of processors.
     */
    @Override
    public void startDataProcessors(List<String> dataProcessorsName){
        if(dataProcessorsName.isEmpty()){
            throw new HandlingExceptions("#### Error: dataProcessorsName cannot be empty.");
        }
        else if(dataProcessorsName == null){
            throw new HandlingExceptions("#### Error: dataProcessorsName cannot be null.");
        }
        if(!servicesStarted) {
            Log.i(TAG, "#### Started processors");
            if (!dataProcessorsName.isEmpty()) {
                for (int i = 0; i < dataProcessorsName.size(); i++) {
                    publishMessage(Topics.START_PROCESSOR_TOPIC.toString(), dataProcessorsName.get(i).toString());
                }
            }
        }
        else{
            Log.e(TAG,"#### Error: Started MainService Service");
        }
    }


    /**
     * Stops framework processors passing a list of processors as parameters.
     * @param dataProcessorsName a list of processors.
     */
    @Override
    public void stopDataProcessors(List<String> dataProcessorsName){
        if(dataProcessorsName.isEmpty()){
            throw new HandlingExceptions("#### Error: dataProcessorsName cannot be empty.");
        }
        else if(dataProcessorsName == null){
            throw new HandlingExceptions("#### Error: dataProcessorsName cannot be null.");
        }
        if(servicesStarted) {
            Log.i(TAG, "#### Stopped processors");
            if (!dataProcessorsName.isEmpty()) {
                for (int i = 0; i < dataProcessorsName.size(); i++) {
                    publishMessage(Topics.STOP_PROCESSOR_TOPIC.toString(), dataProcessorsName.get(i).toString());
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
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @RequiresPermission(allOf = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    })
    @Override
    public void saveExternalServerAddress(String hostServer, Integer port, String username, String password){
        if(hostServer.isEmpty() || hostServer == null){
            throw new HandlingExceptions("#### Error: hostServer cannot be empty or null.");
        }
        else if(port == null){
            throw new HandlingExceptions("#### Error: port number cannot be empty or null.");
        }
        propertyManager.setProperty("hostServer",hostServer);
        propertyManager.setProperty("port", String.valueOf(port));
        propertyManager.setProperty("username",username);
        propertyManager.setProperty("password",password);
    }


    /**
     * Searches the external server address through a string vector.
     * @return returns a string vector.
     */
    public String[] getExternalServerAddress(){
        String str[] = new String[4];
        str[0] = propertyManager.getProperty("hostServer");
        str[1] = propertyManager.getProperty("port");
        str[3] = propertyManager.getProperty("username");
        str[4] = propertyManager.getProperty("password");
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
        if(context == null){
            throw new HandlingExceptions("#### Error: context cannot be null.");
        }
        this.context = context;
    }


    /**
     * Set application activity.
     * @param activity there is application activity.
     */
    public void setActivity(Activity activity){
        if(activity == null){
            throw new HandlingExceptions("#### Error: activity cannot be null.");
        }
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
        if(secure == null){
            throw new HandlingExceptions("#### Error: secure cannot be null.");
        }
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
        if(mainService == null){
            throw new HandlingExceptions("#### Error: mainService cannot be null.");
        }
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
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

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
                '}';
    }


    /**
     *
     */
    public static class Builder{
        private Activity activity;
        private String hostServer = "";
        private Integer port = null;
        private String username = "username";
        private String password = "12345";
        private CompositionMode compositionMode = null;
        private Integer frequency = null;

        public Builder(Activity activity){
            this.activity = activity;
        }

        public Builder setExternalServer(final String host, final Integer port){
            this.hostServer = host;
            this.port = port;
            return this;
        }

        public Builder setCompositionMode(@NonNull CompositionMode compositionMode){
            this.compositionMode = compositionMode;
            return this;
        }

        public Builder setFrequency(@NonNull Integer frequency){
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

        public Builder setPort(@NonNull final Integer port){
            this.port = port;
            return this;
        }

        public DPManager build(){
            if(this.hostServer.isEmpty()){
                throw new HandlingExceptions("#### Error: The hostname is required.");
            }
            else if(this.hostServer.length() > 100){
                throw new HandlingExceptions("#### Error: The hostname is too long.");
            }
            else if(this.hostServer == null){
                throw new HandlingExceptions("#### Error: The hostname cannot be null.");
            }
            else if((this.port <= 0) || (this.port == null)){
                throw new HandlingExceptions("#### Error: port number is required. It cannot be less than or equal to zero, nor null.");
            }
            else if(this.activity == null){
                throw new HandlingExceptions("#### Error: Activity is required. An activity cannot be null.");
            }
            else if(this.compositionMode == null){
                throw new HandlingExceptions("#### Error: Compose mode cannot be null (e.g., setCompositionMode(CompositionMode.FREQUENCY)).");
            }
            else if(this.compositionMode != SEND_WHEN_IT_ARRIVES){
                if(this.compositionMode != GROUP_ALL) {
                    if(this.compositionMode != FREQUENCY) {
                        throw new HandlingExceptions("#### Error: Unidentified compositing mode. Options for composition mode are [SEND_WHEN_IT_ARRIVES, GROUP_ALL, FREQUENCY].");
                    }
                }
            }
            if(this.compositionMode == FREQUENCY){
                if(this.frequency == null){
                    throw new HandlingExceptions("#### Error: frequency cannot be null (e.g., .setFrequency(15) )");
                }
                else if(this.frequency <= 0){
                    throw new HandlingExceptions("#### Error: frequency cannot be less than or equal to zero (e.g., .setFrequency(15).");
                }
            }
            if(!this.username.equals("username")){
                if(this.username.isEmpty()){
                    throw new HandlingExceptions("#### Error: username cannot be empty.");
                }
                else if(this.username.length() > 100){
                    throw new HandlingExceptions("#### Error: username cannot be very long name.");
                }
                else if(this.username.contains(" ")){
                    throw new HandlingExceptions("#### Error: username cannot have a space in the name.");
                }
            }
            if(!this.password.equals("12345")){
                if(this.password.isEmpty()){
                    throw new HandlingExceptions("#### Error: password cannot be empty.");
                }
                else if(this.password.length() > 100){
                    throw new HandlingExceptions("#### Error: password cannot be very long name.");
                }
            }
            return new DPManager(this);
        }
    }
}
