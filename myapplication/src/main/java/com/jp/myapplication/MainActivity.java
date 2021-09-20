package com.jp.myapplication;

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
import br.ufma.lsdi.digitalphenotyping.DPManager;
import br.ufma.lsdi.digitalphenotyping.MainService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    DPManager digitalPhenotypingManager;
    TextView textview_first;
    View button_first;
    View button_stop;
    View button_closeFramework;
    View button_recorder;
    MainService myService;
    List<String> listProcessors = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        listProcessors.add("Sociability");
        listProcessors.add("Mobility");
        listProcessors.add("Sleep");

        startFramework();
    }


    public void startFramework(){
        digitalPhenotypingManager = new DPManager.Builder(this)
                .setExternalServer("broker.hivemq.com",1883)
                .setCompositionMode(CompositionMode.FREQUENCY)
                .setFrequency(15)
                .build();
        digitalPhenotypingManager.start();
    }


    @Override
    public void onStart() {
        super.onStart();

        try{
            Intent intent = new Intent(this, MainService.class);

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
            MainService.LocalBinder binder = (MainService.LocalBinder) iBinder;
            myService = binder.getService();

            digitalPhenotypingManager.getInstance().setMainService(myService);
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
                    digitalPhenotypingManager.getInstance().startDataProcessors(listProcessors);
                    break;
                }

                case R.id.stop: {
                    Log.i(TAG, "#### Stopping processors");
                    digitalPhenotypingManager.getInstance().stopDataProcessors(listProcessors);
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