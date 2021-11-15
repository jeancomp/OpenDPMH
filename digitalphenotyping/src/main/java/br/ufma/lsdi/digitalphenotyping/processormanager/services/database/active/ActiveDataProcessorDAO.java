package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ActiveDataProcessorDAO {
    @Query("SELECT * FROM activedataprocessor")
    List<ActiveDataProcessor> findByActiveDataProcessorAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ActiveDataProcessor... activedataprocessor);

    @Delete
    void delete(ActiveDataProcessor activedataprocessor);

    @Query("SELECT count(*) FROM activedataprocessor")
    int totalActiveDataProcessorAll();
}