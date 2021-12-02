package br.ufma.lsdi.digitalphenotyping.dataprocessor.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhenotypesEventDAO {
    @Query("SELECT * FROM PhenotypesEvent WHERE PhenotypesEvent.dataProcessorName = :name")
    List<PhenotypesEvent> findByPhenotypeAll(String name);

    @Insert
    void insert(PhenotypesEvent... phenotypes);

    @Delete
    void delete(PhenotypesEvent phenotypesEvent);

    @Query("SELECT count(*) FROM PhenotypesEvent WHERE PhenotypesEvent.dataProcessorName = :name")
    int totalPhenotypes(String name);
}