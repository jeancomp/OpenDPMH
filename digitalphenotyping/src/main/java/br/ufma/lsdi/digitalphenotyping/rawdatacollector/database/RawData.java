package br.ufma.lsdi.digitalphenotyping.rawdatacollector.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import br.ufma.lsdi.cddl.message.Message;

@Entity(tableName = "rawdata")
public class RawData{
    @PrimaryKey(autoGenerate = true)
    public int id;

    String rawdata = null;

    public String getRawdata(){
        return rawdata;
    }

    public void setRawdata(String str){
        this.rawdata = str;
    }

    public void stringFromObject(Message message){
        Gson gson = new Gson();
        String jsonString = gson.toJson(message);
        rawdata = jsonString;
    }

    public Message getObjectFromString(String jsonString){
        Type listType = new TypeToken<Message>(){}.getType();
        Message message = new Gson().fromJson(jsonString, listType);
        return message;
    }
}
