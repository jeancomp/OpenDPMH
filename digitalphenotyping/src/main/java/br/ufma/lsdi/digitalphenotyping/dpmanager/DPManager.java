package br.ufma.lsdi.digitalphenotyping.dpmanager;

import static br.ufma.lsdi.digitalphenotyping.CompositionMode.FREQUENCY;
import static br.ufma.lsdi.digitalphenotyping.CompositionMode.GROUP_ALL;
import static br.ufma.lsdi.digitalphenotyping.CompositionMode.SEND_WHEN_IT_ARRIVES;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Publisher;
import br.ufma.lsdi.cddl.pubsub.PublisherFactory;
import br.ufma.lsdi.digitalphenotyping.CompositionMode;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.database.DataProcessorManager;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.database.Phenotypes;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidActivityException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidCompositionModeException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidContextException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidDataProcessorNameException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidFrequencyException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidHostServerException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidMainServiceException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidPasswordException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidPortException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidUsernameException;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessor;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessorManager;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessor;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessorManager;

/**
 * DPInterface class is responsible for starting the framework's main service (MainService).
 */
public class DPManager implements DPInterface {
    private static final String TAG = DPManager.class.getName();
    private Publisher publisher = PublisherFactory.createPublisher();
    private static DPManager instance = null;
    private Context context;
    private Activity activity;
    private String clientID;
    private int communicationTechnology;
    private Boolean secure;
    private DPManagerService myService;
    private boolean servicesStarted = false;
    private static Builder builderCopy;
    private List<Phenotypes> phenotypesList = new ArrayList();


