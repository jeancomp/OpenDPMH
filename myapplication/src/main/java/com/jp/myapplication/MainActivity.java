package com.jp.myapplication;

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

import br.ufma.lsdi.digitalphenotyping.BusSystem;
import br.ufma.lsdi.digitalphenotyping.DigitalPhenotypingManager;

public class MainActivity extends AppCompatActivity {
    DigitalPhenotypingManager digitalPhenotyping;
    TextView textview_first;
    View button_first;
    View button_stop;
    BusSystem myService;
    private static final String TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_first);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textview_first = findViewById(R.id.textview_first);
        button_first = findViewById(R.id.button_first);
        button_stop = findViewById(R.id.stop);

        button_first.setOnClickListener(clickListener);
        button_stop.setOnClickListener(clickListener);

        startFramework();
    }

    @Override
    protected void onStart() {
        super.onStart();

        try{
            Intent intent = new Intent(this, BusSystem.class);
            intent.putExtra("clientID","l");
            intent.putExtra("communicationTechnology",4);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.i(TAG,"#### 111111111.");
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
            else {
                //getActivity().startService(intent);
                Log.i(TAG,"#### 222222222.");
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.getMessage());
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        //unbindService(serviceConnection);
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG,"#### Connection service busSystem");
            BusSystem.LocalBinder binder = (BusSystem.LocalBinder) iBinder;
            myService = binder.getService();

            digitalPhenotyping.getInstance().setBusSystem(myService);
            //binder = (BusSystem.LocalBinder)iBinder;
            //binder.getService().publisher();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG,"#### Disconnection service busSystem");
        }
    };


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case  R.id.button_first: {
                    Log.i(TAG,"#### Publicando mensagem para start sensor");
                    digitalPhenotyping.getInstance().getBusSystem().publishMessage(DigitalPhenotypingManager.ACTIVE_SENSOR,"TouchScreen");
                    //digitalPhenotyping.publishMessage(DigitalPhenotypingManager.ACTIVE_SENSOR,"Goldfish 3-axis Accelerometer");
                    //digitalPhenotyping.publishMessage(DigitalPhenotypingManager.ACTIVE_SENSOR,"SMS");
                    break;
                }

                case R.id.stop: {
                    Log.i(TAG, "#### Publicando mensagem para stop sensor");
                    digitalPhenotyping.getInstance().getBusSystem().publishMessage(DigitalPhenotypingManager.DEACTIVATE_SENSOR, "TouchScreen");
                    break;
                }

                case R.id.button_second: {
                    // do something for button 2 click
                    break;
                }

            //.... etc
            }
        }
    };


    public void startFramework(){
        digitalPhenotyping = new DigitalPhenotypingManager(this, this,"l", 4, false);
        digitalPhenotyping.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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