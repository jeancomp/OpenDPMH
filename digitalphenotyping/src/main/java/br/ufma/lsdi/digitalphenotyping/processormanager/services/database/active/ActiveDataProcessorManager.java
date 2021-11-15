package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class ActiveDataProcessorManager {
    private static final String TAG = ActiveDataProcessorManager.class.getName();
    private static ActiveDataProcessorManager instance = null;
    private ActiveDataProcessor activeDataProcessor;
    private static Context context;
    private AppDatabasePM1 db;

    public ActiveDataProcessorManager(Context context){
        this.context = context;
        db = Room.databaseBuilder(context,
                AppDatabasePM1.class, "database-activedataprocessor").build();
    }

    public static ActiveDataProcessorManager getInstance() {
        if (instance == null) {
            instance = new ActiveDataProcessorManager(context);
        }
        return instance;
    }

    public List<ActiveDataProcessor> select(){
        List<ActiveDataProcessor> list = new ArrayList();
        list = db.activeDataProcessorDAO().findByActiveDataProcessorAll();
        return list;
    }

    public void insert(String name) {
        try {
            ActiveDataProcessor activeDataProcessor = new ActiveDataProcessor();
            activeDataProcessor.setDataProcessorName(name);
            db.activeDataProcessorDAO().insert(activeDataProcessor);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int totalRecords(){
        return db.activeDataProcessorDAO().totalActiveDataProcessorAll();
    }

    public void delete(String name) {
        try {
            // Remove from database
            ActiveDataProcessor activeDataProcessor = new ActiveDataProcessor();
            activeDataProcessor.setDataProcessorName(name);
            db.activeDataProcessorDAO().delete(activeDataProcessor);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
