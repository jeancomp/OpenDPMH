package br.ufma.lsdi.digitalphenotyping.dataprovider.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.DPApplication;

public class ContextDataProvider extends Service {
    private static final String TAG = ContextDataProvider.class.getName();
    private String statusCon = "undefined";
    Subscriber sub;
    Subscriber subDeactive;
    private Context context;
    String clientID = "l";
    int communicationTechnology = 4;
    List<String> sensorOn = null;
    DPApplication dpApplication = DPApplication.getInstance();

    public ContextDataProvider() { }


    @Override
    public void onCreate() {
        try {
            context = getApplicationContext();
            //startBus();
            dpApplication.getInstance().initCDDL("10.0.2.2");

            //context = dpApplication.getInstance().getContext();

            sub = SubscriberFactory.createSubscriber();

            sub.addConnection(dpApplication.getInstance().CDDLGetInstance().getConnection());

            subDeactive = SubscriberFactory.createSubscriber();
            subDeactive.addConnection(dpApplication.getInstance().CDDLGetInstance().getConnection());
        }catch (Exception e){
            Log.e(TAG,"Error: " + e.toString());
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "#### Iniciando ContextDataProvider");

        subscribeMessageActive("activesensor");

        subscribeMessageDeactive("deactivatesensor");

        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void subscribeMessageActive(String serviceName) {
        sub.subscribeServiceByName(serviceName);
        sub.setSubscriberListener(subscriberStart);
    }

    public void subscribeMessageDeactive(String serviceName) {
        subDeactive.subscribeServiceByName(serviceName);
        subDeactive.setSubscriberListener(subscriberStop);
    }


    public ISubscriberListener subscriberStart = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages (start):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);


            if (isInternalSensor(atividade) || isVirtualSensor(atividade)) {
                Log.d(TAG, "#### Start sensor monitoring->  " + atividade);
                startSensor(atividade);
            } else {
                Log.d(TAG, "#### Invalid sensor name: " + atividade);
            }
        }
    };


    public ISubscriberListener subscriberStop = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### Read messages (stop):  " + message);

            Object[] valor = message.getServiceValue();
            String mensagemRecebida = StringUtils.join(valor, ", ");
            Log.d(TAG, "#### " + mensagemRecebida);
            String[] separated = mensagemRecebida.split(",");
            String atividade = String.valueOf(separated[0]);


            if (isInternalSensor(atividade) || isVirtualSensor(atividade)) {
                Log.d(TAG, "#### Stop sensor monitoring->  " + atividade);
                stopSensor(atividade);
            } else {
                Log.d(TAG, "#### Invalid sensor name: " + atividade);
            }
        }
    };


    public ISubscriberListener subscriberPlugins = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
//                    if (message.getServiceName().equals("Meu serviço")) {
//                        Log.d(TAG, ">>> #### Read messages +++++: " + message);
//                    }
            Log.d(TAG, "#### >>>>>>>>>>>>>>>>>>> Read messages PLUGINS:  " + message);

