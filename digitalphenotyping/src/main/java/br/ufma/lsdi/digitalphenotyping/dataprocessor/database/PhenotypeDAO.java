package br.ufma.lsdi.digitalphenotyping.dataprocessor.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhenotypeDAO {
    @Query("SELECT * FROM phenotypes WHERE phenotypes.dataProcessorName = :name")
    List<Phenotypes> findByPhenotypeAll(String name);

    @Insert
    void insert(Phenotypes... phenotypes);

    @Delete
    void delete(Phenotypes phenotypes);

    @Query("SELECT count(*) FROM phenotypes WHERE phenotypes.dataProcessorName = :name")
    int totalPhenotypes(String name);
}