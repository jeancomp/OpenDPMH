package br.ufma.lsdi.digitalphenotyping.dataprocessor.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.DigitalPhenotypeEvent;

@Entity(tableName = "phenotypes")
public class Phenotypes{
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "dataProcessorName")
    String dataProcessorName;

    @NonNull
    @ColumnInfo(name = "phenotype")
    String phenotype;

    public String getDataProcessorName(){
        return dataProcessorName;
    }

    public void setDataProcessorName(String dataProcessorName){
        this.dataProcessorName = dataProcessorName;
    }

    public String getPhenotype(){
        return phenotype;
    }

    public void setPhenotype(String str){
        this.phenotype = str;
    }

    public void stringFromObject(DigitalPhenotypeEvent dpe){
        Gson gson = new Gson();
        String jsonString = gson.toJson(dpe);
        phenotype = jsonString;
    }

    public DigitalPhenotypeEvent getObjectFromString(String jsonString){
        Type listType = new TypeToken<DigitalPhenotypeEvent>(){}.getType();
        DigitalPhenotypeEvent dpe = new Gson().fromJson(jsonString, listType);
        return dpe;
    }
}