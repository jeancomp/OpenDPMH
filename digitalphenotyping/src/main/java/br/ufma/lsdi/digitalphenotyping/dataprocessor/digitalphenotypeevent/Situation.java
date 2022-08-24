package br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent;
import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Situation {
    private String uid = "";
    private String label = "";  // (e.g., Estacionário, Correndo, Andando)
    private String description = "";
    private String dataProcessorName = "";
    private LocalDateTime startDateTime = LocalDateTime.now();

    private LocalDateTime endDateTime = LocalDateTime.now();

    private List<Attribute> attributesList = new ArrayList();

    public void setDataProcessorName(String dataProcessorName) {
        this.dataProcessorName = dataProcessorName;
    }

    public String getDataProcessorName() {
        return dataProcessorName;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setAttributes(@NonNull String label, @NonNull String value, @NonNull String type, @NonNull boolean qualityAttribute) {
        Attribute attribute = new Attribute();
        attribute.setLabel(label);
        attribute.setValue(value);
        attribute.setType(type);
        attribute.setQualityAttribute(qualityAttribute);
        this.attributesList.add(attribute);
    }

    public List<Attribute> getAttributes() {
        return attributesList;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
