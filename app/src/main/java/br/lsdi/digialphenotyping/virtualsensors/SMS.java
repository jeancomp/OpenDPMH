package br.lsdi.digialphenotyping.virtualsensors;

import android.Manifest;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

public class SMS extends Fragment implements VirtualSensorInterface{
    SMSSensor smsSensor;

    public SMS(){

    }

    @Override
    public boolean registerListener() {

        smsSensor = new SMSSensor();

        if( smsSensor != null ){
            return true;
        }
        return false;
    }

    @Override
    public boolean unregisterListener() {
        return false;
    }

    @Override
    public boolean startCollecting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //setPermissions();
        }
        else {
            smsSensor = new SMSSensor();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            getActivity().registerReceiver(smsSensor, intentFilter);
        }
        return true;
    }

    @Override
    public boolean stopCollecting() {
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            smsSensor= new SMSSensor();
            IntentFilter intentFilter=new IntentFilter();
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            getContext().registerReceiver(smsSensor, intentFilter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (getContext() != null && smsSensor != null) {
                getContext().unregisterReceiver(smsSensor);
                smsSensor = null;
            }
        } catch (Exception ignored) {
        }
    }

    public void setPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }
}
