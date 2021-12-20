package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ListDataProcessorDAO {
    @Query("SELECT * FROM listdataprocessor")
    List<ListDataProcessor> findByListDataProcessorAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ListDataProcessor... listdataprocessor);

    /*@Delete
    void delete(ListDataProcessor listdataprocessor);*/

    @Query("DELETE FROM listdataprocessor")
    void delete();


    @Query("SELECT count(*) FROM listdataprocessor")
    int totalDataProcessorAll();
}