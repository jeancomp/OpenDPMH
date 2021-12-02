package br.ufma.lsdi.digitalphenotyping.dpmanager.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface FrameworkOnOffDAO {
    @Query("SELECT * FROM frameworkstatus")
    FrameworkOnOff findByFrameworkStatus();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(FrameworkOnOff... frameworkOnOff);

    /*@Delete
    void delete(FrameworkOnOff... frameworkOnOff);*/

    @Query("DELETE FROM frameworkstatus")
    void delete();

    @Query("SELECT count(*) FROM frameworkstatus")
    int total();
}
