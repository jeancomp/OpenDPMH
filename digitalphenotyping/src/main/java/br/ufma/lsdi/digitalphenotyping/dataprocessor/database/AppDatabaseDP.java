package br.ufma.lsdi.digitalphenotyping.dataprocessor.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PhenotypesEvent.class}, version = 6)
public abstract class AppDatabaseDP extends RoomDatabase {
    //public abstract PhenotypeDAO phenotypeDAO();
}