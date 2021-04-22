package br.ufma.lsdi.digitalphenotyping;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import br.ufma.lsdi.digitalphenotyping.datacontroller.DataControllerManager;
import br.ufma.lsdi.digitalphenotyping.inferenceprocessormanager.services.InferenceProcessorManager;

public abstract class DigitalPhenotypingManager {
    Context context;
    DataControllerManager dataControllerManager = new DataControllerManager();
    private static final String TAG = DigitalPhenotypingManager.class.getName();

    public void start(Context context){
        this.context = context;

        dataControllerManager.start(context);

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
            Log.e(TAG,e.getMessage());
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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context= context;
    }
}
