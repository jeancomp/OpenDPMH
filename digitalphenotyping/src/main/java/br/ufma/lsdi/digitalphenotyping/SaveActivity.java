package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;

public class SaveActivity {
    private static SaveActivity instance = null;
    public static Activity activity;

    public SaveActivity(Activity activity){
        this.activity = activity;
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
