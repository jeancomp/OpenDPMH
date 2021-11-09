package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ActiveDataProcessor.class}, version = 3)
public abstract class AppDatabasePM1 extends RoomDatabase {
    public abstract ActiveDataProcessorDAO activeDataProcessorDAO();
}