package br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DigitalPhenotypeEvent {
    private String dataProcessorName = "";
    private String uid = "";
    //private LocalDateTime startDateTime=LocalDateTime.now();;
    //private LocalDateTime endDateTime=LocalDateTime.now();;
    private final List<Attribute> attributesList = new ArrayList();
    private Situation situation;

    public DigitalPhenotypeEvent() {
    }

    public String getDataProcessorName() {
        return dataProcessorName;
    }

    public void setDataProcessorName(String dataProcessorName) {
        this.dataProcessorName = dataProcessorName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Situation getSituation() {
        return situation;
    }

    public void setSituation(Situation situation) {
        this.situation = situation;
    }

    /*public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }*/

    public List<Attribute> getAttributes() {
        return attributesList;
    }

    public void setAttributes(@NonNull String label, @NonNull String value, @NonNull String type, @NonNull boolean qualityAttribute) {
        Attribute attribute = new Attribute();
        attribute.setLabel(label);
        attribute.setValue(value);
        attribute.setType(type);
        attribute.setQualityAttribute(qualityAttribute);
        this.attributesList.add(attribute);

    }

    public String toString() {
        return "DigitalPhenotypeEvent{" +
                "DataProcessorName=" + getDataProcessorName() +
                ", Uid=" + getUid() + '\'' +
                //", StartDateTime=" + getStartDateTime() + '\'' +
                //", EndDateTime='" + getEndDateTime() + '\'' +
                ", Attributes='" + getAttributes().toString() + '\'' +
                ", Situation='" + getSituation().toString() + '\'' +
                '}';
    }
}