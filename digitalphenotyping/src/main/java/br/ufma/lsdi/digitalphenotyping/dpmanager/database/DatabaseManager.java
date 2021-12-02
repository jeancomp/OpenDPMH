package br.ufma.lsdi.digitalphenotyping.dpmanager.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseManager {
    private static final String TAG = DatabaseManager.class.getName();
    private static DatabaseManager instance = null;
    private String DATABASE_NAME = "database-framework";
    private static Context context;
    private AppDatabase db;

    public DatabaseManager(Context context){
        this.context = context;
        db = Room.databaseBuilder(context,
                AppDatabase.class, DATABASE_NAME).build();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    public static DatabaseManager getInstance(Context cont) {
        if (instance == null) {
            instance = new DatabaseManager(cont);
        }
        return instance;
    }

    public boolean checkDatabaseCreation(){
        final String DB_PATH = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        if(DB_PATH == null){
            return false;
        }
        return true;
    }

    public AppDatabase getDB(){
        return db;
    }
}
