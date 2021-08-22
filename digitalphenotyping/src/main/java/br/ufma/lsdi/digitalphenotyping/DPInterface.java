package br.ufma.lsdi.digitalphenotyping;

import java.util.List;

/**
 * Interface do framework
 */
public interface DPInterface {
    public void start();
    public void stop();
    public void startDataProcessors(List<String> nameProcessors);
    public void stopDataProcessors(List<String> nameProcessors);
    public List<String> getDataProcessorsList();
    public List<String> getActiveDataProcessorsList();
    public void setExternalServerAddress(String hostServer, int port, String clientID, String username, String password, String topic, int compositionMode);
}

// Annotations: força valores para variaveis
// https://developer.android.com/studio/write/annotations?hl=pt-br#thread-annotations