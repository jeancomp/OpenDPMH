package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.content.Context;

public class SaveActivity {
    private static SaveActivity instance = null;
    public static Activity activity;
    public Context context;

    public SaveActivity(Activity activity){
        this.activity = activity;
        context = activity;
    }

    public static SaveActivity getInstance() {
        if (instance == null) {
            instance = new SaveActivity(activity);
        }
        return instance;
    }

    public Activity getActivity(){
        return this.activity;
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }
}
