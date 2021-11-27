package br.ufma.lsdi.digitalphenotyping.dataprocessor.database;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class DataProcessorManager {
    private static final String TAG = DataProcessorManager.class.getName();
    private static DataProcessorManager instance = null;
    private static Context context;
    private AppDatabaseDP db;
    private String DATABASE_NAME = "database-digitalphenotypeevent";

    public DataProcessorManager(Context context){
        this.context = context;
        db = Room.databaseBuilder(context,
                AppDatabaseDP.class, DATABASE_NAME).build();
    }

    public static DataProcessorManager getInstance() {
        if (instance == null) {
            instance = new DataProcessorManager(context);
        }
        return instance;
    }

    public List<Phenotypes> select(String dataProcessorName){
        //int total = db.phenotypeDAO().totalPhenotypes(dataProcessorName);
        //Log.i(TAG,"#### Total registros: " + total);
        List<Phenotypes> list = new ArrayList();
        list = db.phenotypeDAO().findByPhenotypeAll(dataProcessorName);
        //Log.i(TAG,"#### y: " + list.size() + ", " + list.get(0).getDataProcessorName() + ", " + list.get(1).getDataProcessorName() + ", " + list.get(2).getDataProcessorName() + ", " + list.get(1).getDataProcessorName());
        return list;
    }

    public void insert(Phenotypes phenotypes) {
        try {
            db.phenotypeDAO().insert(phenotypes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int totalRecords(String dataProcessorName){
        return db.phenotypeDAO().totalPhenotypes(dataProcessorName);
    }

    public void delete(Phenotypes phenotypes) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // Remove from database
                    db.phenotypeDAO().delete(phenotypes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public boolean checkDatabaseCreation(){
        final String DB_PATH = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        if(DB_PATH == null){
            return false;
        }
        return true;
    }
}
