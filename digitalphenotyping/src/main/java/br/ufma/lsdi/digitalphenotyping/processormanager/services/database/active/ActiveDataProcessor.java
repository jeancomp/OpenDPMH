package br.ufma.lsdi.digitalphenotyping.processormanager.services.database.active;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "activedataprocessor", indices = @Index(value = {"dataProcessor"}, unique = true))
public class ActiveDataProcessor {
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "dataProcessor")
    public String dataProcessorName = null;
    //public String sensorList = null;

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

    /*public void stringFromObject(List<String> sensor){
        Gson gson = new Gson();
        String jsonString = gson.toJson(sensor);
        this.sensorList = jsonString;
    }*/

/*    public List<String> getObjectFromString(String jsonString){
        Type listType = new TypeToken<DigitalPhenotypeEvent>(){}.getType();
        DigitalPhenotypeEvent dpe = new Gson().fromJson(jsonString, listType);
        return dpe;
    }*/
}