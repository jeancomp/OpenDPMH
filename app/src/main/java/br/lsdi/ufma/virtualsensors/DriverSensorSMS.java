package br.lsdi.ufma.virtualsensors;

import android.content.Context;
import android.hardware.SensorManager;
import br.pucrio.inf.lac.mhub.s2pa.technologies.internal.InternalTechnology;
import br.ufma.lsdi.cddl.CDDL;

public class DriverSensorSMS {
    private CDDL mHubCDDL;
    private Context context;

    //Construtor
    public DriverSensorSMS(CDDL cddl){
        mHubCDDL = cddl;
        configS2PA();
    }


    // Configura o S2PA para receber os dados do sensor virtual  SMS
    public void configS2PA() {
        //Obtem instancia da classe MHubCDDL (Padrao Singleton)
        mHubCDDL = CDDL.getInstance();
        //Inicia os servicos do middleware, dentre eles o S2PA
        mHubCDDL.startService();
        //Inicia a tecnologia de sensores internos
        mHubCDDL.startCommunicationTechnology(InternalTechnology.ID);
        //Inicia o sensor de aceleracao
        mHubCDDL.startSensor("SMS", SensorManager.SENSOR_DELAY_FASTEST);
    }
}
