package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ListDataProcessor.class}, version = 4)
public abstract class AppDatabasePM2 extends RoomDatabase {
    public abstract ListDataProcessorDAO listDataProcessorDAO();
}
