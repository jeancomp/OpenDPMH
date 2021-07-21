package br.ufma.lsdi.digitalphenotyping;

/**
 * Interface do framework
 */
public interface DigitalPhenotyping {
    public void start();
    public void stop();
    public void startProcessor(String nameProcessor);
    public void stopProcessor(String nameProcessor);
    public void activaSensor(String nameSensor);
    public void deactivateSensor(String nameSensor);
    public void publish(String service, String message);
    public void subscriber();
    public void initPermissionsRequired();
}
