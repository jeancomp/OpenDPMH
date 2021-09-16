package br.ufma.lsdi.digitalphenotyping.phenotypecomposer.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PhenotypeDAO {
    @Query("SELECT * FROM phenotypes")
    Phenotypes findByPhenotypeAll();

    @Insert
    void insertAll(Phenotypes... phenotypes);

    @Delete
    void delete(Phenotypes phenotypes);
}