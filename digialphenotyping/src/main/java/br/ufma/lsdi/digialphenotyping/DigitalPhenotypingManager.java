package br.ufma.lsdi.digialphenotyping;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.network.ConnectionImpl;
import br.ufma.lsdi.digialphenotyping.inferenceprocessormanager.services.InferenceProcessorManager;

public abstract class DigitalPhenotypingManager {
    Context context;
    private static final String TAG = DigitalPhenotypingManager.class.getName();

    public void start(Context context){
        this.context = context;

        startService();
    }

    public void stop(){
        stopService();
    }

    public synchronized void startService() {
        try{
            Intent ipm = new Intent(context, InferenceProcessorManager.class);
            context.startService(ipm);
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
    }

    public synchronized void stopService() {
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
