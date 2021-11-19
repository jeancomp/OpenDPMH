package br.ufma.lsdi.digitalphenotyping.dpmanager.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {FrameworkOnOff.class}, version = 5)
public abstract class AppDatabaseDPM extends RoomDatabase {
    public abstract FrameworkOnOffDAO frameworkOnOffDAO();
}