package br.ufma.lsdi.digitalphenotyping.rawdatacollector.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RawData.class}, version = 2)
public abstract class AppDatabaseRD extends RoomDatabase {
    public abstract RawDataDAO rawDataDAO();
}