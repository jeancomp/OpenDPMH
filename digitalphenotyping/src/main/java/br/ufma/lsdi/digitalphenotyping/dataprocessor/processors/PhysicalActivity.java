package br.ufma.lsdi.digitalphenotyping.dataprocessor.processors;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.ActivityRecognitionClient;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.SaveActivity;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Situation;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidDataProcessorNameException;

/**
 * PhysicalActivity does activity recognition: walking, running, driving, standing
 * GPS connected required
 */
public class PhysicalActivity extends DataProcessor {
    private static final String TAG = PhysicalActivity.class.getName();
    List<String> sensorList = new ArrayList();
    List<Integer> samplingRateList = new ArrayList();
    ActivityRecognitionClient mActivityRecognitionClient;
    SaveActivity saveActivity = SaveActivity.getInstance();

    @Override
    public void init(){
        try {
            setDataProcessorName("PhysicalActivity");

            initPermissions();
            Intent i = new Intent(this, ActivityDetectionService.class);
            startService(i);
        } catch (InvalidDataProcessorNameException e) {
            e.printStackTrace();
        }
        /*sensorList.add("Call");
        startSensor(sensorList);*/
        /*sensorList.add("MC34XX ACCELEROMETER");
        samplingRateList.add(8000);
        startSensor(sensorList,samplingRateList);*/
    }


    @Override
    public void onSensorDataArrived(Message message){
        inferencePhenotypingEvent(message);
    }


    @Override
    public void inferencePhenotypingEvent(Message message){
        Log.i("PhysicalActivity","#### MSG ORIGINAL PhysicalActivity: " + message);
        DigitalPhenotypeEvent digitalPhenotypeEvent = new DigitalPhenotypeEvent();
        digitalPhenotypeEvent.setDataProcessorName(getDataProcessorName());
        digitalPhenotypeEvent.setUid(CDDL.getInstance().getConnection().getClientId());

        Object[] valor1 = message.getServiceValue();
        String mensagemRecebida1 = StringUtils.join(valor1, ",");
        String[] listValues = mensagemRecebida1.split(",");

        String[] valor2 = message.getAvailableAttributesList();
        String mensagemRecebida2 = StringUtils.join(valor2, ",");
        String[] listAttributes = mensagemRecebida2.split(",");

        Situation situation = new Situation();
        situation.setLabel(listValues[0]);
        situation.setDescription("Type of activity");
        digitalPhenotypeEvent.setSituation(situation);

        if(!listAttributes[1].isEmpty() && !listValues[1].isEmpty()) {
            digitalPhenotypeEvent.setAttributes(listAttributes[1], listValues[1], "Integer", false);
        }
        if(!listAttributes[2].isEmpty() && !listValues[2].isEmpty()) {
            digitalPhenotypeEvent.setAttributes(listAttributes[2], listValues[2], "Date", false);
        }

        Log.i("PhysicalActivity","#### DigitalPhenotypeEvent: " + digitalPhenotypeEvent.toString());

        String json = toJson(digitalPhenotypeEvent);
        Message msg = new Message();
        msg.setServiceValue(json);
        sendProcessedData(msg);
        saveDigitalPhenotypeEvent(digitalPhenotypeEvent);
    }


    public void end(){ }


    public final IBinder mBinder = new LocalBinder();


    public class LocalBinder extends Binder {
        public PhysicalActivity getService() {
            return PhysicalActivity.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) { return mBinder; }

    private void initPermissions() {
        // Checa as permissões
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        if (!hasPermissions(saveActivity.getInstance().getActivity(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(saveActivity.getInstance().getActivity(), PERMISSIONS, PERMISSION_ALL);
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
}
