package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class Configurations extends Application{
    private static Configurations instance = null;
    private Context context;
    private Activity activity;


    public Configurations(){ }


    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        this.context = this;
    }


    public static Configurations getInstance() {
        if (instance == null) {
            instance = new Configurations();
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
