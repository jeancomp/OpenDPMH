package com.jp.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.CompositionMode;
import br.ufma.lsdi.digitalphenotyping.SaveActivity;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dpmanager.DPManager;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidActivityException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidCompositionModeException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidDataProcessorNameException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidFrequencyException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidHostServerException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidMainServiceException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidPasswordException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidPortException;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidUsernameException;
import br.ufma.lsdi.digitalphenotyping.mainservice.MainService;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = MainActivity2.class.getName();
    private List<String> listProcessors = new ArrayList();
    private RecyclerViewAdapter adapter;
    private MainService myService;
    private DPManager dpManager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textLoad;
    private TextView txtStatus;
    private View button_init;
    private View fab;
    private Notification notification;
    private boolean flag_on_off = false;
    private boolean isInitialized = false;
    private Vibrator vibe;
    private Activity activity;
    private SaveActivity saveActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        activity = (Activity) this;
        saveActivity = new SaveActivity(this);
        recyclerView = findViewById(R.id.recyclerview_fragment_main_list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        textLoad = findViewById(R.id.error_msg);
        txtStatus = findViewById(R.id.txtStatus);
        progressBar = findViewById(R.id.progress_bar);
        button_init = findViewById(R.id.button_init);
        button_init.setOnClickListener(clickListener);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(clickListener);
        vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        //listProcessors.add("RawDataCollector");
        //listProcessors.add("Mobility");
        listProcessors.add("Sociability");
        listProcessors.add("Sleep");

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
        }
    }


    public void startFramework() throws InvalidCompositionModeException, InvalidPortException, InvalidUsernameException, InvalidHostServerException, InvalidActivityException, InvalidFrequencyException, InvalidPasswordException {
        dpManager = new DPManager.Builder(this)
                .setExternalServer("192.168.0.7", "1883")
                //.setExternalServer("broker.hivemq.com","1883")
                .setCompositionMode(CompositionMode.SEND_WHEN_IT_ARRIVES)
                //.setFrequency(15)
                .build();
        dpManager.getInstance().start();
    }


    @Override
    public void onStart() {
        super.onStart();

        try {
            Intent intent = new Intent(this, MainService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            } else {
                startService(intent);
            }

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
        } catch (Exception e) {
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "#### Connection service MainService");
            MainService.LocalBinder binder = (MainService.LocalBinder) iBinder;
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
                        /*ColorDrawable buttonColor = (ColorDrawable) button_init.getBackground();
                        int colorId = buttonColor.getColor();*/
                        if(!flag_on_off){
                            isInitialized = true;
                            button_init.setBackgroundResource(R.color.colorGreen);
                            if(notification == null) {
                                notification = new Notification();
                            }
                            dpManager.getInstance().startDataProcessors(listProcessors);
                            flag_on_off = true;
                            txtStatus.setText("on");
                            vibe.vibrate(50);
                        }
                        else {
                            button_init.setBackgroundResource(R.color.colorWhite);
                            flag_on_off = false;
                            txtStatus.setText("off");
                            dpManager.getInstance().stop();
                            vibe.vibrate(50);
                        }
                    } catch (InvalidDataProcessorNameException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case R.id.fab: {
                    try {
                        startAddDataProcessorFragment();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };


    public void startAddDataProcessorFragment(){
        Log.i(TAG,"#### Ativando ADD");
        Intent i = new Intent(this, AddActiveDataProcessorActivity.class);
        startActivity(i);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void notificationAdapter(){
        adapter = new RecyclerViewAdapter(this, dpManager.getInstance().getActiveDataProcessorsList());
        recyclerView.setAdapter(adapter);
    }


    public void notificationDataProcessor(){
        //Carrega no RecyclerView os DataProcessor ativos
        //adapter.notifyDataSetChanged();
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
            startActivity(i);
            return true;
        }
        else if(id == R.id.action_close){
            dpManager.getInstance().stop();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    public class Notification {
        Subscriber subNotification;

        public Notification(){
            subNotification = SubscriberFactory.createSubscriber();
            subNotification.addConnection(CDDL.getInstance().getConnection());
            subscribeMessageNotification(Topics.NOTIFICATION.toString());
        }

        public void subscribeMessageNotification(String serviceName) {
            subNotification.subscribeServiceByName(serviceName);
            subNotification.setSubscriberListener(subscriberNotificationListener);
            subNotification.subscribeTopic(Topics.NOTIFICATION.toString());
        }

        public final ISubscriberListener subscriberNotificationListener = new ISubscriberListener() {
            @Override
            public void onMessageArrived(br.ufma.lsdi.cddl.message.Message message) {
                Log.i(TAG, "#### Read messages (Notification):  " + message);

                Object[] valor = message.getServiceValue();
                String mensagemRecebida = StringUtils.join(valor, ", ");
                String[] separated = mensagemRecebida.split(",");
                String atividade = String.valueOf(separated[0]);

                if (atividade.equals("aliveMainService")) {
                    //notificationAdapter();
                }
                else if (atividade.equals("aliveNewDataProcessor") || atividade.equals("aliveRemoveDataProcessor")) {
                    notificationDataProcessor();
                }
            }
        };
    }
}

//Backup
/*//Start DataProcessor
        try {
            dpManager.getInstance().startDataProcessors(listProcessors);
        } catch (InvalidDataProcessorNameException e) {
            e.printStackTrace();
        }*/

        /*//Stop DataProcessor
        try {
            dpManager.getInstance().stopDataProcessors(listProcessors);
        } catch (InvalidDataProcessorNameException e) { e.printStackTrace(); }*/

        /*//Close framework
        dpManager.getInstance().stop();
        finish();*/

        /*//Start função recorder
        Intent startRecorder = new Intent(getApplicationContext(), MainActivityRecorder.class);
        startActivity(startRecorder);*/