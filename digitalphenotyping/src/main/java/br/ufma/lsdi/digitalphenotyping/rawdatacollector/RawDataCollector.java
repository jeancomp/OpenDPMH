package br.ufma.lsdi.digitalphenotyping.rawdatacollector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

import br.ufma.lsdi.cddl.CDDL;
import br.ufma.lsdi.digitalphenotyping.CompositionMode;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.handlingexceptions.InvalidSensorNameException;

public class RawDataCollector extends Service {
    private List<DataSource> dataSourceList = new ArrayList();
    private Context context;
    private RawDataCollectorUtil rawDataCollectorUtil;


    @Override
    public void onCreate() {
        try {
            context = this;
            DataSource touch = new DataSource("TouchScreen","select * from Message");
            DataSource call = new DataSource("Call","select * from Message");
            //DataSource acel = new DataSource("MC34XX ACCELEROMETER", 1000,"select count(*) from Message.win:time(5 sec) having count(*) >= 10");

            dataSourceList.add(call);
            dataSourceList.add(touch);
            rawDataCollectorUtil =new RawDataCollectorUtil(context, dataSourceList, CompositionMode.GROUP_ALL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        try {
            rawDataCollectorUtil.connectionBroker("192.168.0.7","1883","","", CDDL.getInstance().getConnection().getClientId(), "rawdatacollector");
        } catch (InvalidSensorNameException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        try {
            rawDataCollectorUtil.stopSensor(dataSourceList);
        } catch (InvalidSensorNameException e) {
            e.printStackTrace();
        }
    }
}
