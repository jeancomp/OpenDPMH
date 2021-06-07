package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import br.ufma.lsdi.digitalphenotyping.contextdataprovider.services.ContextDataProvider;
import br.ufma.lsdi.digitalphenotyping.inferenceprocessormanager.services.InferenceProcessorManager;

import static android.content.Context.BIND_AUTO_CREATE;

public class DigitalPhenotypingManager{
    public static final String ACTIVE_SENSOR = "activesensor";
    public static final String DEACTIVATE_SENSOR = "deactivatesensor";
    private String statusCon = "undefined";
    //private final BusSystem busSystem = BusSystem.getInstance();
    private static final String TAG = DigitalPhenotypingManager.class.getName();
    private static DigitalPhenotypingManager instance = null;

    Context context;
    Activity activity;
    String clientID;
    int communicationTechnology;
    Boolean secure;
    BusSystem myService;


    public DigitalPhenotypingManager(){ }


    public DigitalPhenotypingManager(Context context, Activity activity, String clientID, int communicationTechnology, Boolean secure){
        try {
            Log.i(TAG, "#### INICIANDO FRAMEWORK");
            this.context = context;
            this.activity = activity;
            this.clientID = clientID;
            this.communicationTechnology = communicationTechnology;
            this.secure = secure;

//        busSystem.getInstance().start(context, activity, clientID, communicationTechnology);
//        if(secure) {
//            busSystem.getInstance().initSecureCDDL();
//            Log.i(TAG,"#### Iniciando busSystem com criptografia.");
//        }
//        else{
//            busSystem.getInstance().initCDDL();
//            Log.i(TAG,"#### Iniciando busSystem sem criptografia.");
//        }
//        setStatusCon(busSystem.getInstance().getStatusCon());

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


    public void start(){
        startService();
    }


    public void stop(){
        stopService();
    }


    private synchronized void startService() {
        try{
            Log.i(TAG,"#### Starts all framework services.");
            Intent intent = new Intent(getContext(), BusSystem.class);
            intent.putExtra("clientID",getClientID());
            intent.putExtra("communicationTechnology",4);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(intent);


            }
            else {
                getActivity().startService(intent);
            }
//            Intent ipm = new Intent(busSystem.getInstance().getContext(), InferenceProcessorManager.class);
//            busSystem.getContext().startService(ipm);
//
//            Intent cdp = new Intent(busSystem.getInstance().getContext(), ContextDataProvider.class);
//            busSystem.getInstance().getContext().startService(cdp);
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    private synchronized void stopService() {
        Log.i(TAG,"#### Stop all framework services.");
        try {
            Intent intent = new Intent(getContext(), BusSystem.class);
            getActivity().stopService(intent);

            //getActivity().unbindService(serviceConnection);

//            Intent ipm = new Intent(busSystem.getInstance().getContext(), InferenceProcessorManager.class);
//            busSystem.getInstance().getContext().stopService(ipm);
//
//            Intent cdp = new Intent(busSystem.getInstance().getContext(), ContextDataProvider.class);
//            busSystem.getInstance().getContext().stopService(cdp);
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }


//    ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
//            Log.i(TAG,"#### Connection service busSystem");
//            BusSystem.LocalBinder binder = (BusSystem.LocalBinder) iBinder;
//            myService = binder.getService();
//
//
//            //binder = (BusSystem.LocalBinder)iBinder;
//            //binder.getService().publisher();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName componentName) {
//            Log.i(TAG,"#### Connection service busSystem");
//        }
//    };


    public void publishMessage(String service, String message){
        Log.i(TAG,"#### Publicando mensagens");
        myService.publishMessage(service, message);
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


    public void setStatusCon(String statusCon){
        this.statusCon = statusCon;
    }


    public String getStatusCon(){
        return statusCon;
    }


    public void setBusSystem(BusSystem busSystem){
        this.myService = busSystem;
    }


    public BusSystem getBusSystem(){
        return myService;
    }


//    public void subscribeMessage(String serviceName){
//        busSystem.getInstance().subscribeMessage(serviceName);
//    }


//    public void publishMessage(String service, String text){
//        //busSystem.getInstance().publishMessage(service, text);
//    }


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
