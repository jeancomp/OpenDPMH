package br.lsdi.ufma.appviews.ui.home;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import br.lsdi.ufma.appviews.AppMenu;
import br.lsdi.ufma.appviews.ListViewAdapter;
import br.lsdi.ufma.appviews.MessageEvent;
import br.lsdi.ufma.appviews.PubSubActivity;
import br.lsdi.ufma.appviews.R;
import br.lsdi.ufma.appviews.ui.fenotipagem.FenotipagemFragment;
import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.Connection;
import br.ufma.lsdi.cddl.ConnectionFactory;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;

public class HomeFragment extends Fragment {

    public HomeViewModel homeViewModel;

    public Spinner spinner;
    public List<String> spinnerSensors;
    public ArrayAdapter<String> spinnerAdapter;

    public ListView listView;
    public List<String> listViewMessages;
    public ListViewAdapter listViewAdapter;
    public EditText filterEditText;

    public CDDL cddl;
    public String email = "jean.marques@lsdi.ufma.br";    // Observacao importante para topico definido: mhub/+/service_topic/my-service
    public List<String> sensorNames = new ArrayList<String>();  ;
    public String currentSensor;
    public Subscriber subscriber;

    public boolean filtering;

    public Handler handler = new Handler();

    EventBus eb;

    View root;

    Button btAudio;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        //final TextView textView = root.findViewById(R.id.text_home);
        //homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            //@Override
            //public void onChanged(@Nullable String s) {
                //textView.setText(s);
            //}
        //});

        btAudio = (Button) root.findViewById(R.id.btAudio);
        btAudio.setOnClickListener(new View.OnClickListener() {
            private Context currentObj = getActivity();
            @Override
            public void onClick(View view) {
                PubSubActivity pub = new PubSubActivity();
                FenotipagemFragment fenotipagemFragment = new FenotipagemFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, fenotipagemFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        eb = EventBus.builder().build();
        eb.register(this);

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

        return root;
    }

    public void configCDDL() {

        String host = CDDL.startMicroBroker();
        //String host = "broker.hivemq.com";

        Connection connection = ConnectionFactory.createConnection();
        connection.setHost(host);
        connection.setClientId(email);
        connection.connect();

        cddl = CDDL.getInstance();
        cddl.setConnection(connection);
        cddl.setContext(getActivity());

        cddl.startService();
        cddl.startCommunicationTechnology(CDDL.INTERNAL_TECHNOLOGY_ID);
        //cddl.startLocationSensor();

        subscriber = SubscriberFactory.createSubscriber();
        subscriber.addConnection(cddl.getConnection());
        subscriber.setSubscriberListener(this::onMessage);

    }

    public void onMessage(Message message) {

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
    public void onDestroy() {
        eb.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        AppMenu appMenu = AppMenu.getInstance();
        appMenu.setMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AppMenu appMenu = AppMenu.getInstance();
        appMenu.setMenuItem(getActivity(), item);
        return super.onOptionsItemSelected(item);
    }

    public void configSpinner() {
        List<Sensor> sensors = cddl.getInternalSensorList();

        //sensorNames = sensors.stream().map(Sensor::getName).collect(Collectors.toList());

        for (Sensor sensor: cddl.getInternalSensorList()) {
            sensorNames.add(sensor.getName());
            //List<String> str = Collections.singletonList(sensor.getName());
            System.out.println(sensorNames);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, sensorNames);
        spinner = root.findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

    }

    public void configListView() {
        listView = root.findViewById(R.id.listview);
        listViewMessages = new ArrayList<>();
        listViewAdapter = new ListViewAdapter(getActivity(), listViewMessages);
        listView.setAdapter(listViewAdapter);
    }

    public void configStartButton() {
        Button button = root.findViewById(R.id.start_button);
        button.setOnClickListener(e -> {
            stopCurrentSensor();
            startSelectedSensor();
        });
    }

    public void startSelectedSensor() {
        String selectedSensor = spinner.getSelectedItem().toString();
        //cddl.setFilter("select * from Message where cast(serviceValue[0], int) > 50");
        cddl.startSensor(selectedSensor);
        subscriber.subscribeServiceByName(selectedSensor);
        currentSensor = selectedSensor;
    }

    public void stopCurrentSensor() {
        if (currentSensor != null) {
            cddl.stopSensor(currentSensor);
        }
    }

    public void configStopButton() {
        Button button = root.findViewById(R.id.stop_button);
        button.setOnClickListener(e -> stopCurrentSensor());
    }

    public void configClearButton() {
        final Button clearButton = root.findViewById(R.id.clear_button);
        clearButton.setOnClickListener(e -> {
            listViewMessages.clear();
            listViewAdapter.notifyDataSetChanged();
        });
    }

    public void configFilterButton() {

        filterEditText = root.findViewById(R.id.filter_edittext);

        Button button = root.findViewById(R.id.filter_button);
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

    public void setPermissions() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }

    public void proximaTela(View view){
        //Intent intent = new Intent(getActivity(), PubSubActivity.class);
        //startActivity(intent);
    }
}