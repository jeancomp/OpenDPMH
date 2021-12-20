package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
// Pegar o endereço atualizado da API, e ver se é da mesma forma contruida
// pelo Dartmount(se esta desatualizada)

//https://www.cs.dartmouth.edu/~campbell/cs65/lecture22/lecture22_2018.html
//https://developer.android.com/studio/projects/add-native-code
public class ActivityDetectionService extends Service {
    private static final String TAG = ActivityDetectionService.class.getName();
    private ActivityRecognitionClient mActivityRecognitionClient;
    private PendingIntent mPendingIntent;

    @Override
    public void onCreate() {
        Log.i(TAG,"#### Activity Recognition");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        Intent mIntentService = new Intent(this, DetectedActivityIntentService.class);
        mPendingIntent = PendingIntent.getService(this.getApplicationContext(), 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesHandler();

        return START_STICKY;
    }

    // remove the activity requested updates from Google play.
    @Override
    public void onDestroy() {
        super.onDestroy();
        // need to remove the request to Google play services. Brings down the connection
        removeActivityUpdatesHandler();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // help method for onStartCommand to request activity updates and set task listeners.
    public void requestActivityUpdatesHandler() {
        if(mActivityRecognitionClient != null) {
            Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(1000, mPendingIntent);
            // Adds a listener that is called if the Task completes successfully.
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "#### Successfully requested activity updates");
                }
            });
            // Adds a listener that is called if the Task fails.
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "#### Requesting activity updates failed to start-> " + e.getMessage());
                }
            });
        }
    }

    // remove updates and set up callbacks for success or failure
    public void removeActivityUpdatesHandler() {
        if(mActivityRecognitionClient != null){
            Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                    mPendingIntent);

            // Adds a listener that is called if the Task completes successfully.
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "#### Removed activity updates successfully!");
                }
            });

            // Adds a listener that is called if the Task fails.
            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "#### Failed to remove activity updates!");
                }
            });
        }
    }
}
