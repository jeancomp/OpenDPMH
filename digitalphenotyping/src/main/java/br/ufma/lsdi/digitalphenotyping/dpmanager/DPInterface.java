package br.ufma.lsdi.digitalphenotyping.dpmanager;

import java.util.List;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.database.Phenotypes;
import br.ufma.lsdi.digitalphenotyping.dpmanager.handlingexceptions.InvalidDataProcessorNameException;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessor;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessor;

/**
 * Interface framework
 */
public interface DPInterface {
    public void start();
    public void stop();
    public void startDataProcessors(List<String> nameProcessors) throws InvalidDataProcessorNameException;
    public void stopDataProcessors(List<String> nameProcessors) throws InvalidDataProcessorNameException;
    public List<ListDataProcessor> getDataProcessorsList();
    public List<ActiveDataProcessor> getActiveDataProcessorsList();
    public List<Phenotypes> getPhenotypesList(String situationInterest);
}