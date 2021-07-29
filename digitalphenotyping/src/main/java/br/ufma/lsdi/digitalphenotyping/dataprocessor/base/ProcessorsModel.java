package br.ufma.lsdi.digitalphenotyping.dataprocessor.base;

import br.ufma.lsdi.cddl.message.Message;

public interface ProcessorsModel {
    public void startSensor(String nameSensor);
    public void stopSensor(String nameSensor);
    public void publish(Message message);
    public String subscribe();
    public void inference();
}