//            Object[] valor = message.getServiceValue();
//            String mensagemRecebida = StringUtils.join(valor, ", ");
//            Log.d(TAG, "#### " + mensagemRecebida);
//            String[] separated = mensagemRecebida.split(",");
//            String atividade = String.valueOf(separated[0]);
        }
    };


    public synchronized void publishMessage(String service, String text) {
        dpApplication.getInstance().publishMessage(service, text);
    }


    private Boolean isInternalSensor(String sensor) {
        if (listInternalSensor().contains(sensor)) {
            return true;
        }
        return false;
    }


    private Boolean isVirtualSensor(String sensor) {
        if (listVirtualSensor().contains(sensor)) {
            return true;
        }
        return false;
    }


    public List<String> listVirtualSensor() {
        List<String> s = dpApplication.getInstance().CDDLGetInstance().getSensorVirtualList();

        Log.i(TAG, "\n #### Sensores virtuais disponíveis, Tamanho: \n" + s.size());
        for (int i = 0; i < s.size(); i++) {
            Log.i(TAG, "#### (" + i + "): " + s.get(i).toString());
        }
        return s;
    }


    public List<String> listInternalSensor() {
        List<String> s = new ArrayList();
        List<Sensor> sensorInternal = dpApplication.getInstance().CDDLGetInstance().getInternalSensorList();

        Log.i(TAG, "\n #### Sensores internos disponíveis, Tamanho: \n" + sensorInternal.size());
        if (sensorInternal.size() != 0) {
            for (int i = 0; i < sensorInternal.size(); i++) {
                s.add(sensorInternal.get(i).getName());
                Log.i(TAG, "#### (" + i + "): " + sensorInternal.get(i).getName());
            }
            return s;
        }
        return s;
    }


    public void startSensor(String sensor) {
        if (sensor.equalsIgnoreCase("TouchScreen")) {
            // Solicita permissão de desenhar (canDrawOverlays) para Toque de Tela
            checkDrawOverlayPermission();
            dpApplication.getInstance().CDDLGetInstance().startSensor(sensor, 0);
        } else {
            initPermissions(sensor);
            dpApplication.getInstance().CDDLGetInstance().startSensor(sensor, 0);
        }
        //cddl.startSensor("SMS",0);
        //cddl.startSensor("Call",0);
        //cddl.startSensor("ScreenOnOff",0);
    }


    public void stopSensor(String sensor) {
        dpApplication.getInstance().CDDLGetInstance().stopSensor(sensor);
    }


    public void startAllVirtualSensors() {
        // solicita permissão ao  usuário
        initAllPermissions();

        //Start sensores virtuais pelo nome e delay
        dpApplication.getInstance().CDDLGetInstance().startSensor("SMS", 0);
        dpApplication.getInstance().CDDLGetInstance().startSensor("Call", 0);
        dpApplication.getInstance().CDDLGetInstance().startSensor("ScreenOnOff", 0);

        // Solicita permissão de desenhar (canDrawOverlays) para Toque de Tela
        checkDrawOverlayPermission();
        dpApplication.getInstance().CDDLGetInstance().startSensor("TouchScreen", 0);
    }


    public Context getContext(){
        return this.context;
    }


    private void checkDrawOverlayPermission() {
        Log.i(TAG, "#### Permissao para o sensor TouchScreen");
        // check if we already  have permission to draw over other apps
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(dpApplication.getInstance().getContext())) {
                // if not construct intent to request permission
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + dpApplication.getInstance().getContext().getPackageName()));
                //startService(intent);
                // request permission via start activity for result
                Log.i(TAG, "#### permissao dada pelo usuário");

                dpApplication.getInstance().getActivity().startActivityForResult(intent, 1);
            }
        }
    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void initPermissions(String sensor) {
        // Checa as permissões para rodar os sensores virtuais
        int PERMISSION_ALL = 1;

        if (sensor.equalsIgnoreCase("SMS")) {
            String[] PERMISSIONS = {
                    // SMS entrada
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.RECEIVE_SMS,
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.WRITE_CONTACTS,
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.GET_ACCOUNTS,
                    // SMS saída
                    android.Manifest.permission.READ_EXTERNAL_STORAGE};

            if (!hasPermissions(dpApplication.getInstance().getActivity(), PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for the sensor: " + sensor);
                ActivityCompat.requestPermissions(dpApplication.getInstance().getActivity(), PERMISSIONS, PERMISSION_ALL);
            }
        } else if (sensor.equalsIgnoreCase("Call")) {
            String[] PERMISSIONS = {
                    //Call
                    android.Manifest.permission.READ_PHONE_STATE,
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.READ_CALL_LOG,
                    android.Manifest.permission.WRITE_CALL_LOG,
                    android.Manifest.permission.ADD_VOICEMAIL};

            if (!hasPermissions(dpApplication.getInstance().getActivity(), PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for the sensor: " + sensor);
                ActivityCompat.requestPermissions(dpApplication.getInstance().getActivity(), PERMISSIONS, PERMISSION_ALL);
            }
        }
//        String[] PERMISSIONS = {
//                // SMS entrada
//                android.Manifest.permission.SEND_SMS,
//                android.Manifest.permission.RECEIVE_SMS,
//                android.Manifest.permission.READ_SMS,
//
//                // SMS saída
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
//
//                //Call
//                android.Manifest.permission.READ_PHONE_STATE,
//                android.Manifest.permission.CALL_PHONE,
//                android.Manifest.permission.READ_CALL_LOG,
//                android.Manifest.permission.WRITE_CALL_LOG,
//                android.Manifest.permission.ADD_VOICEMAIL,
//
//                // Escrita no storage Certificado Digital
//                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//
//                // Para usar o GPS
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//        };
    }


    private void initAllPermissions() {
        // Checa as permissões para rodar os sensores virtuais
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                // SMS
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.READ_SMS,

                //Call
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.CALL_PHONE,
                android.Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.WRITE_CALL_LOG,
                android.Manifest.permission.ADD_VOICEMAIL,

                // SMS saída
                android.Manifest.permission.READ_EXTERNAL_STORAGE,

                // Escrita no storage Certificado Digital
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,

                // Para usar o GPS
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (!hasPermissions(dpApplication.getInstance().getActivity(), PERMISSIONS)) {
            Log.i(TAG, "##### Permissão Ativada");
            ActivityCompat.requestPermissions(dpApplication.getInstance().getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
    }


    private void initPermissionsRequired() {
        // Checa as permissões para rodar os sensores virtuais
        int PERMISSION_ALL = 1;

        if (true) {
            String[] PERMISSIONS = {
                    // Service location
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION

                    // Outros services
            };

            if (!hasPermissions(dpApplication.getInstance().getActivity(), PERMISSIONS)) {
                Log.i(TAG, "##### Permission enabled for framework");
                ActivityCompat.requestPermissions(dpApplication.getInstance().getActivity(), PERMISSIONS, PERMISSION_ALL);
            }
        }
    }
}

