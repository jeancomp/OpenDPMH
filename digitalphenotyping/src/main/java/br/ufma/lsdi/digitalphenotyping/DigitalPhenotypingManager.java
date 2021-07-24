package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.UUID;
import br.ufma.lsdi.digitalphenotyping.dataprovider.services.ContextDataProvider;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.InferenceProcessorManager;

public class DigitalPhenotypingManager implements DigitalPhenotyping {
    public static final String ACTIVE_SENSOR = "activesensor";
    public static final String DEACTIVATE_SENSOR = "deactivatesensor";
    private String statusCon = "undefined";
    private static final String TAG = DigitalPhenotypingManager.class.getName();
    private static DigitalPhenotypingManager instance = null;
    DPApplication dpApplication = DPApplication.getInstance();
    Topic topic = Topic.J;

    private Context context;
    private Activity activity;
    private String clientID;
    //private String clientID = UUID.randomUUID().toString();
    private int communicationTechnology;
    private Boolean secure;
    private Bus myService;


    /**
     * Construtor do DigitalPhenotypingManager
     */
    public DigitalPhenotypingManager(){ }


    public DigitalPhenotypingManager(Activity activity, String clientID, int communicationTechnology, Boolean secure){
        try {
            Log.i(TAG, "#### INICIANDO FRAMEWORK");
            this.activity = activity;
            this.context = dpApplication.getInstance().getContext();
            dpApplication.getInstance().setActivity(activity);
            this.clientID = clientID;
            this.communicationTechnology = communicationTechnology;
            this.secure = secure;

            initPermissionsRequired();
        }catch (Exception e){
            Log.e(TAG,"Error: " + e.toString());
        }
    }


    public static DigitalPhenotypingManager getInstance() {
        if (instance == null) {
            instance = new DigitalPhenotypingManager();
        }
        return instance;
    }

    @Override
    public synchronized void start(){
        try{
            Log.i(TAG,"#### Starts all framework services.");
            Intent intent = new Intent(getActivity(), Bus.class);
            intent.putExtra("clientID",getClientID());
            intent.putExtra("communicationTechnology",4);
            intent.putExtra("secure", getSecure());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(intent);
            }
            else {
                getActivity().startService(intent);
            }

            Intent ipm = new Intent(getContext(), InferenceProcessorManager.class);
            getActivity().startService(ipm);

            Intent cdp = new Intent(getContext(), ContextDataProvider.class);
            getActivity().startService(cdp);
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    @Override
    public void stop(){
        Log.i(TAG,"#### Stop all framework services.");
        // PARA O SERVICE PRIMEIRO PLANO
        myService.stopForeground(true);

        try {
            Intent intent = new Intent(getContext(), Bus.class);
            getActivity().stopService(intent);

            Intent ipm = new Intent(getContext(), InferenceProcessorManager.class);
            getActivity().stopService(ipm);

            Intent cdp = new Intent(getContext(), ContextDataProvider.class);
            getActivity().stopService(cdp);
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }


    @Override
    public void startProcessor(String nameProcessor){
        dpApplication.getInstance().publishMessage(topic.START_PROCESSOR, nameProcessor);
    }


    @Override
    public void stopProcessor(String nameProcessor){
        dpApplication.getInstance().publishMessage(topic.STOP_PROCESSOR, nameProcessor);
    }


    @Override
    public void activaSensor(String nameSensor){
        dpApplication.getInstance().publishMessage(topic.ACTIVE_SENSOR, nameSensor);
    }


    @Override
    public void deactivateSensor(String nameSensor){
        dpApplication.getInstance().publishMessage(topic.DEACTIVATE_SENSOR, nameSensor);
    }


    @Override
    public synchronized void publish(String service, String text) {
        dpApplication.getInstance().publishMessage(service, text);
    }


    @Override
    public void subscriber(){

    }

    public Context getContext() {
        return context;
    }


    public void setContext(Context context) {
        this.context= context;
    }


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


    public void setCommunicationTechnology(int communicationTechnology) {
        this.communicationTechnology = communicationTechnology;
    }


    public int getCommunicationTechnology() {
        return communicationTechnology;
    }


    public void setSecure(Boolean secure) {
        this.secure = secure;
    }

    public Boolean getSecure() {
        return this.secure;
    }


    public void setStatusCon(String statusCon){
        this.statusCon = statusCon;
    }


    public String getStatusCon(){
        return statusCon;
    }


    public void setBusSystem(Bus bus){
        this.myService = bus;
    }


    public Bus getBusSystem(){
        return myService;
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


    @Override
    public void initPermissionsRequired() {
        // Checa as permissões para rodar os sensores virtuais
        int PERMISSION_ALL = 1;

        if (true) {
            String[] PERMISSIONS = {
                    // Service location
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.FOREGROUND_SERVICE

                    // Outros services
            };


            if (!hasPermissions(getActivity(), PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for framework");
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
            }
        }
    }
}
