package br.ufma.lsdi.digitalphenotyping.rawdatacollector.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface RawDataDAO {
    @Query("SELECT * FROM rawdata")
    RawData findByRawDataAll();

    @Insert
    void insertAll(RawData... rawData);

    @Delete
    void delete(RawData rawData);
}