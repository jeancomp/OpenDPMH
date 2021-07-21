package br.ufma.lsdi.digitalphenotyping;

public enum Topic {
    J();
    public String NEW_PROCESSOR = "newprocessor";
    public String REMOVE_PROCESSOR = "removeprocessor";
    public String START_PROCESSOR = "startprocessor";
    public String STOP_PROCESSOR = "stoprocessor";
    public String ACTIVE_SENSOR = "activesensor";
    public String DEACTIVATE_SENSOR = "deactivatesensor";
    public String RAW_DATA;
    public String DATA_INFERENCE;
    public String DATA_COMPOSER;
}
