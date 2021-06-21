package br.ufma.lsdi.digitalphenotyping;

import android.app.Application;

import br.ufma.lsdi.cddl.CDDL;

public class DPApplication extends Application {
    private static CDDL cddl;
    private static DPApplication instance = null;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public static DPApplication getInstance() {
        if (instance == null) {
            instance = new DPApplication();
        }
        return instance;
    }


    public static CDDL CDDLGetInstance(){
        if(cddl == null){
            cddl = CDDL.getInstance();
        }
        return cddl;
    }
}
