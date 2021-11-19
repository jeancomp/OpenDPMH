package br.ufma.lsdi.digitalphenotyping.dpmanager.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "frameworkstatus", indices = @Index(value = {"status"}, unique = true))
public class FrameworkOnOff {
    @PrimaryKey
    @ColumnInfo(name = "status")
    public Boolean status = null; // value true,false

    public void setStatus(Boolean s) {
        this.status = s;
    }

    public Boolean getStatus(){
        return status;
    }
}