    /**
     * Construtor do DPManager
     */
    public DPManager() {}


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
            instance = new DPManager(builderCopy);
        }
        return instance;
    }


    /**
     * Starts the framework giving start service on the main services of the framework.
     */
    @Override
    public void start() {
        try {
            Log.i(TAG, "#### FRAMEWORK STARTED");

            //this.activity = activity;
            this.context = (Context) builderCopy.activity;

            this.communicationTechnology = 4;
            this.secure = false;

            initPermissionsRequired();

            if(!servicesStarted) {
                Log.i(TAG, "#### Started framework MainService.");
                Intent intent = new Intent(this.activity, DPManagerService.class);

                //ActivityParcelable activityParcelable = new ActivityParcelable();
                //activityParcelable.setActivity(this.activity);
                //intent.putExtra("activity", (Parcelable) activityParcelable);

                intent.putExtra("compositionmode", builderCopy.compositionMode);
                intent.putExtra("hostserver", builderCopy.hostServer);
                intent.putExtra("port", builderCopy.port);
                intent.putExtra("username", builderCopy.username);
                intent.putExtra("password", builderCopy.password);
                intent.putExtra("clientid", createClientID());
                intent.putExtra("activerawdatacollector", builderCopy.activerawdatacollector);
                if(builderCopy.compositionMode == FREQUENCY) {
                    intent.putExtra("frequency", builderCopy.frequency);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getContext().startService(intent);
                    //getContext().startForegroundService(intent);
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
        if(myService != null) {
            myService.stopForeground(true);
        }

        try {
            if(servicesStarted) {
                Intent intent = new Intent(getContext(), DPManagerService.class);
                getContext().stopService(intent);

                servicesStarted = false;
            }
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }
    }


    public String createClientID(){
        this.clientID = UUID.randomUUID().toString().replaceAll("-","");
        this.clientID = this.clientID.toString().replaceAll("[0-9]","");
        //this.clientID = "febfcfbccaeabda";
        Log.i(TAG,"#### New clientID: " + this.clientID);
        return clientID;
    }


    public String getClientID(){
        return this.clientID;
    }


    public void foregroundAPP(){
        Log.i(TAG,"#### Ativa foreground app");
        Intent intent = new Intent(getContext(), DPManagerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            getContext().startService(intent);
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "#### Connection service MainService");
            DPManagerService.LocalBinder binder = (DPManagerService.LocalBinder) iBinder;
            myService = binder.getService();

            myService.foregroundAPP();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "#### Disconnection service MainService");
        }
    };


    /**
     * Start framework processors passing a list of processors as parameters.
     * @param dataProcessorsName a list of processors.
     */
    @Override
    public void startDataProcessors(List<String> dataProcessorsName) throws InvalidDataProcessorNameException {
        if(dataProcessorsName.isEmpty()){
            throw new InvalidDataProcessorNameException("#### Error: dataProcessorsName cannot be empty.");
        }
        else if(dataProcessorsName == null){
            throw new InvalidDataProcessorNameException("#### Error: dataProcessorsName cannot be null.");
        }
        if(servicesStarted) {
            Log.i(TAG, "#### Started processors");
            if (!dataProcessorsName.isEmpty()) {
                for (int i = 0; i < dataProcessorsName.size(); i++) {
                    publishMessage(Topics.START_DATAPROCESSOR_TOPIC.toString(), dataProcessorsName.get(i).toString());
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
    public void stopDataProcessors(List<String> dataProcessorsName) throws InvalidDataProcessorNameException {
        if(dataProcessorsName.isEmpty()){
            throw new InvalidDataProcessorNameException("#### Error: dataProcessorsName cannot be empty.");
        }
        else if(dataProcessorsName == null){
            throw new InvalidDataProcessorNameException("#### Error: dataProcessorsName cannot be null.");
        }
        if(servicesStarted) {
            Log.i(TAG, "#### Stopped processors");
            if (!dataProcessorsName.isEmpty()) {
                for (int i = 0; i < dataProcessorsName.size(); i++) {
                    publishMessage(Topics.STOP_DATAPROCESSOR_TOPIC.toString(), dataProcessorsName.get(i).toString());
                }
            }
        }
        else{
            Log.e(TAG,"#### Error: Started MainService Service");
        }
    }


    /**
     * Search all data processors available in the framework
     * @return a list of processors.
     */
    @Override
    public List<ListDataProcessor> getDataProcessorsList(){
        ListDataProcessorManager ldpManager = ListDataProcessorManager.getInstance();
        List<ListDataProcessor> listDataProcessor = new ArrayList();
        listDataProcessor = ldpManager.getInstance().select();
        return listDataProcessor;
    }


    /**
     * Search for all active data processors that are running in the framework.
     * @return a list of active data processors.
     */
    @Override
    public List<ActiveDataProcessor> getActiveDataProcessorsList(){
        ActiveDataProcessorManager adpManager = ActiveDataProcessorManager.getInstance();
        List<ActiveDataProcessor> activeDataProcessorList = new ArrayList();
        activeDataProcessorList = adpManager.getInstance().select();
        return activeDataProcessorList;
    }


    @Override
    public List<Phenotypes> getPhenotypesList(String situationInterest){
        DataProcessorManager dataProcessorManager = new DataProcessorManager(getContext());
        if(dataProcessorManager.checkDatabaseCreation()){
            Log.i(TAG,"#### dpm");
            try {
                new AddItemTask().execute(situationInterest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            phenotypesList = null;
        }
        return phenotypesList;
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
    public void setContext(Context context) throws InvalidContextException {
        if(context == null){
            throw new InvalidContextException("#### Error: context cannot be null.");
        }
        this.context = context;
    }


    /**
     * Set application activity.
     * @param activity there is application activity.
     */
    public void setActivity(Activity activity) throws InvalidActivityException {
        if(activity == null){
            throw new InvalidActivityException("#### Error: activity cannot be null.");
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
            //throw new HandlingExceptions("#### Error: secure cannot be null.");
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
    public boolean getServicesStarted(){
        return servicesStarted;
    }


    /**
     *
     * @param DPManagerService
     */
    public void setMainService(DPManagerService DPManagerService) throws InvalidMainServiceException {
        if(DPManagerService == null){
            throw new InvalidMainServiceException("#### Error: mainService cannot be null.");
        }
        this.myService = DPManagerService;
    }


    /**
     *
     * @return
     */
    public DPManagerService getMainService(){
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
                Manifest.permission.FOREGROUND_SERVICE
                /*Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE*/

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
        private String port = "";
        private String username = "username";
        private String password = "12345";
        private CompositionMode compositionMode = null;
        private Integer frequency = null;
        private boolean activerawdatacollector = false;

        public Builder(Activity activity){
            this.activity = activity;
        }

        public Builder setExternalServer(final String host, final String port){
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

        public Builder setPort(@NonNull final String port){
            this.port = port;
            return this;
        }

        public Builder setRawDataCollector(@NonNull final boolean activerawdatacollector){
            this.activerawdatacollector = activerawdatacollector;
            return this;
        }

        public DPManager build() throws InvalidHostServerException, InvalidPortException, InvalidActivityException, InvalidCompositionModeException, InvalidFrequencyException, InvalidUsernameException, InvalidPasswordException {
            if(this.hostServer.isEmpty()){
                throw new InvalidHostServerException("#### Error: The hostname is required.");
            }
            else if(this.hostServer.length() > 100){
                throw new InvalidHostServerException("#### Error: The hostname is too long.");
            }
            else if(this.hostServer == null){
                throw new InvalidHostServerException("#### Error: The hostname cannot be null.");
            }
            else if((this.port.equals("0")) || (this.port == null)){
                throw new InvalidPortException("#### Error: port number is required. It cannot be less than or equal to zero, nor null.");
            }
            else if(this.activity == null){
                throw new InvalidActivityException("#### Error: Activity is required. An activity cannot be null.");
            }
            else if(this.compositionMode == null){
                throw new InvalidCompositionModeException("#### Error: Compose mode cannot be null (e.g., setCompositionMode(CompositionMode.FREQUENCY)).");
            }
            else if(this.compositionMode != SEND_WHEN_IT_ARRIVES){
                if(this.compositionMode != GROUP_ALL) {
                    if(this.compositionMode != FREQUENCY) {
                        throw new InvalidCompositionModeException("#### Error: Unidentified compositing mode. Options for composition mode are [SEND_WHEN_IT_ARRIVES, GROUP_ALL, FREQUENCY].");
                    }
                }
            }
            if(this.compositionMode == FREQUENCY){
                if(this.frequency == null){
                    throw new InvalidFrequencyException("#### Error: frequency cannot be null (e.g., .setFrequency(15) )");
                }
                else if(this.frequency <= 0){
                    throw new InvalidFrequencyException("#### Error: frequency cannot be less than or equal to zero (e.g., .setFrequency(15).");
                }
            }
            if(!this.username.equals("username")){
                if(this.username.isEmpty()){
                    throw new InvalidUsernameException("#### Error: username cannot be empty.");
                }
                else if(this.username.length() > 100){
                    throw new InvalidUsernameException("#### Error: username cannot be very long name.");
                }
                else if(this.username.contains(" ")){
                    throw new InvalidUsernameException("#### Error: username cannot have a space in the name.");
                }
            }
            if(!this.password.equals("12345")){
                if(this.password.isEmpty()){
                    throw new InvalidPasswordException("#### Error: password cannot be empty.");
                }
                else if(this.password.length() > 100){
                    throw new InvalidPasswordException("#### Error: password cannot be very long name.");
                }
            }
            return new DPManager(this);
        }
    }


    private class AddItemTask extends AsyncTask<String, Void, List<Phenotypes>> {
        DataProcessorManager dataProcessorManager = new DataProcessorManager(getContext());

        @Override
        protected List<Phenotypes> doInBackground(String... params) {
            List<Phenotypes> l = new ArrayList();
            l = dataProcessorManager.select(params[0]);
            return l;
        }

        @Override
        protected void onPostExecute(List<Phenotypes> result) {
            processValue(result);
        }
    }


    public void processValue(List<Phenotypes> myValue) {
        phenotypesList = myValue;
    }
}
