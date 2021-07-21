package com.jp.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import br.ufma.lsdi.digitalphenotyping.Bus;
import br.ufma.lsdi.digitalphenotyping.DigitalPhenotypingManager;

public class MainActivity extends AppCompatActivity {
    DigitalPhenotypingManager digitalPhenotyping;
    TextView textview_first;
    View button_first;
    View button_stop;
    View button_closeFramework;
    Bus myService;
    private static final String TAG = MainActivity.class.getName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_first);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textview_first = findViewById(R.id.textview_first);
        button_first = findViewById(R.id.button_first);
        button_stop = findViewById(R.id.stop);
        button_closeFramework = findViewById(R.id.closeFramework);

        button_first.setOnClickListener(clickListener);
        button_stop.setOnClickListener(clickListener);
        button_closeFramework.setOnClickListener(clickListener);

        startFramework();
    }

    @Override
    public void onStart() {
        super.onStart();

        try{
            Intent intent = new Intent(this, Bus.class);
            intent.putExtra("clientID","l");
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
            Bus.LocalBinder binder = (Bus.LocalBinder) iBinder;
            myService = binder.getService();

            digitalPhenotyping.getInstance().setBusSystem(myService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"#### Disconnection service busSystem");
        }
    };


    public void startFramework(){
        digitalPhenotyping = new DigitalPhenotypingManager(this,"l", 4, false);
        digitalPhenotyping.start();
    }


    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case  R.id.button_first: {
                    Log.i(TAG,"#### Publicando mensagem para start sensor");
                    //digitalPhenotyping.getInstance().publish(DigitalPhenotypingManager.ACTIVE_SENSOR,"TouchScreen");
                    digitalPhenotyping.getInstance().activaSensor("TouchScreen");
                    break;
                }

                case R.id.stop: {
                    Log.i(TAG, "#### Publicando mensagem para stop sensor");
                    //digitalPhenotyping.getInstance().publish(DigitalPhenotypingManager.DEACTIVATE_SENSOR, "TouchScreen");
                    digitalPhenotyping.getInstance().deactivateSensor("TouchScreen");
                    break;
                }

                case R.id.closeFramework: {
                    Log.i(TAG, "#### Parando o framework");
                    digitalPhenotyping.getInstance().stop();
                    finish();
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