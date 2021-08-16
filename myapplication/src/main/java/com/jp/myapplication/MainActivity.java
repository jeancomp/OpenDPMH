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
import java.util.Collections;
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

        startFramework();
    }


    public void startFramework(){
        digitalPhenotypingManager = new DPManager();
        digitalPhenotypingManager.start(this, "", 8000, "", "", "", 1);
    }


    @Override
    public void onStart() {
        super.onStart();

        try{
            Intent intent = new Intent(this, MainService.class);
            //intent.putExtra("clientID","l");
            intent.putExtra("communicationTechnology",4);

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
            Log.i(TAG,"#### Connection service busSystem");
            MainService.LocalBinder binder = (MainService.LocalBinder) iBinder;
            myService = binder.getService();

            digitalPhenotypingManager.getInstance().setBusSystem(myService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"#### Disconnection service busSystem");
        }
    };


    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case  R.id.button_first: {
                    //Log.i(TAG,"#### Publicando mensagem para start sensor");
                    //digitalPhenotyping.getInstance().publishInference(DigitalPhenotypingManager.ACTIVE_SENSOR,"TouchScreen");
                    //digitalPhenotyping.getInstance().activaSensor("TouchScreen");

                    //Log.i(TAG,"#### Publicando mensagem para start sensor");
                    //digitalPhenotyping.getInstance().activaSensor("Call");

                    Log.i(TAG,"#### Publicando mensagem para start processor: Sociability");
                    digitalPhenotypingManager.getInstance().startDataProcessors(Collections.singletonList("Sociability"));
                    break;
                }

                case R.id.stop: {
                    //Log.i(TAG, "#### Publicando mensagem para stop sensor");
                    //digitalPhenotyping.getInstance().publishInference(DigitalPhenotypingManager.DEACTIVATE_SENSOR, "TouchScreen");
                    //digitalPhenotyping.getInstance().deactivateSensor("TouchScreen");

                    Log.i(TAG, "#### Publicando mensagem para stop processor: Sociability");
                    digitalPhenotypingManager.getInstance().stopDataProcessors(Collections.singletonList("Sociability"));
                    break;
                }

                case R.id.closeFramework: {
                    Log.i(TAG, "#### Parando o framework");
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
        //digitalPhenotyping.getInstance().stop();
        super.onDestroy();
    }
}