package com.jp.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import br.ufma.lsdi.digitalphenotyping.DigitalPhenotypingManager;

public class MainActivity extends AppCompatActivity {
    DigitalPhenotypingManager digitalPhenotyping;
    TextView textview_first;
    View button_first;
    View button_stop;
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

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//            }
//        });


        startFramework();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case  R.id.button_first: {
                    Log.i(TAG,"#### Publicando mensagem para start sensor");
                    digitalPhenotyping.publishMessage(DigitalPhenotypingManager.ACTIVE_SENSOR,"TouchScreen");
                    //digitalPhenotyping.publishMessage(DigitalPhenotypingManager.ACTIVE_SENSOR,"Goldfish 3-axis Accelerometer");
                    //digitalPhenotyping.publishMessage(DigitalPhenotypingManager.ACTIVE_SENSOR,"SMS");
                    break;
                }

                case R.id.stop: {
                    Log.i(TAG, "#### Publicando mensagem para stop sensor");
                    digitalPhenotyping.publishMessage(DigitalPhenotypingManager.DEACTIVATE_SENSOR, "TouchScreen");
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
        Log.i(TAG,"#### INICIANDO FRAMEWORK");
        digitalPhenotyping = new DigitalPhenotypingManager(this, this,"lcmuniz@gmail.com", 4, false);
        digitalPhenotyping.start();
        //textview_first.setText(digitalPhenotyping.getStatusCon());
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
}