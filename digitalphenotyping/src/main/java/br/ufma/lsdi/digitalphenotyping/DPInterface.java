package br.ufma.lsdi.digitalphenotyping;

import java.util.List;

/**
 * Interface framework
 */
public interface DPInterface {
    public void start();
    public void stop();
    public void startDataProcessors(List<String> nameProcessors);
    public void stopDataProcessors(List<String> nameProcessors);
    public List<String> getDataProcessorsList();
    public List<String> getActiveDataProcessorsList();
    public void saveExternalServerAddress(String hostServer, Integer port, String username, String password);
}

// Annotations: força valores para variaveis
// https://developer.android.com/studio/write/annotations?hl=pt-br#thread-annotations