package br.ufma.lsdi.digitalphenotyping.dpmanager.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.database.PhenotypesEvent;
import br.ufma.lsdi.digitalphenotyping.dataprocessor.database.PhenotypesEventDAO;
import br.ufma.lsdi.digitalphenotyping.phenotypecomposer.database.PhenotypeDAO;
import br.ufma.lsdi.digitalphenotyping.phenotypecomposer.database.Phenotypes;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessor;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active.ActiveDataProcessorDAO;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessor;
import br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list.ListDataProcessorDAO;

@Database(entities = {FrameworkOnOff.class, Phenotypes.class, PhenotypesEvent.class, ActiveDataProcessor.class, ListDataProcessor.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    /**
     * Gerencia o flag do framework: ligado, desligado
     * @return Status do framework: ligado/desligado
     */
    public abstract FrameworkOnOffDAO frameworkOnOffDAO();


    /**
     * Gerencia os fenótipos digitais a partir da composoção de vários eventos de fenotipagem digital.
     * @return Classe DigitalPhenotype
     */
    public abstract PhenotypeDAO phenotypeDAO();


    /**
     * Gerencia os eventos de fenotipagem digital (e.g., chamada telefônica, SMS, Audio)
     * @return DigitalPhenotypeEvent
     */
    public abstract PhenotypesEventDAO phenotypesEventDAO();


    /**
     * Gerencia os processadores de dados ativos, apresentando na tela principal apenas os processadores ativos..
     * @return Classe DataProcessor
     */
    public abstract ActiveDataProcessorDAO activeDataProcessorDAO();


    /**
     * Gerencia a list de processadores de dados, disponibiliza todos os processadores implementados
     * no framework.
     * @return
     */
    public abstract ListDataProcessorDAO listDataProcessorDAO();
}