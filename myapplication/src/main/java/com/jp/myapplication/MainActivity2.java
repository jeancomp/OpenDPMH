package com.jp.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.CompositionMode;
import br.ufma.lsdi.digitalphenotyping.SaveActivity;
import br.ufma.lsdi.digitalphenotyping.dpmanager.DPManager;
import br.ufma.lsdi.digitalphenotyping.dpmanager.DPManagerService;
import br.ufma.lsdi.digitalphenotyping.dpmanager.database.DatabaseManager;
import br.ufma.lsdi.digitalphenotyping.dpmanager.database.FrameworkOnOff;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidActivityException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidClientIDException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidCompositionModeException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidDataProcessorNameException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidFrequencyException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidHostServerException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidMainServiceException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidPasswordException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidPortException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidUsernameException;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessor;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = MainActivity2.class.getName();
    private DatabaseManager databaseManager;
    private RecyclerViewAdapter adapter;
    private DPManagerService myService;
    private DPManager dpManager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private TextView txtClientID;
    private TextView txtStatus;
    private TextView txtBatteryStatus;

    private View button_init;
    private View fab;

    private boolean flag_on_off = false;
    private Vibrator vibe;
    private Activity activity;
    private SaveActivity saveActivity;

    private SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "pref_main" ;
    private static final String Host = "hostKey";
    private static final String Port = "portKey";
    private static final String Clientid = "clientidKey";
    private static final String Compositionmode = "compositioModeKey";
    private static final String Frequency = "frequencyKey";
    private static final String SecurityModule = "securitymoduleKey";

    private String host = "not set";
    private String port = "not set" ;
    private String clientid = "not set" ;
    private String compositionmode = "not set";
    private String frequency = null;
    private boolean securitymodule = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        activity = (Activity) this;
        databaseManager = DatabaseManager.getInstance(this);
        saveActivity = new SaveActivity(this);
        recyclerView = findViewById(R.id.recyclerview_fragment_main_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        txtClientID = findViewById(R.id.txtClientID_value);
        txtStatus = findViewById(R.id.txtStatus);
        txtBatteryStatus = findViewById(R.id.textview_total_value_change_24h);

        progressBar = findViewById(R.id.progress_bar);
        button_init = findViewById(R.id.button_init);
        button_init.setOnClickListener(clickListener);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(clickListener);
        vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        try {
            new AddItemTaskGet().execute(); // verifica status do framework on/off
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            /*if (databaseManager.getInstance().getDB() != null && databaseManager.getInstance().getDB().isOpen()){
                databaseManager.getInstance().getDB().close();
            }*/
        }

        sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (!Settings.canDrawOverlays(getApplicationContext())) {
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);

            myIntent.setData(uri);
            startActivityForResult(myIntent, 1);
            return;
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
            dpManager = new DPManager.Builder(this)
                    .setExternalServer(host, port, clientid)
                    .setSecurityModule(securitymodule)
                    //.setExternalServer("broker.hivemq.com","1883")
                    .setCompositionMode(CompositionMode.SEND_WHEN_IT_ARRIVES)
                    .build();
        }
        else if(compositionmode.equals("GROUP_ALL")){
            dpManager = new DPManager.Builder(this)
                    .setExternalServer(host, port, clientid)
                    .setSecurityModule(securitymodule)
                    .setCompositionMode(CompositionMode.GROUP_ALL)
                    .build();
        }
        else if(compositionmode.equals("FREQUENCY")){
            int freq = Integer.parseInt(frequency);
            dpManager = new DPManager.Builder(this)
                    .setExternalServer(host, port, clientid)
                    .setSecurityModule(securitymodule)
                    .setCompositionMode(CompositionMode.FREQUENCY)
                    .setFrequency(freq)
                    .build();
        }
        dpManager.getInstance().start();
        try {
            Intent intent = new Intent(this, DPManagerService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            } else {
                startService(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getBaseContext().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float)scale;
        txtBatteryStatus.setText(String.valueOf(batteryPct) + " %");

        if(sharedpreferences != null){
            host = sharedpreferences.getString(Host,"");
            port = sharedpreferences.getString(Port,"");
            clientid = sharedpreferences.getString(Clientid, "");
            compositionmode = sharedpreferences.getString(Compositionmode,"");
            frequency = sharedpreferences.getString(Frequency,"");
            securitymodule = sharedpreferences.getBoolean(SecurityModule, false);
        }
        if(!clientid.isEmpty()){
            txtClientID.setText(clientid);
        }

        try {
            if(flag_on_off) {
                Intent intent = new Intent(this, DPManagerService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                } else {
                    startService(intent);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        /*if(serviceConnection != null) {
            getBaseContext().unbindService(serviceConnection);
        }*/
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "#### Connection service MainService");
            DPManagerService.LocalBinder binder = (DPManagerService.LocalBinder) iBinder;
            myService = binder.getService();

            try {
                dpManager.getInstance().setMainService(myService);
            } catch (InvalidMainServiceException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "#### Disconnection service MainService");
        }
    };


    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_init: {
                    try {
                        if (!flag_on_off) {
                            vibe.vibrate(50);
                            if((host.isEmpty()) || (port.isEmpty() || clientid.isEmpty() || compositionmode.isEmpty())) {
                                Toast.makeText(getBaseContext(), "Required to configure host, port, clientid and composition mode!",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                initAPP();
                                processValue(true);
                                new AddItemTaskSet().execute(true);
                            }
                        }
                        else {
                            vibe.vibrate(50);
                            questionClose();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case R.id.fab: {
                    try {
                        if(flag_on_off){
                            startAddDataProcessorFragment();
                        }
                        else {
                            Toast.makeText(getBaseContext(), "Click start application button",Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };


    public void questionClose(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing OpenDPMH")
                .setMessage("Are you sure you want to close this app? It will stop the data collection.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            processValue(false);
                            new AddItemTaskSet().execute(false);
                            new AddItemTaskOn().execute();  // limpa o banco com os nomes dos DataProcessor ativos, ficando vazio.
                            txtClientID.setText("--");
                            new AddItemTaskClearStatusFramework().execute();
                            new AddItemTaskClearListDataProcessor().execute();

                            dpManager.getInstance().stop();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        finally {
                            /*if (databaseManager.getInstance().getDB() != null && databaseManager.getInstance().getDB().isOpen()){
                                databaseManager.getInstance().getDB().close();
                            }*/
                        }
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


    public void startAddDataProcessorFragment(){
        Intent i = new Intent(this, AddActiveDataProcessorActivity.class);
        startActivity(i);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        /*if(serviceConnection != null) {
            getBaseContext().unbindService(serviceConnection);
        }*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            i.putExtra("flag_on_off", flag_on_off);
            startActivity(i);
            return true;
        }
        /*else if(id == R.id.action_close){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Closing OpenDPMH")
                    .setMessage("Are you sure you want to close this app? It will stop the data collection.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            processValue(false);
                            new AddItemTaskSet().execute(false);
                            new AddItemTaskOn().execute();
                            new AddItemTaskClear().execute();
                            dpManager.getInstance().stop();
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }*/
        /*else if(id == R.id.action_audio){
            //Start função recorder
            Intent startRecorder = new Intent(getApplicationContext(), MainActivityRecorder.class);
            startActivity(startRecorder);
        }*/

        return super.onOptionsItemSelected(item);
    }


    public void processValue(Boolean myValue) {
        Log.i(TAG,"#### myValue: " + myValue);
        if(!myValue){
            button_init.setBackgroundResource(R.color.colorWhite);
            txtStatus.setText("off");
        }
        else if(myValue){
            button_init.setBackgroundResource(R.color.colorGreen);
            txtStatus.setText("on");
        }
        flag_on_off = myValue;
    }


    private class AddItemTaskGet extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            FrameworkOnOff frameworkOnOff = new FrameworkOnOff();
            frameworkOnOff = databaseManager.getInstance().getDB().frameworkOnOffDAO().findByFrameworkStatus();
            int tam = databaseManager.getInstance().getDB().frameworkOnOffDAO().total();
            Log.i(TAG,"#### Registro: " + tam);
            Boolean value = true;
            if(tam == 0){
                value = false;
            }
            return value;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            processValue(result);
        }
    }


    private class AddItemTaskClearStatusFramework extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // Limpa a tabela com status do framework: on/off
            FrameworkOnOff frameworkOnOff1 = new FrameworkOnOff();
            frameworkOnOff1.setStatus(true);
            databaseManager.getInstance().getDB().frameworkOnOffDAO().delete();

            // Limpa tabela com a lista de processadores disponíveis
            //databaseManager.getInstance().getInstance().getDB().listDataProcessorDAO().delete();
            return null;
        }
    }


    private class AddItemTaskClearListDataProcessor extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // Limpa tabela com a lista de processadores disponíveis
            databaseManager.getInstance().getInstance().getDB().listDataProcessorDAO().delete();
            return null;
        }
    }


    private class AddItemTaskSet extends AsyncTask<Boolean, Void, Void> {
        @Override
        protected Void doInBackground(Boolean... params) {
            FrameworkOnOff frameworkOnOff = new FrameworkOnOff();
            frameworkOnOff.setStatus(params[0]);
            databaseManager.getInstance().getDB().frameworkOnOffDAO().update(frameworkOnOff);
            return null;
        }
    }


    public void processValue(List<ActiveDataProcessor> myValue) {
        if(myValue.size() > 0){
            List<String> list = new ArrayList();
            for(int i=0; i < myValue.size(); i++) {
                list.add(myValue.get(i).getDataProcessorName());
            }
            try {
                dpManager.getInstance().stopDataProcessors(list);
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

            for(int i=0; i < l.size(); i++){
                databaseManager.getInstance().getDB().activeDataProcessorDAO().delete(l.get(i));
            }
            return l;
        }

        @Override
        protected void onPostExecute(List<ActiveDataProcessor> result) {
            processValue(result);
        }
    }
}
//--Backup-----------------------------------------
            /*final int tempoDeEspera = 1000;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(tempoDeEspera);
                    Log.i(TAG,"#### Aguardando1");
                    SystemClock.sleep(tempoDeEspera);
                    Log.i(TAG,"#### Aguardando2");
                    SystemClock.sleep(tempoDeEspera);
                    Log.i(TAG,"#### Aguardando3");
                    SystemClock.sleep(tempoDeEspera);
                    Log.i(TAG,"#### Aguardando4");
                    SystemClock.sleep(tempoDeEspera);
                    progressBar.setVisibility(View.INVISIBLE);
                    textLoad.setVisibility(View.INVISIBLE);
                }
            }).start();*/