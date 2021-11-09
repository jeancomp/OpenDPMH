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

import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.digitalphenotyping.phenotypecomposer.database.AppDatabasePC;
import br.ufma.lsdi.digitalphenotyping.phenotypecomposer.database.Phenotypes;

public class DistributePhenotypeWork extends Worker {
    private static final String TAG = DistributePhenotypeWork.class.getName();
    Context context;
    AppDatabasePC db;
    Phenotypes phenotype;
    PublishPhenotype publishPhenotype = PublishPhenotype.getInstance();

    public DistributePhenotypeWork(@NonNull Context context, @NonNull WorkerParameters workerParams){
        super(context, workerParams);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabasePC.class, "database-phenotype").build();
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        try{
            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            Date date = new Date(stamp.getTime());
            Log.i(TAG,"#### WORK EXECUTE PHENOTYPE: " + date);

            DigitalPhenotype digitalPhenotype = new DigitalPhenotype();

            // Retrieve information
            phenotype = db.phenotypeDAO().findByPhenotypeAll();
            while (phenotype != null) {
                String stringPhenotype = phenotype.getPhenotype();
                DigitalPhenotypeEvent digitalPhenotypeEvent = phenotype.getObjectFromString(stringPhenotype);
                digitalPhenotype.setDpeList(digitalPhenotypeEvent);

                // Remove from database
                db.phenotypeDAO().delete(phenotype);

                phenotype = db.phenotypeDAO().findByPhenotypeAll();
            }
            if(digitalPhenotype.getDigitalPhenotypeEventList().size() > 0){
                // Publish the information
                publishPhenotype.getInstance().publishPhenotypeComposer(digitalPhenotype);
            }
        }catch (Exception e){
            Log.e(TAG,"Error: " + e.toString());
            return ListenableWorker.Result.retry();
        }

        return ListenableWorker.Result.success();
    }

}
