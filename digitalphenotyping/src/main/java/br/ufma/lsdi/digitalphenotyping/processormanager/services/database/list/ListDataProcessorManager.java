package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

public class ListDataProcessorManager {
    private static final String TAG = ListDataProcessorManager.class.getName();
    private static ListDataProcessorManager instance = null;
    //List<ListDataProcessor> listDataProcessor = new ArrayList();
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

    public List<ListDataProcessor> select(){
        int total = db.listDataProcessorDAO().totalDataProcessorAll();
        Log.i(TAG,"#### Total registros: " + total);
        List<ListDataProcessor> list = new ArrayList();
        list = db.listDataProcessorDAO().findByListDataProcessorAll();
        Log.i(TAG,"#### y: " + list.size() + ", " + list.get(0).getDataProcessorName() + ", " + list.get(1).getDataProcessorName() + ", " + list.get(2).getDataProcessorName() + ", " + list.get(1).getDataProcessorName());
        return list;
    }

    public void insert(String name) {
        /*Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    ListDataProcessor listDataProcessor = new ListDataProcessor();
                    listDataProcessor.setDataProcessorName(name);
                    db.listDataProcessorDAO().insert(listDataProcessor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();*/
        try {
            ListDataProcessor listDataProcessor = new ListDataProcessor();
            listDataProcessor.setDataProcessorName(name);
            db.listDataProcessorDAO().insert(listDataProcessor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int totalRecords(){
        return db.listDataProcessorDAO().totalDataProcessorAll();
    }

    public void delete(String name) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    // Remove from database
                    ListDataProcessor listDataProcessor = new ListDataProcessor();
                    listDataProcessor.setDataProcessorName(name);
                    db.listDataProcessorDAO().delete(listDataProcessor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        //thread.interrupt();
    }
}
