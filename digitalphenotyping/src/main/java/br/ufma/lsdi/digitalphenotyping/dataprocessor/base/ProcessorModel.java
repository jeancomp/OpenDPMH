package br.ufma.lsdi.digitalphenotyping.dataprocessor.base;

import br.ufma.lsdi.cddl.message.Message;

public interface ProcessorModel {
    public void startSensor(String nameSensor);
    public void stopSensor(String nameSensor);
    public void setCommunicationTechnology(int number);
    public void publish(Message message);
    public String subscribe(Message message);
    public void inference(Message message);
}
