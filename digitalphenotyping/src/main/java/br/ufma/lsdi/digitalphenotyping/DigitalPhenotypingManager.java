package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import br.ufma.lsdi.digitalphenotyping.dataprovider.services.ContextDataProvider;
import br.ufma.lsdi.digitalphenotyping.inferenceprocessormanager.services.InferenceProcessorManager;

public class DigitalPhenotypingManager{
    public static final String ACTIVE_SENSOR = "activesensor";
    public static final String DEACTIVATE_SENSOR = "deactivatesensor";
    private String statusCon = "undefined";
    private final BusSystem busSystem = BusSystem.getInstance();
    // MyApplication mApplication = (MyApplication)getApplicationContext();
    private static final String TAG = DigitalPhenotypingManager.class.getName();
    private static DigitalPhenotypingManager instance = null;


    public DigitalPhenotypingManager(){ }


    public DigitalPhenotypingManager(Context context, Activity activity, String clientID, int communicationTechnology, Boolean secure){
        busSystem.getInstance().start(context, activity, clientID, communicationTechnology);
        if(secure) {
            busSystem.getInstance().initSecureCDDL();
            Log.i(TAG,"#### Iniciando busSystem com criptografia.");
        }
        else{
            busSystem.getInstance().initCDDL();
            Log.i(TAG,"#### Iniciando busSystem sem criptografia.");
        }
        setStatusCon(busSystem.getInstance().getStatusCon());

        initPermissionsRequired();
    }


    public static DigitalPhenotypingManager getInstance() {
        if (instance == null) {
            instance = new DigitalPhenotypingManager();
        }
        return instance;
    }


    public void start(){
        startService();
    }


    public void stop(){
        stopService();
    }


    private synchronized void startService() {
        Log.i(TAG,"Start service framework.");
        try{
            Intent ipm = new Intent(busSystem.getInstance().getContext(), InferenceProcessorManager.class);
            busSystem.getContext().startService(ipm);

            Intent cdp = new Intent(busSystem.getInstance().getContext(), ContextDataProvider.class);
            busSystem.getInstance().getContext().startService(cdp);
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    private synchronized void stopService() {
        try {
            Intent ipm = new Intent(busSystem.getInstance().getContext(), InferenceProcessorManager.class);
            busSystem.getInstance().getContext().stopService(ipm);

            Intent cdp = new Intent(busSystem.getInstance().getContext(), ContextDataProvider.class);
            busSystem.getInstance().getContext().stopService(cdp);
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }


    public void setStatusCon(String statusCon){
        this.statusCon = statusCon;
    }


    public String getStatusCon(){
        return statusCon;
    }


//    public void subscribeMessage(String serviceName){
//        busSystem.getInstance().subscribeMessage(serviceName);
//    }


    public void publishMessage(String service, String text){
        busSystem.getInstance().publishMessage(service, text);
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

            if (!hasPermissions(busSystem.getActivity(), PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for framework");
                ActivityCompat.requestPermissions(busSystem.getActivity(), PERMISSIONS, PERMISSION_ALL);
            }
        }
    }


}
