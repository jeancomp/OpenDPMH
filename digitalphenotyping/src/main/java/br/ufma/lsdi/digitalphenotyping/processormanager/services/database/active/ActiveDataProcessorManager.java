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

    public ActiveDataProcessor select(){
        activeDataProcessor = db.activeDataProcessorDAO().findByActiveDataProcessorAll();
        return activeDataProcessor;
    }

    public List<ActiveDataProcessor> selectAll(){
        List<ActiveDataProcessor> activeDataProcessorsList = new ArrayList();
        activeDataProcessor = db.activeDataProcessorDAO().findByActiveDataProcessorAll();
        while(activeDataProcessor != null) {
            activeDataProcessorsList.add(activeDataProcessor);
            activeDataProcessor = db.activeDataProcessorDAO().findByActiveDataProcessorAll();
        }
        return activeDataProcessorsList;
    }

    public void insert(String name) {
        ActiveDataProcessor activeDataProcessor = new ActiveDataProcessor();
        activeDataProcessor.setDataProcessorName(name);
        db.activeDataProcessorDAO().insertAll(activeDataProcessor);
    }

    public void delete(String name) {
        // Remove from database
        ActiveDataProcessor activeDataProcessor = new ActiveDataProcessor();
        activeDataProcessor.setDataProcessorName(name);
        db.activeDataProcessorDAO().delete(activeDataProcessor);
    }
}
