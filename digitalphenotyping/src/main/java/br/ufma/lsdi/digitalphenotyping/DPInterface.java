package br.ufma.lsdi.digitalphenotyping;

import android.app.Activity;

import androidx.annotation.IntRange;

import java.util.List;

/**
 * Interface do framework
 */
public interface DPInterface {
    public void start(Activity activity, String host, int port, String username, String password, String topic, @IntRange(from=0,to=2) int configuration);
    public void stop();
    public void startDataProcessors(List<String> nameProcessors);
    public void stopDataProcessors(List<String> nameProcessors);
    public List<String> getDataProcessorsList();
    public List<String> getActiveDataProcessorsList();
    public void setExternalServer(String hostServer, int port, String username, String password, String topic);
    public void setConfiguration(int number);
}

// Annotations: força valores para variaveis
// https://developer.android.com/studio/write/annotations?hl=pt-br#thread-annotations