package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import br.ufma.lsdi.digitalphenotyping.dataprovider.services.ContextDataProvider;
import br.ufma.lsdi.digitalphenotyping.inferenceprocessormanager.services.InferenceProcessorManager;

public class DigitalPhenotypingManager{
    public static final String ACTIVE_SENSOR = "activesensor";
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
}
