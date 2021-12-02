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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.cddl.message.Message;
import br.ufma.lsdi.digitalphenotyping.SaveActivity;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.base.DataProcessor;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Situation;

public class Online_Sociability extends DataProcessor{
    private static final String TAG = Online_Sociability.class.getName();
    private SaveActivity saveActivity = SaveActivity.getInstance();
    private List<String> listSensors = new ArrayList();

    @Override
    public void init() {
        try {
            Log.i(TAG, "#### Running processor Online_Sociability.");

            setDataProcessorName("Online_Sociability");

            initPermissions();
            listSensors.add("Call");
            listSensors.add("SMS");
            startSensor(listSensors);
        }catch (Exception e){
            Log.e(TAG, "#### Error: " + e.toString());
        }
    }


    @Override
    public void onSensorDataArrived(Message message) {
        inferencePhenotypingEvent(message);
    }


    @Override
    public void inferencePhenotypingEvent(Message message){
        try {
            Log.i(TAG, "#### MSG ORIGINAL ONLINE_SOCIABILITY: " + message);
            DigitalPhenotypeEvent digitalPhenotypeEvent = new DigitalPhenotypeEvent();
            digitalPhenotypeEvent.setDataProcessorName(getDataProcessorName());
            digitalPhenotypeEvent.setUid(CDDL.getInstance().getConnection().getClientId());

            Object[] valor1 = message.getServiceValue();
            String mensagemRecebida1 = StringUtils.join(valor1, ",");
            String[] listServiceValue = mensagemRecebida1.split(",");

            String[] valor2 = message.getAvailableAttributesList();
            String mensagemRecebida2 = StringUtils.join(valor2, ",");
            String[] listAttributes = mensagemRecebida2.split(",");

            if (message.getServiceName().equals("Call")) {
                Situation situation = new Situation();
                situation.setLabel("PhoneCall_Online_Sociability");
                situation.setDescription("We identify Online_Sociability by the user through the phone call.");
                digitalPhenotypeEvent.setSituation(situation);

                if (!listAttributes[1].isEmpty() && !listServiceValue[1].isEmpty()) {
                    // Numero de telefone (Hash)
                    digitalPhenotypeEvent.setAttributes(listAttributes[1], listServiceValue[1], "String", false);
                }
                if (!listAttributes[2].isEmpty() && !listServiceValue[2].isEmpty()) {
                    // Tipo de chamada
                    digitalPhenotypeEvent.setAttributes(listAttributes[2], listServiceValue[2], "String", false);
                }
                if (!listAttributes[3].isEmpty() && !listServiceValue[3].isEmpty()) {
                    // Data da Chamada
                    digitalPhenotypeEvent.setAttributes(listAttributes[3], listServiceValue[3], "Date", false);
                }
                if (!listAttributes[4].isEmpty() && !listServiceValue[4].isEmpty()) {
                    // Duração(seg)
                    digitalPhenotypeEvent.setAttributes(listAttributes[4], listServiceValue[4], "Integer", false);
                }
            }
            else if (message.getServiceName().equals("SMS")) {
                Situation situation = new Situation();
                situation.setLabel("SMS_Online_Sociability");
                situation.setDescription("We identify Online_Sociability by the user through the SMS.");
                digitalPhenotypeEvent.setSituation(situation);

                if (!listAttributes[1].isEmpty() && !listServiceValue[1].isEmpty()) {
                    // Tipo Mensagem
                    digitalPhenotypeEvent.setAttributes(listAttributes[1], listServiceValue[1], "String", false);
                }
                if (!listAttributes[2].isEmpty() && !listServiceValue[2].isEmpty()) {
                    // Corpo da Mensagem
                    digitalPhenotypeEvent.setAttributes(listAttributes[2], listServiceValue[2], "String", false);
                }
                if (!listAttributes[3].isEmpty() && !listServiceValue[3].isEmpty()) {
                    // Timestamp
                    digitalPhenotypeEvent.setAttributes(listAttributes[3], listServiceValue[3], "Date", false);
                }
                if (!listAttributes[4].isEmpty() && !listServiceValue[4].isEmpty()) {
                    // Número Destinatáio
                    digitalPhenotypeEvent.setAttributes(listAttributes[4], listServiceValue[4], "Integer", false);
                }
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
    public void end(){}


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public final IBinder mBinder = new Online_Sociability.LocalBinder();


    public class LocalBinder extends Binder {
        public Online_Sociability getService() {
            return Online_Sociability.this;
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

        String[] PERMISSIONS = {
                //SMS
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                //Call
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.ADD_VOICEMAIL};
        if (!hasPermissions(saveActivity.getInstance().getActivity(), PERMISSIONS)) {
            Log.i(TAG, "##### Permission enabled!");
            ActivityCompat.requestPermissions(saveActivity.getInstance().getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
