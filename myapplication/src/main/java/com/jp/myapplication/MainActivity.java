package com.jp.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.digitalphenotyping.CompositionMode;
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
import br.ufma.lsdi.digitalphenotyping.dpmanager.DPManagerService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    DPManager digitalPhenotypingManager;
    TextView textview_first;
    View button_first;
    View button_stop;
    View button_closeFramework;
    View button_recorder;
    DPManagerService myService;
    List<String> listProcessors = null;
    Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.fragment_first);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textview_first = findViewById(R.id.textview_first);
        button_first = findViewById(R.id.button_first);
        button_stop = findViewById(R.id.stop2);
        button_closeFramework = findViewById(R.id.closeFramework);
        button_recorder = findViewById(R.id.recorder);

        button_first.setOnClickListener(clickListener);
        button_stop.setOnClickListener(clickListener);
        button_closeFramework.setOnClickListener(clickListener);
        button_recorder.setOnClickListener(clickListener);

        this.listProcessors = new ArrayList();
        //listProcessors.add("RawDataCollector");
        //listProcessors.add("Sociability");
        //listProcessors.add("Mobility");
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
        digitalPhenotypingManager = new DPManager.Builder(this)
                .setExternalServer("192.168.0.7","1883")
                //.setExternalServer("broker.hivemq.com","1883")
                .setCompositionMode(CompositionMode.SEND_WHEN_IT_ARRIVES)
                //.setFrequency(15)
                .build();
        digitalPhenotypingManager.getInstance().start();
    }


    @Override
    public void onStart() {
        super.onStart();

        try{
            Intent intent = new Intent(this, DPManagerService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
            else {
                startService(intent);
            }
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        //unbindService(serviceConnection);
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG,"#### Connection service MainService");
            DPManagerService.LocalBinder binder = (DPManagerService.LocalBinder) iBinder;
            myService = binder.getService();

            try {
                digitalPhenotypingManager.getInstance().setMainService(myService);
            } catch (InvalidMainServiceException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"#### Disconnection service MainService");
        }
    };


    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case  R.id.button_first: {
                    Log.i(TAG,"#### Starting processors");
                    try {
                        digitalPhenotypingManager.getInstance().startDataProcessors(listProcessors);
                    } catch (InvalidDataProcessorNameException e) { e.printStackTrace(); }
                    break;
                }

                case R.id.stop: {
                    Log.i(TAG, "#### Stopping processors");
                    try {
                        digitalPhenotypingManager.getInstance().stopDataProcessors(listProcessors);
                    } catch (InvalidDataProcessorNameException e) { e.printStackTrace(); }
                    break;
                }

                case R.id.closeFramework: {
                    Log.i(TAG, "#### Stop Framework");
                    digitalPhenotypingManager.getInstance().stop();
                    finish();
                    break;
                }
                case R.id.recorder: {
                    Log.i(TAG, "#### Função recorder");
                    Intent startRecorder = new Intent(getApplicationContext(), MainActivityRecorder.class);
                    startActivity(startRecorder);
                    break;
                }

            //.... etc
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}