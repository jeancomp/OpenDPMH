package br.ufma.lsdi.digitalphenotyping.phenotypecomposer.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import br.ufma.lsdi.cddl.message.Message;

@Entity(tableName = "phenotypes")
public class Phenotypes{
    @PrimaryKey(autoGenerate = true)
    public int id;

    String phenotype = null;

    public String getPhenotype(){
        return phenotype;
    }

    public void stringFromObject(Message msg){
        Gson gson = new Gson();
        String jsonString = gson.toJson(msg);
        phenotype = jsonString;
    }

    public Message getObjectFromString(String jsonString){
        Type listType = new TypeToken<Message>(){}.getType();
        Message msg = new Gson().fromJson(jsonString, listType);
        return msg;
    }
}
