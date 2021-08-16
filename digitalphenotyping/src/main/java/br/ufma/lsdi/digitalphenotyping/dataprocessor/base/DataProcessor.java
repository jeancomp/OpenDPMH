package br.ufma.lsdi.digitalphenotyping.dataprocessor.base;

import br.ufma.lsdi.cddl.message.Message;

public interface DataProcessor {
    //Context context;
    public void onStartSensor(String nameSensor);
    public void onStopSensor(String nameSensor);
    public void setCommunicationTechnology(int number);
    public void publishInference(Message message);
    public String subscribeRawData(Message message);
    public void inference(Message message);
}
