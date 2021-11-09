package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.list;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "listdataprocessor")
public class ListDataProcessor {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String dataProcessorName = null;

    public void setDataProcessorName(String dataProcessorName) {
        this.dataProcessorName = dataProcessorName;
    }

    public String getDataProcessorName(){
        return dataProcessorName;
    }
}