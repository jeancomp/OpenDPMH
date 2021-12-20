package br.ufma.lsdi.digitalphenotyping.phenotypecomposer.base;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.sql.Timestamp;
import java.util.Date;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.digitalphenotyping.dpmanager.database.DatabaseManager;
import br.ufma.lsdi.digitalphenotyping.phenotypecomposer.database.Phenotypes;

public class DistributePhenotypeWork extends Worker {
    private static final String TAG = DistributePhenotypeWork.class.getName();
    private PublishPhenotype publishPhenotype = PublishPhenotype.getInstance();
    private DatabaseManager databaseManager = DatabaseManager.getInstance(getApplicationContext());
    private Context context;
    private Phenotypes phenotype;

    public DistributePhenotypeWork(@NonNull Context context, @NonNull WorkerParameters workerParams){
        super(context, workerParams);
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
            phenotype = databaseManager.getInstance().getDB().phenotypeDAO().findByPhenotypeAll();
            while (phenotype != null) {
                String stringPhenotype = phenotype.getPhenotype();
                DigitalPhenotypeEvent digitalPhenotypeEvent = phenotype.getObjectFromString(stringPhenotype);
                digitalPhenotype.setDpeList(digitalPhenotypeEvent);

                // Remove from database
                databaseManager.getInstance().getDB().phenotypeDAO().delete(phenotype);

                phenotype = databaseManager.getInstance().getDB().phenotypeDAO().findByPhenotypeAll();
            }
            if(digitalPhenotype.getDigitalPhenotypeEventList().size() > 0){
                // Publish the information
                publishPhenotype.getInstance().publishPhenotypeComposer(digitalPhenotype);
            }
        }catch (Exception e){
            Log.d(TAG,"#### Error: " + e.toString());
            return ListenableWorker.Result.retry();
        }
        return ListenableWorker.Result.success();
    }
}
