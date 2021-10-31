package br.ufma.lsdi.digitalphenotyping.rawdatacollector.base;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.sql.Timestamp;
import java.util.Date;

import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.rawdatacollector.database.AppDatabaseRD;
import br.ufma.lsdi.digitalphenotyping.rawdatacollector.database.RawData;

public class DistributeRawDataWork extends Worker {
    private static final String TAG = DistributeRawDataWork.class.getName();
    PublishRawData publishRawData = PublishRawData.getInstance();
    Context context;
    AppDatabaseRD db;
    RawData rawData;

    public DistributeRawDataWork(@NonNull Context context, @NonNull WorkerParameters workerParams){
        super(context, workerParams);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabaseRD.class, "database-rawdata").build();
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        try{
            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            Date date = new Date(stamp.getTime());
            Log.i(TAG,"#### WORK EXECUTE RAWDATA: " + date);

            // Retrieve information
            rawData = db.rawDataDAO().findByRawDataAll();
            while (rawData != null) {
                String stringRawData = rawData.getRawdata();
                Message message = rawData.getObjectFromString(stringRawData);

                // Publish the information
                if(message != null) {
                    publishRawData.getInstance().publishRawDataComposer(message);
                }

                // Remove from database
                db.rawDataDAO().delete(rawData);

                rawData = db.rawDataDAO().findByRawDataAll();
            }
        }catch (Exception e){
            Log.e(TAG,"Error: " + e.toString());
            return ListenableWorker.Result.retry();
        }

        return ListenableWorker.Result.success();
    }

}
