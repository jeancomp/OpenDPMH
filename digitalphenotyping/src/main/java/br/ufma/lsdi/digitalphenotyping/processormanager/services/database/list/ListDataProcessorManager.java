package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list;

import android.content.Context;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class ListDataProcessorManager {
    private static final String TAG = ListDataProcessorManager.class.getName();
    private static ListDataProcessorManager instance = null;
    private ListDataProcessor listDataProcessor;
    private static Context context;
    private AppDatabasePM2 db;

    public ListDataProcessorManager(Context context){
        this.context = context;
        db = Room.databaseBuilder(context,
                AppDatabasePM2.class, "database-listdataprocessor").build();
    }

    public static ListDataProcessorManager getInstance() {
        if (instance == null) {
            instance = new ListDataProcessorManager(context);
        }
        return instance;
    }

    public ListDataProcessor select(){
        listDataProcessor = db.listDataProcessorDAO().findByListDataProcessorAll();
        return listDataProcessor;
    }


    public List<ListDataProcessor> selectAll(){
        List<ListDataProcessor> listDP = new ArrayList();
        listDataProcessor = db.listDataProcessorDAO().findByListDataProcessorAll();
        while (listDataProcessor != null){
            listDP.add(listDataProcessor);
            listDataProcessor = db.listDataProcessorDAO().findByListDataProcessorAll();
        }
        return listDP;
    }

    public void insert(String name) {
        ListDataProcessor listDataProcessor = new ListDataProcessor();
        listDataProcessor.setDataProcessorName(name);
        db.listDataProcessorDAO().insertAll(listDataProcessor);
    }

    public void delete(String name) {
        // Remove from database
        ListDataProcessor listDataProcessor = new ListDataProcessor();
        listDataProcessor.setDataProcessorName(name);
        db.listDataProcessorDAO().delete(listDataProcessor);
    }
}
