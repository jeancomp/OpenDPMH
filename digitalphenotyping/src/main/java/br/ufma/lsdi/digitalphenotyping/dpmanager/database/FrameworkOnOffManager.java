package br.ufma.lsdi.digitalphenotyping.dpmanager.database;

import android.content.Context;

import androidx.room.Room;

public class FrameworkOnOffManager {
    private static final String TAG = FrameworkOnOffManager.class.getName();
    private static FrameworkOnOffManager instance = null;
    private static Context context;
    private AppDatabaseDPM db;

    public FrameworkOnOffManager(Context context){
        this.context = context;
        db = Room.databaseBuilder(context,
                AppDatabaseDPM.class, "database-frameworkonoff").build();
    }

    public static FrameworkOnOffManager getInstance() {
        if (instance == null) {
            instance = new FrameworkOnOffManager(context);
        }
        return instance;
    }

    public FrameworkOnOff select(){
        FrameworkOnOff frameworkOnOff = new FrameworkOnOff();
        frameworkOnOff = db.frameworkOnOffDAO().findByFrameworkStatus();
        return frameworkOnOff;
    }

    public void update(Boolean value){
        FrameworkOnOff frameworkOnOff = new FrameworkOnOff();
        frameworkOnOff.setStatus(value);
        db.frameworkOnOffDAO().update(frameworkOnOff);
    }

    public void delete(){
        FrameworkOnOff frameworkOnOff1 = new FrameworkOnOff();
        frameworkOnOff1.setStatus(true);
        db.frameworkOnOffDAO().delete(frameworkOnOff1);

        FrameworkOnOff frameworkOnOff2 = new FrameworkOnOff();
        frameworkOnOff2.setStatus(false);
        db.frameworkOnOffDAO().delete(frameworkOnOff2);
    }

    public int totalRecords(){
        return db.frameworkOnOffDAO().total();
    }
}
