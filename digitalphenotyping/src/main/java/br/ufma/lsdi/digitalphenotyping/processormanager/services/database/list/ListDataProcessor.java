package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "listdataprocessor", indices = @Index(value = {"dataProcessor"}, unique = true))
public class ListDataProcessor {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "dataProcessor")
    public String dataProcessorName;

    public void setDataProcessorName(String dataProcessorName) {
        this.dataProcessorName = dataProcessorName;
    }

    public String getDataProcessorName(){
        return dataProcessorName;
    }
}