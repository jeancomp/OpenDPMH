package br.ufma.lsdi.digitalphenotyping;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.UUID;
import br.ufma.lsdi.digitalphenotyping.dataprovider.services.ContextDataProvider;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.InferenceProcessorManager;

public class DigitalPhenotypingManager implements DigitalPhenotyping {
    private static final String TAG = DigitalPhenotypingManager.class.getName();
    private String statusCon = "undefined";
    private static DigitalPhenotypingManager instance = null;
    DPApplication dpApplication = DPApplication.getInstance();

    private Context context;
    private Activity activity;
    private String clientID;
    private int communicationTechnology;
    private Boolean secure;
    private Bus myService;


    /**
     * Construtor do DigitalPhenotypingManager
     */
    public DigitalPhenotypingManager(){ }


    public DigitalPhenotypingManager(Activity activity){
        try {
            Log.i(TAG, "#### INICIANDO FRAMEWORK");
            this.activity = activity;
            this.context = dpApplication.getInstance().getContext();
            dpApplication.getInstance().setActivity(activity);
            this.clientID = UUID.randomUUID().toString().replaceAll("-","");
            this.clientID = this.clientID.toString().replaceAll("[0-9]","");
            dpApplication.getInstance().setClientID(this.clientID);
            this.communicationTechnology = 4;   // Pré-configuramos o communicationTechnology inicia por 4
            this.secure = false;                // True==Certificado digitais, False==Não usa Cert. Digitais

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
            Intent intent = new Intent(this.context, Bus.class);
            intent.putExtra("clientID",getClientID());
            intent.putExtra("communicationTechnology",this.communicationTechnology);
            intent.putExtra("secure", getSecure());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getContext().startForegroundService(intent);
            }
            else {
                getContext().startService(intent);
            }

            Intent ipm = new Intent(getContext(), InferenceProcessorManager.class);
            getContext().startService(ipm);

            Intent cdp = new Intent(getContext(), ContextDataProvider.class);
            getContext().startService(cdp);
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
            getContext().stopService(intent);

            Intent ipm = new Intent(getContext(), InferenceProcessorManager.class);
            getContext().stopService(ipm);

            Intent cdp = new Intent(getContext(), ContextDataProvider.class);
            getContext().stopService(cdp);
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }


    @Override
    public void startProcessor(String nameProcessor){
        dpApplication.getInstance().publishMessage(dpApplication.getInstance().START_PROCESSOR_TOPIC, nameProcessor);
    }


    @Override
    public void stopProcessor(String nameProcessor){
        dpApplication.getInstance().publishMessage(dpApplication.getInstance().STOP_PROCESSOR_TOPIC, nameProcessor);
    }


    @Override
    public void activaSensor(String nameSensor){
        dpApplication.getInstance().publishMessage(dpApplication.getInstance().ACTIVE_SENSOR_TOPIC, nameSensor);
    }


    @Override
    public void deactivateSensor(String nameSensor){
        dpApplication.getInstance().publishMessage(dpApplication.getInstance().DEACTIVATE_SENSOR_TOPIC, nameSensor);
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
                    android.Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.RECORD_AUDIO

                    // Outros services
            };


            if (!hasPermissions(getContext(), PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for framework");
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
            }
        }
    }
}
