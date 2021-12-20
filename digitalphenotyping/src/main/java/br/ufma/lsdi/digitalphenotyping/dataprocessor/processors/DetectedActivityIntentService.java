package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import br.ufma.lsdi.cddl.message.Message;

public class DetectedActivityIntentService extends IntentService {
    private static final String TAG = DetectedActivityIntentService.class.getName();
    private PhysicalActivity physicalActivity;
    private Context context;
    private Send send;

    public DetectedActivityIntentService(){
        super("DetectedActivityIntentService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DetectedActivityIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /*try {
            Intent intent = new Intent(getBaseContext(), PhysicalActivity.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            } else {
                startService(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "#### Error: " + e.getMessage());
        }*/
        this.context = getBaseContext();
        send = new Send();
        send.getInstance().setContext(context);
    }

    /*public PhysicalActivity getPhysicalActivityService(){
        Intent intent = new Intent(getBaseContext(), PhysicalActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getBaseContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            getBaseContext().startService(intent);
        }
        return physicalActivity;
    }*/

    /*ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "#### Connection service PhysicalActivity");
            PhysicalActivity.LocalBinder binder = (PhysicalActivity.LocalBinder) iBinder;
            physicalActivity = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "#### Disconnection service MainService");
        }
    };*/

   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if (physicalActivity != null) {
            unbindService((ServiceConnection) physicalActivity);
        }*/
        send.getInstance().stop();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG,"#### Activity Detection->onHandleIntent()");
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level (between 0-100)

        List<DetectedActivity> detectedActivities = result.getProbableActivities();

        int typeMax=0;
        int percentageMax=0;
        for (DetectedActivity activity : detectedActivities) {
            Log.i(TAG, "#### Detected activity: " + activity.getType() + ", " + activity.getConfidence());
            if(typeMax < activity.getType()){
                typeMax = activity.getType();
                percentageMax = activity.getConfidence();
            }
        }
        handleUserActivity(typeMax, percentageMax);
    }

    private void handleUserActivity(int type, int confidence) {
        String label = "Unknown";
        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = "In_Vehicle"; // automóvel
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = "On_Bicycle"; // bicicleta
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = "On_Foot";  // a pé
                break;
            }
            case DetectedActivity.RUNNING: {
                label = "Running";  // correndo
                break;
            }
            case DetectedActivity.STILL: {
                label = "Still";  // parado! celular estar em cima da mesa
                break;
            }
            case DetectedActivity.TILTING: {
                label = "Tilting"; // inclinado
                break;
            }
            case DetectedActivity.WALKING: {
                label = "Walking"; // andando
                break;
            }
            case DetectedActivity.UNKNOWN: {
                break;  // desconhecido
            }
            //mTextARLabel.setText(label);
            //mTextConfidence.setText(confidence+"");
        }
        //Log.i(TAG, "#### Broadcast:onReceive(): Activity is " + label + " and confidence level is: " + confidence);
        long timestamp = System.currentTimeMillis();
        Object[] valor = {label, confidence, timestamp};
        String[] str = {"Type of activity", "Confidence", "timestamp"};
        Message message = new Message();
        message.setServiceValue(valor);
        message.setAvailableAttributesList(str);
        message.setAvailableAttributes(3);
        //getPhysicalActivityService().inferencePhenotypingEvent(message);
        send.getInstance().conectPhysicalActivity(message);
    }

    //--------------------------------------------------------------------------------------------------
    public static class Send {
        Context context;
        Message message = new Message();
        private static Send instance = null;

        public Send() {
        }

        public static Send getInstance() {
            if (instance == null) {
                instance = new Send();
            }
            return instance;
        }

        public void stop(){
            if(serviceConnection != null) {
                context.unbindService(serviceConnection);
            }
        }

        public void setContext(Context c) {
            context = c;
        }

        public void conectPhysicalActivity(Message m) {
            try {
                message = m;
                Intent intent = new Intent(context, PhysicalActivity.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                } else {
                    context.startService(intent);
                }
            } catch (Exception e) {
                Log.e("PhysicalActivity", "#### Error: " + e.getMessage());
            }
        }


        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.i("PhysicalActivity", "#### Connection service MainService");
                PhysicalActivity.LocalBinder binder = (PhysicalActivity.LocalBinder) iBinder;
                PhysicalActivity myService = binder.getService();

                myService.inferencePhenotypingEvent(message);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.i("PhysicalActivity", "#### Disconnection service MainService");
            }
        };
    }
}

/*
 Confidence
A API retorna uma lista de atividades com seus níveis de confiança associados (entre 0-100, onde 100
é uma confiança muito alta e o 0 é uma confiança muito (ou, nenhuma / baixa). Na transição entre caminhar e correr
- todas essas atividades podem ser muito semelhantes se você estiver apenas olhando para o sinais brutos
do acelerômetro X, Y, Z.

        ANDANDO (confiança = 80)
        RUNNING (confiança = 20)
        IN_VEHICLE (confiança = 10)

A frequencia que ocorre a detecção em intervalos de millisegundos, feita no requestActivityUpdates()

 Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(1, mPendingIntent);
*/
