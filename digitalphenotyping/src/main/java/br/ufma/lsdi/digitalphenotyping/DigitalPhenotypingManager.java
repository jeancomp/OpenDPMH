package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.util.List;
import br.ufma.lsdi.digitalphenotyping.dataprovider.ContextDataProvider;
import br.ufma.lsdi.digitalphenotyping.inferenceprocessormanager.services.InferenceProcessorManager;

public class DigitalPhenotypingManager {
    public String clientID;
    public Boolean secure = false;
    public Context context;
    public Activity activity;
    public String statusCon = "undefined";
    private final ContextDataProvider contextDataProvider = ContextDataProvider.getInstance();
    private static final String TAG = DigitalPhenotypingManager.class.getName();
    private static DigitalPhenotypingManager instance = null;


    public DigitalPhenotypingManager(){}


    public DigitalPhenotypingManager(Context context, String clientID, Boolean secure){
        Log.i(TAG,"#### DigitalPhenotypingManager() ----> clientID, secure:  " + clientID + ", " + secure);
        this.context = context;
        this.clientID = clientID;
        this.secure = secure;
    }


    public static DigitalPhenotypingManager getInstance() {
        if (instance == null) {
            instance = new DigitalPhenotypingManager();
        }
        return instance;
    }


    public void start(){
        initFramework();
    }


    private void initFramework(){
        contextDataProvider.getInstance().start(getContext(), getActivity(), getClientID());
        if(secure) {
            contextDataProvider.getInstance().initSecureCDDL();
            Log.i(TAG,"#### Iniciando CDDL com criptografia.");
        }
        else{
            contextDataProvider.getInstance().initCDDL();
            Log.i(TAG,"#### Iniciando CDDL sem criptografia.");
        }
        setStatusCon(contextDataProvider.getInstance().getStatusCon());

        startService();
    }


    public void stop(){
        stopService();
    }


    private synchronized void startService() {
        try{
            Intent ipm = new Intent(context, InferenceProcessorManager.class);
            context.startService(ipm);
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    private synchronized void stopService() {
        try {
            Intent ipm = new Intent(context, InferenceProcessorManager.class);
            context.stopService(ipm);
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }


    public void setClientID(String clientID){
        this.clientID = clientID;
    }


    public String getClientID(){
        return clientID;
    }


    public Context getContext() {
        return context;
    }


    public void setContext(Context context) {
        this.context= context;
    }


    public Activity getActivity(){ return activity; }


    public void setActivity(Activity activity){
        this.activity = activity;
    }


    public String getStatusCon(){
        return this.statusCon;
    }


    public void setStatusCon(String conexao){
        this.statusCon = conexao;
    }


    public List<String> getListVirtualSensor(){
        return contextDataProvider.getInstance().listSensoresVirtuais();
    }


    public Boolean startVirtualSensor(String nameSensor){
        List<String> sensor = getListVirtualSensor();

        for(int i = 0; i < sensor.size(); i++)
        {
            if(sensor.get(i).equalsIgnoreCase(nameSensor)){
                contextDataProvider.getInstance().startVirtualSensor(nameSensor);
                Log.i(TAG,"#### Start virtual sensor");
                return true;
            }
        }
        Log.i(TAG,"#### Erro: start virtual sensor failure");
        return false;
    }


    public void startAllVirtualSensor(){
        contextDataProvider.getInstance().startAllVirtualSensors();
    }


    public void subscribeMessage(String serviceName){
        contextDataProvider.getInstance().subscribeMessage(serviceName);
    }


}
