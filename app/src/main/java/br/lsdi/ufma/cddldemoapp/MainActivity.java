// Possiveis nomes para o framework fenotipagem digital na saude:

// SERELEPE, QUATIPURU: trata-se de um esquilo selvagem
// MUTUM: ave maranhense extinsão

package br.lsdi.ufma.cddldemoapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.Connection;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;


public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private List<String> spinnerSensors;
    private ArrayAdapter<String> spinnerAdapter;

    private ListView listView;
    private List<String> listViewMessages;
    private ListViewAdapter listViewAdapter;
    private EditText filterEditText;

    private CDDL cddl;
    private String email = "jean.marques@lsdi.ufma.br";    // Observacao importante para topico definido: mhub/+/service_topic/my-service
    private List<String> sensorNames = new ArrayList<String>();  ;
    private String currentSensor;
    private Subscriber subscriber;

    private boolean filtering;

    private Handler handler = new Handler();
    private AppBarConfiguration mAppBarConfiguration;

    EventBus eb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eb = EventBus.builder().build();
        eb.register(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_sociability, R.id.nav_fenotipagem, R.id.nav_chat, R.id.nav_configuracao, R.id.nav_sair)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


/*
        setPermissions();

        if (savedInstanceState == null) {
            configCDDL();
        }

        configSpinner();
        configListView();
        configStartButton();
        configStopButton();
        configClearButton();
        configFilterButton();
*/



    }

    private void configCDDL() {

        String host = CDDL.startMicroBroker();
        //String host = "broker.hivemq.com";

        Connection connection = ConnectionFactory.createConnection();
        connection.setHost(host);
        connection.setClientId(email);
        connection.connect();

        cddl = CDDL.getInstance();
        cddl.setConnection(connection);
        cddl.setContext(this);

        cddl.startService();
        cddl.startCommunicationTechnology(CDDL.INTERNAL_TECHNOLOGY_ID);
        //cddl.startLocationSensor();

        subscriber = SubscriberFactory.createSubscriber();
        subscriber.addConnection(cddl.getConnection());
        subscriber.setSubscriberListener(this::onMessage);

    }

    private void onMessage(Message message) {

        System.out.println("+++++++++++++++++++++");
        System.out.println(message);

        handler.post(() -> {
            Object[] valor = message.getServiceValue();
            listViewMessages.add(0, StringUtils.join(valor, ", "));
            listViewAdapter.notifyDataSetChanged();
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void on(MessageEvent event) {
    }

    @Override
    protected void onDestroy() {
        eb.unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //AppMenu appMenu = AppMenu.getInstance();
        //appMenu.setMenu(menu);

        getMenuInflater().inflate(R.menu.master, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AppMenu appMenu = AppMenu.getInstance();
        appMenu.setMenuItem(MainActivity.this, item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void configSpinner() {
        List<Sensor> sensors = cddl.getInternalSensorList();

        //sensorNames = sensors.stream().map(Sensor::getName).collect(Collectors.toList());

        for (Sensor sensor: cddl.getInternalSensorList()) {
            sensorNames.add(sensor.getName());
            //List<String> str = Collections.singletonList(sensor.getName());
            System.out.println(sensorNames);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensorNames);
        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

    }

    private void configListView() {
        listView = findViewById(R.id.listview);
        listViewMessages = new ArrayList<>();
        listViewAdapter = new ListViewAdapter(this, listViewMessages);
        listView.setAdapter(listViewAdapter);
    }

    private void configStartButton() {

        Button button = findViewById(R.id.start_button);
        button.setOnClickListener(e -> {
            stopCurrentSensor();
            startSelectedSensor();
        });

    }

    private void startSelectedSensor() {

        String selectedSensor = spinner.getSelectedItem().toString();
        //cddl.setFilter("select * from Message where cast(serviceValue[0], int) > 50");
        cddl.startSensor(selectedSensor);
        subscriber.subscribeServiceByName(selectedSensor);
        currentSensor = selectedSensor;

    }

    private void stopCurrentSensor() {
        if (currentSensor != null) {
            cddl.stopSensor(currentSensor);
        }
    }

    private void configStopButton() {
        Button button = findViewById(R.id.stop_button);
        button.setOnClickListener(e -> stopCurrentSensor());
    }

    private void configClearButton() {
        final Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(e -> {
            listViewMessages.clear();
            listViewAdapter.notifyDataSetChanged();
        });
    }

    private void configFilterButton() {

        filterEditText = findViewById(R.id.filter_edittext);

        Button button = findViewById(R.id.filter_button);
        button.setOnClickListener(e -> {
            if (filterEditText.getText().toString().equals(""))
                return;

            if (filtering) {
                subscriber.clearFilter();
                button.setText(R.string.filter_button_label);
            }
            else {
                subscriber.setFilter(filterEditText.getText().toString());
                button.setText(R.string.clear_filter_button_label);
            }
            filtering = !filtering;

        });

    }

    private void setPermissions() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }
}
