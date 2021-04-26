package br.ufma.lsdi.digitalphenotyping.datacontroller;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import java.util.HashSet;
import java.util.Iterator;

public class DataControllerManager extends Application {
    private HashSet<Privacy> blockedDataContext = new HashSet<Privacy>();
    private Activity activity = (Activity) getApplicationContext();
    private static DataControllerManager instance = null;
    private static final String TAG = DataControllerManager.class.getName();

    public DataControllerManager(){
        Log.d(TAG, "DataControllerManager() called");
    }


    public static DataControllerManager getInstance() {
        if (instance == null) {
            instance = new DataControllerManager();
        }
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d(TAG, "onCreate() called");
    }


    public class Privacy{
        String nameDataContext;
        Double startTime;
        Double endTime;


        public Privacy(String nameDataContext, Double startTime, Double endTime){
            this.nameDataContext = nameDataContext;
            this.startTime = startTime;
            this.endTime = endTime;
        }


        public String getNameDataContext(){
            return nameDataContext;
        }


        public Double getStartTime(){
            return startTime;
        }


        public Double getEndTime(){
            return endTime;
        }
    }


    public Boolean isBlockedDataContext(String nameDataContext){
        Iterator<Privacy> iterator = blockedDataContext.iterator();
        while (iterator.hasNext()) {
            if(nameDataContext.equalsIgnoreCase(iterator.next().nameDataContext)) {
                Log.i(TAG,"Blocked Phenotype Composer: " + iterator.next().nameDataContext);
                return true;
            }
        }
        return false;
    }


    public void addBlockedDataContext(String nameDataContext, Double start, Double end){
        blockedDataContext.add(new Privacy(nameDataContext, start, end));
    }
}
