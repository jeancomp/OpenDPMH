package br.ufma.lsdi.digitalphenotyping.rawcontextdataprocessor.base;

import android.util.Log;
import br.pucrio.inf.lac.mhub.models.locals.SensorDataExtended;

public abstract class RawContextData extends SensorDataExtended {
    String nameProcessor;
    Object rawData;

    public RawContextData() { }

    public void receive(){
        Log.i("","");
    }

    public abstract void send();
}
