package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.util.List;

@Entity(tableName = "activedataprocessor")
public class ActiveDataProcessor {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String dataProcessorName = null;
    public String sensorList = null;

    public void setDataProcessorName(String dataProcessorName) {
        this.dataProcessorName = dataProcessorName;
    }

    public String getDataProcessorName(){
        return dataProcessorName;
    }

    /*public void setSensorList(String sensorName){
        sensorList.add(sensorName);
    }

    public List<String> getSensorList(){
        return sensorList;
    }*/

    public void stringFromObject(List<String> sensor){
        Gson gson = new Gson();
        String jsonString = gson.toJson(sensor);
        this.sensorList = jsonString;
    }

/*    public List<String> getObjectFromString(String jsonString){
        Type listType = new TypeToken<DigitalPhenotypeEvent>(){}.getType();
        DigitalPhenotypeEvent dpe = new Gson().fromJson(jsonString, listType);
        return dpe;
    }*/
}