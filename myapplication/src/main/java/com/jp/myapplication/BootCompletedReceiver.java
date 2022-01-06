package com.jp.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.digitalphenotyping.CompositionMode;
import br.ufma.lsdi.digitalphenotyping.dpmanager.DPManager;
import br.ufma.lsdi.digitalphenotyping.dpmanager.database.DatabaseManager;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidActivityException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidClientIDException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidCompositionModeException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidDataProcessorNameException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidFrequencyException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidHostServerException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidPasswordException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidPortException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidUsernameException;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessor;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedReceiver.class.getName();
    private DPManager dpManager;
    private Context con;
    private Activity activity;
    private DatabaseManager databaseManager;
    private Subscriber subAliveDPManagerService;

    SharedPreferences sharedpreferences;

    private final String MyPREFERENCES = "pref_main" ;
    private final String Host = "hostKey";
    private final String Port = "portKey";
    private final String Clientid = "clientidKey";
    private final String Compositionmode = "compositioModeKey";
    private final String Frequency = "frequencyKey";
    private static final String SecurityModule = "securitymoduleKey";

    private String host = "not set";
    private String port = "not set" ;
    private String clientid = "not set" ;
    private String compositionmode = "not set";
    private String frequency = null;
    private boolean securitymodule = false;

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        con = context;
        activity = (Activity) con;

        //if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) { }
        Log.i(TAG,"#### Ativando o framework");
        //SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, MainActivity2.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);

            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            if(sharedpreferences != null){
                host = sharedpreferences.getString(Host,"");
                port = sharedpreferences.getString(Port,"");
                clientid = sharedpreferences.getString(Clientid, "");
                compositionmode = sharedpreferences.getString(Compositionmode,"");
                frequency = sharedpreferences.getString(Frequency,"");
                securitymodule = sharedpreferences.getBoolean(SecurityModule, false);
            }

            initAPP();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    databaseManager = DatabaseManager.getInstance(con);
                    new AddItemTaskOn().execute();
                }
            }, 60000);

            /*Intent serviceIntent = new Intent(context, DPManager.class);
            context.startService(serviceIntent);*/
        }
    }

    public void initAPP(){
        try {
            startFramework();
        } catch (InvalidCompositionModeException e) {
            e.printStackTrace();
        } catch (InvalidPortException e) {
            e.printStackTrace();
        } catch (InvalidUsernameException e) {
            e.printStackTrace();
        } catch (InvalidHostServerException e) {
            e.printStackTrace();
        } catch (InvalidActivityException e) {
            e.printStackTrace();
        } catch (InvalidFrequencyException e) {
            e.printStackTrace();
        } catch (InvalidPasswordException e) {
            e.printStackTrace();
        } catch (InvalidClientIDException e) {
            e.printStackTrace();
        }
    }

    public void startFramework() throws InvalidCompositionModeException, InvalidPortException, InvalidUsernameException, InvalidHostServerException, InvalidActivityException, InvalidFrequencyException, InvalidPasswordException, InvalidClientIDException {
        if(compositionmode.equals("SEND_WHEN_IT_ARRIVES")) {
            dpManager = new DPManager.Builder(activity)
                    .setExternalServer(host, port, clientid)
                    .setSecurityModule(securitymodule)
                    //.setExternalServer("broker.hivemq.com","1883")
                    .setCompositionMode(CompositionMode.SEND_WHEN_IT_ARRIVES)
                    .build();
        }
        else if(compositionmode.equals("GROUP_ALL")){
            dpManager = new DPManager.Builder(activity)
                    .setExternalServer(host, port, clientid)
                    .setSecurityModule(securitymodule)
                    .setCompositionMode(CompositionMode.GROUP_ALL)
                    .build();
        }
        else if(compositionmode.equals("FREQUENCY")){
            int freq = Integer.parseInt(frequency);
            dpManager = new DPManager.Builder(activity)
                    .setExternalServer(host, port, clientid)
                    .setSecurityModule(securitymodule)
                    .setCompositionMode(CompositionMode.FREQUENCY)
                    .setFrequency(freq)
                    .build();
        }
        dpManager.getInstance().start();
    }

    public void processValue(List<ActiveDataProcessor> myValue) {
        if(myValue.size() > 0){
            List<String> list = new ArrayList();
            for(int i=0; i < myValue.size(); i++) {
                list.add(myValue.get(i).getDataProcessorName());
            }
            try {
                dpManager.getInstance().startDataProcessors(list);
            } catch (InvalidDataProcessorNameException e) {
                e.printStackTrace();
            }
        }
    }

    private class AddItemTaskOn extends AsyncTask<Void, Void, List<ActiveDataProcessor>> {
        @Override
        protected List<ActiveDataProcessor> doInBackground(Void... params) {
            List<ActiveDataProcessor> l = new ArrayList();
            l = databaseManager.getInstance().getDB().activeDataProcessorDAO().findByActiveDataProcessorAll();
            return l;
        }

        @Override
        protected void onPostExecute(List<ActiveDataProcessor> result) {
            processValue(result);
        }
    }
}