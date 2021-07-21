package br.ufma.lsdi.digitalphenotyping.processormanager.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.util.List;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Sociability;

public class InferenceProcessorManager extends Service {
    private static final String TAG = InferenceProcessorManager.class.getName();
    Context context;
    List<String> processors = null;

    @Override
    public void onCreate() {
        Log.i(TAG,"#### Starting InferenceProcessorManager");
        startProcessors();
    }

    public synchronized void startProcessors() {
        try {
            Intent s = new Intent(context, Sociability.class);
            context.startService(s);
            Log.i(TAG,"#### Starting inference services");
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    public synchronized void stopProcessors() {
        try {
            Intent s = new Intent(context, Sociability.class);
            context.stopService(s);
            Log.i(TAG,"#### Stopping inference services");
        }catch (Exception e){
            Log.e(TAG,"#### Error: " + e.toString());
        }
    }


    public final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public InferenceProcessorManager getService() {
            return InferenceProcessorManager.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // código
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopProcessors();
    }
}
