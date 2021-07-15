package br.ufma.lsdi.digitalphenotyping;

public enum Topic {
    J;
    private final String ACTIVE_SENSOR;
    private final String DEACTIVATE_SENSOR;
    private final String RAW_DATA;
    private final String DATA_INFERENCE;

    Topic() {
        this.ACTIVE_SENSOR= "activesensor";
        this.DEACTIVATE_SENSOR = "deactivatesensor";
        this.RAW_DATA = "rawdata";
        this.DATA_INFERENCE = "datainference";
    }
}
