package br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent;

public class Attribute {
    private String label="";
    private String value="";
    private String type="";
    private boolean qualityAttribute=false;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isQualityAttribute() {
        return qualityAttribute;
    }

    public void setQualityAttribute(boolean qualityAttribute) {
        this.qualityAttribute = qualityAttribute;
    }

    public String toString(){
        return "Attribute{" +
                "label=" + getLabel() +
                ", value='" + getValue() + '\'' +
                ", type=" + getType() +
                ", qualityAttribute='" + isQualityAttribute() + '\'' +
                '}';
    }
}

