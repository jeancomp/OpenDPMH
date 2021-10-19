package br.ufma.lsdi.digitalphenotyping.dataprocessor.digitalphenotypeevent;

public class Situation {
    private String label="";  // (e.g., Estacionário, Correndo, Andando)
    private String description="";

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString(){
        return "Situation{" +
                "label=" + getLabel() +
                ", description='" + getDescription() + '\'' +
                '}';
    }
}
