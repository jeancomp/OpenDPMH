package br.ufma.lsdi.digitalphenotyping.phenotypecomposer.base;

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
import br.ufma.lsdi.digitalphenotyping.phenotypecomposer.database.AppDatabase;
import br.ufma.lsdi.digitalphenotyping.phenotypecomposer.database.Phenotypes;

public class DistributePhenotypeWork extends Worker {
    private static final String TAG = DistributePhenotypeWork.class.getName();
    Context context;
    AppDatabase db;
    Phenotypes phenotype;
    PublishPhenotype publishPhenotype = PublishPhenotype.getInstance();

    public DistributePhenotypeWork(@NonNull Context context, @NonNull WorkerParameters workerParams){
        super(context, workerParams);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-phenotype").build();
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        try{
            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            Date date = new Date(stamp.getTime());
            Log.i(TAG,"#### WORK EXECUTE: " + date);

            // Retrieve information
            phenotype = db.phenotypeDAO().findByPhenotypeAll();
            while (phenotype != null) {
                String stringPhenotype = phenotype.getPhenotype();
                Message msg = phenotype.getObjectFromString(stringPhenotype);
                Log.i(TAG, "#### Database Result: " + msg.getServiceValue());

                // Publish the information
                publishPhenotype.getInstance().publishPhenotypeComposer(msg);

                // Remove from database
                db.phenotypeDAO().delete(phenotype);

                phenotype = db.phenotypeDAO().findByPhenotypeAll();
            }
        }catch (Exception e){
            Log.e(TAG,"Error: " + e.toString());
            return ListenableWorker.Result.retry();
        }

        return ListenableWorker.Result.success();
    }

}
