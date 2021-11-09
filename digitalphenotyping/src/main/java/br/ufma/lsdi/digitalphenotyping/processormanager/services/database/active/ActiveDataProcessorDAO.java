package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ActiveDataProcessorDAO {
    @Query("SELECT * FROM activedataprocessor")
    ActiveDataProcessor findByActiveDataProcessorAll();

    @Insert
    void insertAll(ActiveDataProcessor... activedataprocessor);

    @Delete
    void delete(ActiveDataProcessor activedataprocessor);
}