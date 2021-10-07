package br.ufma.lsdi.digitalphenotyping.dp;

import java.util.List;

import br.ufma.lsdi.digitalphenotyping.dp.handlingexceptions.InvalidDataProcessorNameException;
import br.ufma.lsdi.digitalphenotyping.dp.handlingexceptions.InvalidHostServerException;
import br.ufma.lsdi.digitalphenotyping.dp.handlingexceptions.InvalidPortException;

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
    public void saveExternalServerAddress(String hostServer, Integer port, String username, String password) throws InvalidHostServerException, InvalidPortException;
}

// Annotations: força valores para variaveis
// https://developer.android.com/studio/write/annotations?hl=pt-br#thread-annotations