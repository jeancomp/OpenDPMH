package br.ufma.lsdi.digitalphenotyping.dp;

import java.util.List;

import br.ufma.lsdi.digitalphenotyping.dp.handlingexceptions.InvalidDataProcessorNameException;

/**
 * Interface framework
 */
public interface DPInterface {
    public void start();
    public void stop();
    public void startDataProcessors(List<String> nameProcessors) throws InvalidDataProcessorNameException;
    public void stopDataProcessors(List<String> nameProcessors) throws InvalidDataProcessorNameException;
    public List<String> getDataProcessorsList();
    public List<String> getActiveDataProcessorsList();
}

// Annotations: força valores para variaveis
// https://developer.android.com/studio/write/annotations?hl=pt-br#thread-annotations