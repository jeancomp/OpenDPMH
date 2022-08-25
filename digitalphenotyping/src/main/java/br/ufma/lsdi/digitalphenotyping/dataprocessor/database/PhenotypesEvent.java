package br.ufma.lsdi.digitalphenotyping.dataprocessor.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent.Situation;

@Entity(tableName = "phenotypesevent")
public class PhenotypesEvent {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    @ColumnInfo(name = "dataProcessorName")
    String dataProcessorName;

    @NonNull
    @ColumnInfo(name = "phenotypeevent")
    String phenotypeevent;

    public String getDataProcessorName() {
        return dataProcessorName;
    }

    public void setDataProcessorName(String dataProcessorName) {
        this.dataProcessorName = dataProcessorName;
    }

    public String getPhenotypeEvent() {
        return phenotypeevent;
    }

    public void setPhenotypeEvent(String str) {
        this.phenotypeevent = str;
    }

    public void stringFromObject(Situation dpe) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(dpe);
        phenotypeevent = jsonString;
    }

    public Situation getObjectFromString(String jsonString) {
        Type listType = new TypeToken<Situation>() {
        }.getType();
        Situation dpe = new Gson().fromJson(jsonString, listType);
        return dpe;
    }
}