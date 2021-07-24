package br.ufma.lsdi.digitalphenotyping;

public enum Topic {
    J();
    public String NEW_PROCESSOR = "newprocessor";
    public String REMOVE_PROCESSOR = "removeprocessor";
    public String START_PROCESSOR = "startprocessor";
    public String STOP_PROCESSOR = "stoprocessor";
    public String ACTIVE_SENSOR = "activesensor";
    public String DEACTIVATE_SENSOR = "deactivatesensor";
    public String SUB_AUDIO = "/service_topic/Audio";
    public String SUB_CALL = "Call";
    public String SUB_SMS = "SMS";
    public String RAW_DATA;
    public String DATA_INFERENCE;
    public String DATA_COMPOSER;
}
