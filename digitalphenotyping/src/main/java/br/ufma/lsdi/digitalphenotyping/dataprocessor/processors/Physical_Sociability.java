package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.SaveActivity;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Situation;

public class Physical_Sociability extends DataProcessor{
    private static final String TAG = Physical_Sociability.class.getName();
    private SaveActivity saveActivity = SaveActivity.getInstance();
    private List<String> listSensors = new ArrayList();
    //private AlarmAudio alarm = new AlarmAudio();
    private Voice voice = new Voice();

    @Override
    public void init() {
        try {
            Log.i(TAG, "#### Running processor Physical_Sociability.");

            setDataProcessorName("Physical_Sociability");

            initPermissions();
            voice.config(getContext());
            voice.setAlarm(2000);
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.toString());
        }
    }


    @Override
    public void onSensorDataArrived(Message message) {
        voice.setAlarm(2000);
        inferencePhenotypingEvent(message);
    }


    @Override
    public void inferencePhenotypingEvent(Message message){
        try {
            Log.i(TAG, "#### MSG ORIGINAL PHYSICAL_SOCIABILITY: " + message);
            DigitalPhenotypeEvent digitalPhenotypeEvent = new DigitalPhenotypeEvent();
            digitalPhenotypeEvent.setDataProcessorName(getDataProcessorName());
            digitalPhenotypeEvent.setUid(CDDL.getInstance().getConnection().getClientId());

            Object[] valor1 = message.getServiceValue();
            String mensagemRecebida1 = StringUtils.join(valor1, ", ");
            String[] listServiceValue = mensagemRecebida1.split(",");

            String[] valor2 = message.getAvailableAttributesList();
            String mensagemRecebida2 = StringUtils.join(valor2, ", ");
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

            Log.i(TAG, "#### DigitalPhenotypeEvent: " + digitalPhenotypeEvent.toString());

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
        voice.desableAlarm();
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
}
