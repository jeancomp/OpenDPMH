package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.listeners.ISubscriberListener;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.cddl.pubsub.Subscriber;
import br.ufma.lsdi.cddl.pubsub.SubscriberFactory;
import br.ufma.lsdi.digitalphenotyping.SaveActivity;
import br.ufma.lsdi.digitalphenotyping.Topics;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Situation;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.util.AlarmAudio;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.util.TriggerAlarm2;

public class Physical_Sociability extends DataProcessor{
    private static final String TAG = Physical_Sociability.class.getName();
    private SaveActivity saveActivity = SaveActivity.getInstance();
    private List<String> listSensors = new ArrayList();
    private AlarmAudio alarm = new AlarmAudio();
    private long frequency_sensor = 3000;
    private Subscriber subMessage;
    private static final int ID_SERVICE = 103;
    private Voice voice = new Voice();
    private TriggerAlarm2 triggerAlarm2;
    private boolean flag;
    private int contador;

    @Override
    public void init() {
        try {
            Log.i(TAG, "#### Running processor Physical_Sociability.");

            /*NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Audio")
                    .setContentText("Audio module")
                    .setPriority(PRIORITY_MIN)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .build();

            startForeground(ID_SERVICE, notification);*/

            setDataProcessorName("Physical_Sociability");

            initPermissions();
            voice.getInstance().config(this);
            alarm.setAlarm(getContext(), frequency_sensor);

            subMessage = SubscriberFactory.createSubscriber();
            subMessage.addConnection(CDDL.getInstance().getConnection());
            subscribeMessage(Topics.AUDIO_TOPIC.toString());

            triggerAlarm2 = new TriggerAlarm2();
            triggerAlarm2.getInstance().set(false);

            flag = true;
            contador = 0;
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.toString());
        }
    }


    /*@RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "my_service_channelid_audio";
        String channelName = "Service audio";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        //channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        //channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }*/


    @Override
    public void dO(){
        subscribeMessage(Topics.AUDIO_TOPIC.toString());
        initPermissions();

        final int tempoDeEspera = 60000;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(tempoDeEspera);
                if(!triggerAlarm2.getInstance().get()){
                    triggerAlarm2.getInstance().set(false);

                    //cria uma mensagem nula: nenhum dado de sensor foi gerado no intervalo de 1 min
                    String dataProcessorName = "Physical_Sociability";
                    String alert = "Nenhum_dado";
                    long stamp = System.currentTimeMillis();
                    String str = String.valueOf(stamp);
                    Object[] valor = {dataProcessorName, alert, str};
                    String[] atributte = {"data processor name", "message", "timestamp"};

                    Message message = new Message();
                    message.setServiceValue(valor);
                    message.setAvailableAttributesList(atributte);
                    message.setAvailableAttributes(3);
                    message.setServiceName("audiodetected");

                    onSensorDataArrived(message);
                }
            }
        }).start();
    }


    @Override
    public void onSensorDataArrived(Message message) {
        triggerAlarm2.getInstance().set(true); // Dado de contexto recebido dentro do intervalor de 1 min.
        alarm.setAlarm(getContext(), frequency_sensor);

        // Regra para enviar 1 Message a cada 100
        contador = contador + 1;
        Log.i(TAG,"#### Contador: " + contador);
        if(flag){
            inferencePhenotypingEvent(message);
            flag = false;
        }
        if(contador >= 100){
            flag = true;
            contador = 0;
        }
    }


    @Override
    public void inferencePhenotypingEvent(Message message){
        try {
            //Log.i(TAG, "#### MSG ORIGINAL PHYSICAL_SOCIABILITY: " + message);
            DigitalPhenotypeEvent digitalPhenotypeEvent = new DigitalPhenotypeEvent();
            digitalPhenotypeEvent.setDataProcessorName(getDataProcessorName());
            digitalPhenotypeEvent.setUid(CDDL.getInstance().getConnection().getClientId());

            Object[] valor1 = message.getServiceValue();
            String mensagemRecebida1 = StringUtils.join(valor1, ",");
            String[] listServiceValue = mensagemRecebida1.split(",");

            String[] valor2 = message.getAvailableAttributesList();
            String mensagemRecebida2 = StringUtils.join(valor2, ",");
            String[] listAttributes = mensagemRecebida2.split(",");

            Situation situation = new Situation();
            situation.setLabel("Physical_Sociability");
            situation.setDescription("We identify Physical_Sociability by the user through the audio.");
            digitalPhenotypeEvent.setSituation(situation);

            if (!listAttributes[1].isEmpty() && !listServiceValue[1].isEmpty()) {
                digitalPhenotypeEvent.setAttributes(listAttributes[1], listServiceValue[1], "String", false);
            }
            if (!listAttributes[2].isEmpty() && !listServiceValue[2].isEmpty()) {
                digitalPhenotypeEvent.setAttributes(listAttributes[2], listServiceValue[2], "Date", false);
            }

            //Log.i(TAG, "#### DigitalPhenotypeEvent: " + digitalPhenotypeEvent.toString());

            String json = toJson(digitalPhenotypeEvent);
            Message msg = new Message();
            msg.setServiceValue(json);
            sendProcessedData(msg);
            saveDigitalPhenotypeEvent(digitalPhenotypeEvent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void end(){
        try {
            alarm.desableAlarm();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public final IBinder mBinder = new Physical_Sociability.LocalBinder();


    public class LocalBinder extends Binder {
        public Physical_Sociability getService() {
            return Physical_Sociability.this;
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


    private void initPermissions() {
        // Checa as permissões para rodar os sensores virtuais
        int PERMISSION_ALL = 1;

        String[] PERMISSIONS = { Manifest.permission.RECORD_AUDIO};
        if (!hasPermissions(saveActivity.getInstance().getActivity(), PERMISSIONS)) {
            Log.i(TAG, "##### Permission enabled!");
            ActivityCompat.requestPermissions(saveActivity.getInstance().getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
    }


    public void subscribeMessage(String serviceName) {
        subMessage.subscribeServiceByName(serviceName);
        subMessage.setSubscriberListener(subscriberMsg);
    }

    public ISubscriberListener subscriberMsg = new ISubscriberListener() {
        @Override
        public void onMessageArrived(Message message) {
            //Log.i(TAG, "#### Read-audio detected:  " + message);
            onSensorDataArrived(message);
        }
    };
}
