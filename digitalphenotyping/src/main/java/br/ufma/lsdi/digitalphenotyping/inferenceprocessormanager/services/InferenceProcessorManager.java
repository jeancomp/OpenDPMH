package br.ufma.lsdi.digitalphenotyping.inferenceprocessormanager.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.processors.Sociability;

public class InferenceProcessorManager extends Service {
    Context context;

    public synchronized void startService() {
        Intent s = new Intent(context, Sociability.class);
        context.startService(s);
    }


    public synchronized void stopService() {
        Intent s = new Intent(context, Sociability.class);
        context.stopService(s);
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
    }
}
