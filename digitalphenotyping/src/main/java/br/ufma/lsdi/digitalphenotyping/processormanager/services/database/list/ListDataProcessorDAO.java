package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ListDataProcessorDAO {
    @Query("SELECT * FROM listdataprocessor")
    ListDataProcessor findByListDataProcessorAll();

    @Insert
    void insertAll(ListDataProcessor... listdataprocessor);

    @Delete
    void delete(ListDataProcessor listdataprocessor);
}